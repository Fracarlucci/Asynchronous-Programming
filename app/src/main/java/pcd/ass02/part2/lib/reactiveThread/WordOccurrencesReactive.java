package pcd.ass02.part2.lib.reactiveThread;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pcd.ass02.part2.lib.EventLoop.Monitor;
import pcd.ass02.part2.lib.EventLoop.WordFinderThread;
import pcd.ass02.part2.lib.WordOccurrences;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WordOccurrencesReactive implements WordOccurrences {
  private int counter = 0;
  private String wordToFind;


  private final Map<String, Integer> map = new HashMap<>();
  private final Set<String> pageLinks = new HashSet<>();
  Document doc;
  //    private final Deque<Event> events;
  private Monitor counterMonitor = new Monitor();
  private Monitor linksMonitor = new Monitor();

  @Override
  public Map<String, Integer> getWordOccurences(final String webAddress, final String wordToFind, final int depth) throws ExecutionException, InterruptedException, IOException {
    this.wordToFind = wordToFind;
    pageLinks.add(webAddress);
    final List<Future<Map.Entry<String, Integer>>> wordFutureList = new ArrayList<>();
    final List<Future<Set<String>>> linkFutureList = new ArrayList<>();
    Set<String> foundLinks = new HashSet<>();


    for (int i = 0; i <= depth; i++) {
      int finalI = i;
      @NonNull Flowable<Set<String>> obs2 = Flowable.fromCallable(() -> {
        Set<String> depthlinks = new HashSet<>();
        pageLinks.forEach(address -> {
          Set<String> links = new HashSet<>();
          try {
            final Document doc = Jsoup.connect(address).get();
            links = findLinks(doc);
            final Integer counter = findWord(doc);
            if(counter != 0) {
              map.put(address, counter);
            }
            depthlinks.addAll(links);
          } catch (Exception e) {
            System.out.println(e);
          }
        });
        System.out.println("Level: " + finalI + ", completed.");
        return depthlinks;
      }).observeOn(Schedulers.io());
      obs2.blockingSubscribe(res -> {
        foundLinks.addAll(res);
        pageLinks.clear();
        pageLinks.addAll(res);
        System.out.println("Done.");
      });
    }

    return getMap();
  }

  public Map<String, Integer> getMap() {
    this.counterMonitor.requestRead();
    final Map<String, Integer> map = this.map;
    this.counterMonitor.releaseRead();
    return map;
  }

  private Set<String> findLinks(final Document document) {
    return document.getElementsByTag("a")
            .stream()
            .map(l -> l.attr("href"))
            .filter(l -> l.startsWith("http"))
            .collect(Collectors.toSet());
  }

  private Integer findWord(final Document document) {
    counter = 0;
    Elements elements = document.body().select("*");
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
    return counter;
  }
}
