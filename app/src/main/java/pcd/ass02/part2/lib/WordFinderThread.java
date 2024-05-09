package pcd.ass02.part2.lib;

import java.util.*;

public class WordFinderThread extends Thread {

    private int counter;
    Set<String> foundLinks = new HashSet<>();
    private final Map<String, Integer> map = new HashMap<>();

    private final List<WordFinder> wordFinders;
    private final String wordToFind;

    public WordFinderThread(final String wordToFind) {
        this.wordToFind = wordToFind;
        this.wordFinders = new LinkedList<>();
    }

    public void addWordFinder(final WordFinder wordFinder) {
        this.wordFinders.add(wordFinder);
    }

    @Override
    public void run() {
        for (WordFinder finder : wordFinders) {
            counter = finder.findWord(wordToFind);
            map.put(finder.getWebAddress(), counter);
            foundLinks.addAll(finder.findLinks());
        }
    }
}
