package pcd.ass02.part2.lib;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class WordOccurrencesEventLoop implements WordOccurrences {

    private String wordToFind;
    private final Map<String, Integer> map = new HashMap<>();
    private final Set<String> pageLinks = new HashSet<>(); // OR LIST
    Document doc;

    @Override
    public Map<String, Integer> getWordOccurences(final String webAddress, final String wordToFind, final int depth) {
        this.wordToFind = wordToFind;
        pageLinks.add(webAddress);

        for (int i = 0; i <= depth; i++) {
            pageLinks.iterator().forEachRemaining(l -> {
                try {
                    if (l.startsWith("http")) {
                        doc = findWord(l);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            if (i < depth) {
                findLinks(doc);
            }
        }
        return map;
    }

    /**
     *
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
                            counter++;
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
     *
     * @param doc html document in which to search for the links
     */
    private void findLinks(Document doc) {
        pageLinks.clear();
        Elements links = doc.getElementsByTag("a");
        links.forEach(l -> pageLinks.add(l.attr("href")));
    }
}
