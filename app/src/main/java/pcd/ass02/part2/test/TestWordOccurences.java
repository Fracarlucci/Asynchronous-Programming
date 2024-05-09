package pcd.ass02.part2.test;

import pcd.ass02.part2.lib.WordOccurrences;
import pcd.ass02.part2.lib.WordOccurrencesEventLoop;
import java.util.Map;

public class TestWordOccurences {
    public static void main(String[] args) {

        final String webAddress = "http://bias.csr.unibo.it/maltoni/ml/";
        final String wordToFind = "hello";
        final int depth = 1;
        final int nThreads = Runtime.getRuntime().availableProcessors();

        final WordOccurrences wordOccEventLoop = new WordOccurrencesEventLoop(nThreads);
//        final WordOccurrences wordOccVirtualThread = new WordOccurrencesVirtualThread();
//        final WordOccurrences wordOccReactive = new WordOccurrencesReactive();

        final Map<String, Integer> report = wordOccEventLoop.getWordOccurences(webAddress, wordToFind, depth);
        final int viewedLinks = report.keySet().size();
        final int wordsFound = report.values().stream().mapToInt(Integer::intValue).sum();

        System.out.println("*************** REPORT ***************");
        System.out.println("Occurrences of \"" + wordToFind + "\" : link");

        report.forEach((k,v) -> System.out.println(v + " : " + k));
        System.out.println("Viewed links: " + viewedLinks);
        System.out.println("Words found: " + wordsFound);
    }
}
