package pcd.ass02.part2.lib;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class WordOccurrencesEventLoop implements WordOccurrences {
    @Override
    public Map<String, Integer> getWordOccurences(final String webAddress, final String wordToFind, final int depth) throws IOException {
        int counter = 0;
        Map<String, Integer> map = new HashMap<>();
        Document doc = Jsoup.connect(webAddress).get();
        Elements elements = doc.body().select("*");

        for (Element element : elements) {
            if ( Pattern.compile(Pattern.quote(wordToFind), Pattern.CASE_INSENSITIVE).matcher(element.ownText()).find()) {
                counter++;
                map.put(webAddress, counter);
            }
        }
        return map;
    }
}
