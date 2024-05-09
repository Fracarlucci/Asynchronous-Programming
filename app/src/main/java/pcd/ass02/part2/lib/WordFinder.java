package pcd.ass02.part2.lib;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WordFinder {

    private final String wordToFind;


    private final String webAddress;
    private final Document doc;


    /**
     * @param webAddress where to find the word
     * @return the html document
     * @throws IOException if there are errors reading the web page
     */
    public WordFinder(String wordToFind, String webAddress) throws IOException {
        this.wordToFind = wordToFind;
        this.webAddress = webAddress;
        try {
            this.doc = Jsoup.connect(webAddress).get();
        } catch(Exception e) {
            System.out.println("[ERROR]: Can't load the page: " + webAddress);
        }
    }

    public Integer findWord(final String wordToFind) {
        int counter = 0;
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
        return counter;
    }

    public Set<String> findLinks() {
        return doc.getElementsByTag("a")
                .stream()
                .map(l -> l.attr("href"))
                .filter(l -> l.startsWith("http"))
                .collect(Collectors.toSet());
    }

    public String getWebAddress() {
        return webAddress;
    }
}
