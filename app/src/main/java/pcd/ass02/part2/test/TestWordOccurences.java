package pcd.ass02.part2.test;

import pcd.ass02.part2.lib.WordOccurrences;
import pcd.ass02.part2.lib.eventLoop.WordOccurrencesEventLoop;
import pcd.ass02.part2.lib.reactiveThread.WordOccurrencesReactive;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class TestWordOccurences {
  public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

    final String webAddress = "https://www.riminitoday.it/";
    final String wordToFind = "romagna";
    final int depth = 2;
    final int nThreads = Runtime.getRuntime().availableProcessors();


//        final WordOccurrencesEventLoop wordOccEventLoop = new WordOccurrencesEventLoop();
//        final WordOccurrences wordOccVirtualThread = new WordOccurrencesVirtualThread(nThreads);
    final WordOccurrences wordOccReactive = new WordOccurrencesReactive();

    final Map<String, Integer> report = wordOccReactive.getWordOccurences(webAddress, wordToFind, depth);
//     wordOccEventLoop.getWordOccurences(webAddress, wordToFind, depth);
    // For using wordOccEventLoop, comment down below
    final int viewedLinks = report.keySet().size();
    final int wordsFound = report.values().stream().mapToInt(Integer::intValue).sum();

    System.out.println("*************** REPORT ***************");
    System.out.println("Occurrences of \"" + wordToFind + "\" : link");

    report.forEach((k, v) -> System.out.println(v + " : " + k));
    System.out.println("Viewed links: " + viewedLinks);
    System.out.println("Words found: " + wordsFound);
  }
}
