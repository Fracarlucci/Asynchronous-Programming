package pcd.ass02.part2.test;

import pcd.ass02.part2.lib.WordOccurrences;
import pcd.ass02.part2.lib.WordOccurrencesEventLoop;
import pcd.ass02.part2.lib.WordOccurrencesReactive;
import pcd.ass02.part2.lib.WordOccurrencesVirtualThread;

public class TestWordOccurences {
    public static void main(String[] args) {

        final WordOccurrences wordOccurrencesEventLoop = new WordOccurrencesEventLoop();
        final WordOccurrences wordOccurrencesVirtualThread = new WordOccurrencesVirtualThread();
        final WordOccurrences wordOccurrencesReactive = new WordOccurrencesReactive();
    }
}
