package pcd.ass02.part2.test;

import pcd.ass02.part2.lib.WordOccurrences;
import pcd.ass02.part2.lib.WordOccurrencesEventLoop;

import java.io.IOException;
import java.util.Map;

public class TestWordOccurences {
    public static void main(String[] args) throws IOException {

        final String webAddress = "https://it.wikipedia.org/wiki/Pagina_principale";
        final String wordToFind = "pizzeria";
        final int depth = 3;

        final WordOccurrences wordOccEventLoop = new WordOccurrencesEventLoop();
//        final WordOccurrences wordOccVirtualThread = new WordOccurrencesVirtualThread();
//        final WordOccurrences wordOccReactive = new WordOccurrencesReactive();

        final Map<String, Integer> report = wordOccEventLoop.getWordOccurences(webAddress, wordToFind, depth);

        report.forEach((k,v) -> System.out.println(k + " has " + v + " occurrences of \"" + wordToFind + "\""));
    }
}
