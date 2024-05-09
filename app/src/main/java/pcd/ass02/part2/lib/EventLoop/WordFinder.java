package pcd.ass02.part2.lib.EventLoop;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WordFinder {

    private final String wordToFind;
    private final String webAddress;
    private Document doc;

    /**
     * @param webAddress where to find the word
     * @return the html document
     * @throws IOException if there are errors reading the web page
     */
    public WordFinder(final String wordToFind, final String webAddress) {
        this.wordToFind = wordToFind;
        this.webAddress = webAddress;
        try {

        } catch(Exception e) {
            System.out.println("[ERROR]: Can't load the page: " + webAddress);
        }
    }

    public Map.Entry<String, Integer> findWord() throws IOException {
        int counter = 0;
        try {
            this.doc = Jsoup.connect(webAddress).get();
            Elements elements = doc.body().select("*");
            for (Element element : elements) {
                if (Pattern.compile(Pattern.quote(this.wordToFind), Pattern.CASE_INSENSITIVE).matcher(element.ownText()).find()) {
                    String[] words = element.ownText().split("[\\s\\p{Punct}]+");
                    for (String word : words) {
                        if (word.equalsIgnoreCase(this.wordToFind)) {
                            counter++;
                        }
                    }
                }
            }
        } catch(Exception e) {
            System.out.println("[ERROR]: Can't load the page: " + webAddress);
        }
        return new AbstractMap.SimpleEntry<>(webAddress, counter);
    }

    public Set<String> findLinks() {
        return this.doc.getElementsByTag("a")
                .stream()
                .map(l -> l.attr("href"))
                .filter(l -> l.startsWith("http"))
                .collect(Collectors.toSet());
    }

    public String getWebAddress() {
        return this.webAddress;
    }
}
