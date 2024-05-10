package pcd.ass02.part2.test;

import pcd.ass02.part2.lib.WordOccurrences;
import pcd.ass02.part2.lib.EventLoop.WordOccurrencesEventLoop;
import pcd.ass02.part2.lib.virtualThread.WordOccurrencesVirtualThread;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class TestWordOccurences {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        final String webAddress = "https://stackoverflow.com/questions/19348248/waiting-on-a-list-of-future";
        final String wordToFind = "future";
        final int depth = 2;
        final int nThreads = Runtime.getRuntime().availableProcessors();

//        final WordOccurrences wordOccEventLoop = new WordOccurrencesEventLoop(nThreads);
        final WordOccurrences wordOccVirtualThread = new WordOccurrencesVirtualThread(nThreads);
      /* final WordOccurrences wordOccReactive = new WordOccurrencesReactive(); */

        final Map<String, Integer> report = wordOccVirtualThread.getWordOccurences(webAddress, wordToFind, depth);
        final int viewedLinks = report.keySet().size();
        final int wordsFound = report.values().stream().mapToInt(Integer::intValue).sum();

        System.out.println("*************** REPORT ***************");
        System.out.println("Occurrences of \"" + wordToFind + "\" : link");

        report.forEach((k,v) -> System.out.println(v + " : " + k));
        System.out.println("Viewed links: " + viewedLinks);
        System.out.println("Words found: " + wordsFound);
    }
}
