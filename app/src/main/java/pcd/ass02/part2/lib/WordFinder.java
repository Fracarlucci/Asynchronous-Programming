package pcd.ass02.part2.lib;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

public class WordFinder {

    private final String wordToFind;


    private final String webAddress;
    private final Document doc;


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
        final Set<String> foundLinks = new HashSet<>();
        final Elements links = doc.getElementsByTag("a");

        links.forEach(l -> foundLinks.add(l.attr("href")));
        return foundLinks;
    }

    public String getWebAddress() {
        return webAddress;
    }
}
