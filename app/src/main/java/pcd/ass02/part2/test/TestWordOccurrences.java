package pcd.ass02.part2.test;

import pcd.ass02.part2.lib.WordOccurrences;
import pcd.ass02.part2.lib.eventLoop.WordOccurrencesEventLoop;
import pcd.ass02.part2.lib.reactiveThread.WordOccurrencesReactive;
import pcd.ass02.part2.lib.virtualThread.WordOccurrencesVirtualThread;

import java.util.Map;

public class TestWordOccurrences {
    public static void main(String[] args) {

        final String webAddress = "https://www.riminitoday.it/"; // https://fracarlucci.github.io/RancorRank
        final String wordToFind = "romagna";
        final int depth = 3;

//        final WordOccurrencesEventLoop wordOccEventLoop = new WordOccurrencesEventLoop();
        final WordOccurrences wordOccVirtualThread = new WordOccurrencesVirtualThread();
        final WordOccurrences wordOccReactive = new WordOccurrencesReactive();

//        wordOccEventLoop.getWordOccurences(webAddress, wordToFind, depth);
        final Map<String, Integer> report = wordOccVirtualThread.getWordOccurences(webAddress, wordToFind, depth);
        final int viewedLinks = report.keySet().size();
        final int wordsFound = report.values().stream().mapToInt(Integer::intValue).sum();
//
        System.out.println("*************** REPORT ***************");
        System.out.println("Occurrences of \"" + wordToFind + "\" : link");
//
        report.forEach((k,v) -> System.out.println(v + " : " + k));
        System.out.println("Links: " + viewedLinks);
        System.out.println("Words found: " + wordsFound);
    }
}
