package pcd.ass02.part2.lib.virtualThread;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pcd.ass02.part2.lib.EventLoop.Monitor;
import pcd.ass02.part2.lib.EventLoop.WordFinder;
import pcd.ass02.part2.lib.EventLoop.WordFinderThread;
import pcd.ass02.part2.lib.WordOccurrences;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WordOccurrencesVirtualThread implements WordOccurrences {
  private final int nThreads;
  private int counter = 0;
  private String wordToFind;


  private final Map<String, Integer> map = new HashMap<>();
  private final Set<String> pageLinks = new HashSet<>();
  Document doc;
  private final List<WordFinderThread> wordFinderThreads;
  //    private final Deque<Event> events;
  private Monitor counterMonitor = new Monitor();
  private Monitor linksMonitor = new Monitor();

  public WordOccurrencesVirtualThread(int nThreads) {
//        this.events = new ArrayDeque<>();
    this.wordFinderThreads = new LinkedList<>();
    this.nThreads = nThreads;
  }

  @Override
  public Map<String, Integer> getWordOccurences(final String webAddress, final String wordToFind, final int depth) throws ExecutionException, InterruptedException, IOException {
    this.wordToFind = wordToFind;
    pageLinks.add(webAddress);
    final List<Future<Map.Entry<String, Integer>>> wordFutureList = new ArrayList<>();
    final List<Future<Set<String>>> linkFutureList = new ArrayList<>();
    Set<String> foundLinks = new HashSet<>();


    for (int i = 0; i < depth; i++) {
      final ExecutorService executor = Executors.newFixedThreadPool(pageLinks.size());

      pageLinks.forEach(page -> {
        linkFutureList.add(executor.submit(new LinkFinderFuture(page)));
      });

      linkFutureList.forEach(future -> {
        try {
          final Set<String> links = future.get();
          System.out.println(future + ": link trovati = " + links.size());
          foundLinks.addAll(links);
        } catch (Exception e) {
          System.out.println("Failed to run future");
        }
      });

//      pageLinks.iterator().forEachRemaining(l -> {
//        try {
//
//          if (!map.containsKey(l)) {
//            doc = Jsoup.connect(l).get();
//            foundLinks.addAll(findLinks());
//            map.put(l, 0);
//          }
//        } catch (IOException e) {
//          System.out.println("A website failed to connect");
//        }
//      });
      pageLinks.clear();
      pageLinks.addAll(foundLinks);
      executor.shutdown();
    }

    // I need to add again the first address because pageLink.clear() delete it
    pageLinks.add(webAddress);
    final ExecutorService executor = Executors.newFixedThreadPool(pageLinks.size());

    pageLinks.forEach(page -> {
      wordFutureList.add(executor.submit(new WordFinderFuture(wordToFind, page, doc)));
    });

    wordFutureList.forEach(future -> {
      try {
        final Map.Entry<String, Integer> entry = future.get();
        if (entry.getValue() != 0) {
          System.out.println("Future n. " + future + ": " + entry.getValue());
          map.put(entry.getKey(), entry.getValue());
        }
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    });

    executor.shutdown();
    return getMap();
  }

  public Map<String, Integer> getMap() {
    this.counterMonitor.requestRead();
    final Map<String, Integer> map = this.map;
    this.counterMonitor.releaseRead();
    return map;
  }
}
