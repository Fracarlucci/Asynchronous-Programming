package pcd.ass02.part2.lib.virtualThread;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

public class WordFinderFuture implements Callable<Map.Entry<String, Integer>> {

  private final String wordToFind;
  private final String webAddress;
  private Document doc;
  private Integer counter;

  public WordFinderFuture(String wordToFind, String webAddress, Document doc) {
    this.wordToFind = wordToFind;
    this.webAddress = webAddress;
    this.doc = doc;
    this.counter = 0;
  }

  @Override
  public Map.Entry<String, Integer> call() throws Exception {
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
}
