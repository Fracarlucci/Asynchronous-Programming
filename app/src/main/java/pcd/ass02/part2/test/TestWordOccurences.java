package pcd.ass02.part2.test;

import pcd.ass02.part2.lib.WordOccurrences;
import pcd.ass02.part2.lib.WordOccurrencesEventLoop;
import java.util.Map;

public class TestWordOccurences {
    public static void main(String[] args) {

        final String webAddress = "http://bias.csr.unibo.it/maltoni/ml/";
        final String wordToFind = "hello";
        final int depth = 1;

        final WordOccurrences wordOccEventLoop = new WordOccurrencesEventLoop();
//        final WordOccurrences wordOccVirtualThread = new WordOccurrencesVirtualThread();
//        final WordOccurrences wordOccReactive = new WordOccurrencesReactive();

        final Map<String, Integer> report = wordOccEventLoop.getWordOccurences(webAddress, wordToFind, depth);

        System.out.println("*************** REPORT ***************");
        System.out.println("Occurrences of \"" + wordToFind + "\" : link");

        report.forEach((k,v) -> System.out.println(v + " : " + k));
        System.out.println("Viewed links: " + report.keySet().size());
        System.out.println("Words found: " + report.values().stream().mapToInt(Integer::intValue).sum());
    }
}
