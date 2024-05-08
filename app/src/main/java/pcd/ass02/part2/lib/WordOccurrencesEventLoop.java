package pcd.ass02.part2.lib;

import com.google.common.base.CharMatcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class WordOccurrencesEventLoop implements WordOccurrences {

    private int counter;
    private String wordToFind;
    private final Map<String, Integer> map = new HashMap<>();
    //    private final List<String> pageLinks = new LinkedList<>();
    private Elements links;
    Document doc;

    @Override
    public Map<String, Integer> getWordOccurences(final String webAddress, final String wordToFind, final int depth) throws IOException {
        this.wordToFind = wordToFind;

        for (int i = 0; i < depth; i++) {
            doc = findWord(webAddress);
            findLinks(doc);
        }
        doc = findWord(webAddress);
        return map;
    }

    private Document findWord(String webAddress) throws IOException {
        try {
            doc = Jsoup.connect(webAddress).get();
            Elements elements = doc.body().select("*");
            counter = 0;

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
        } catch(Exception e){
            System.out.println("[ERROR]: Can't load the page!");
        }
        map.put(webAddress, counter);
        return doc;
    }

    private void findLinks(Document doc) {
        links = doc.getElementsByTag("a");
        links.iterator().forEachRemaining(l -> {
            try {
                if( (l.attr("href")).startsWith("http")) {
                    findWord(l.attr("href"));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
