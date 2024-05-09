package pcd.ass02.part2.lib.EventLoop;

import java.io.IOException;
import java.util.*;

public class WordFinderThread extends Thread {

    private final Map<String, Integer> map;
    private final Set<String> foundLinks;
    private final List<WordFinder> wordFinders;
    private final Monitor counterMonitor;
    private final Monitor linksMonitor;
    // TODO FIX FOUNDLINKS var
    public WordFinderThread(Map<String, Integer> map, Set<String> foundLinks, final Monitor counterMonitor, final Monitor linksMonitor) {
        this.map = map;
        this.foundLinks = foundLinks;
        this.counterMonitor = counterMonitor;
        this.linksMonitor = linksMonitor;
        this.wordFinders = new LinkedList<>();
    }

    public void addWordFinder(final WordFinder wordFinder) {
        this.wordFinders.add(wordFinder);
    }

    @Override
    public void run() {
        for (final WordFinder finder : wordFinders) {
            this.counterMonitor.requestWrite();
            try {
                this.map.put(finder.findWord().getKey(), finder.findWord().getValue());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.counterMonitor.releaseWrite();
            this.linksMonitor.requestWrite();
            foundLinks.addAll(finder.findLinks());
            this.linksMonitor.releaseWrite();
        }
    }
}
