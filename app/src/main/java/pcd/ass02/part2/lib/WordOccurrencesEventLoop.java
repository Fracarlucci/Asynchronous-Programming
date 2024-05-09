package pcd.ass02.part2.lib;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WordOccurrencesEventLoop implements WordOccurrences {

    private final int nThreads;
    private int counter = 0;
    private String wordToFind;
    private final Map<String, Integer> map = new HashMap<>();
    private final Set<String> pageLinks = new HashSet<>();
    //    private final List<String> pageLinks = new LinkedList<>();
    Document doc;
    private final List<WordFinderThread> wordFinderThreads;

    public WordOccurrencesEventLoop(int nThreads) {
        this.wordFinderThreads = new LinkedList<>();
        this.nThreads = nThreads;
    }

    public void generateWordFinders(Set<String> webAddresses) {
        this.wordFinderThreads.clear();
        var iterator = webAddresses.iterator();
        final int webAddrPerThread = webAddresses.size() / nThreads;
        int remainingWebAddr = webAddresses.size() % nThreads;

        for (int i = 0; i < nThreads; i++) {

            WordFinderThread wf = new WordFinderThread(wordToFind);
            wordFinderThreads.add(wf);

            IntStream.range(0, webAddrPerThread).forEach(j -> wf.addWordFinder(new WordFinder(wordToFind, iterator.next())));

            if (remainingWebAddr > 0) {
                remainingWebAddr--;
                wf.addWordFinder(new WordFinder(wordToFind, iterator.next()));
            }
        }
    }

    @Override
    public Map<String, Integer> getWordOccurences(final String webAddress, final String wordToFind, final int depth) throws ExecutionException, InterruptedException {
        this.wordToFind = wordToFind;
        pageLinks.add(webAddress);
        Set<String> foundLinks = new HashSet<>();

        for (int i = 0; i <= depth; i++) {
            this.generateWordFinders(pageLinks);

            pageLinks.iterator().forEachRemaining(l -> {
                try {
                    if (l.startsWith("http") && !map.containsKey(l)) {
                        doc = findWord(l);
                        foundLinks.addAll(findLinks(doc));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            pageLinks.clear();
            pageLinks.addAll(foundLinks);
        }
        return map;
    }

    /**
     * @param webAddress where to find the word
     * @return the html document
     * @throws IOException if there are errors reading the web page
     */
    private Document findWord(String webAddress) throws IOException {
        int counter = 0;
        try {
            doc = Jsoup.connect(webAddress).get();
            Elements elements = doc.body().select("*");

            for (Element element : elements) {
                if (Pattern.compile(Pattern.quote(wordToFind), Pattern.CASE_INSENSITIVE).matcher(element.ownText()).find()) {
                    String[] words = element.ownText().split("[\\s\\p{Punct}]+");
                    for (String word : words) {
                        if (word.equalsIgnoreCase(wordToFind)) {
                            counter++; //monitor.write
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("[ERROR]: Can't load the page: " + webAddress);
        }
        map.put(webAddress, counter);
        return doc;
    }

    /**
     * @param doc html document in which to search for the links
     */
    private Set<String> findLinks(Document doc) {
        final Set<String> foundLinks = new HashSet<>();
        final Elements links = doc.getElementsByTag("a");

//        links.forEach(l -> foundLinks.add(l.attr("href")));
//        foundLinks.stream().filter(l -> l.startsWith("http"));
//        return foundLinks;

        return links.stream()
                .map(l -> l.attr("href"))  // Get the href attribute of each element
                .filter(l -> l.startsWith("http"))  // Filter out links that start with "http"
                .collect(Collectors.toSet());  // Collect the filtered links into a list

    }
}
