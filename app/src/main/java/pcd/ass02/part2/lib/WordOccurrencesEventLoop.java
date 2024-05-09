package pcd.ass02.part2.lib;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WordOccurrencesEventLoop implements WordOccurrences {

    private final int nThreads;
    private int counter = 0;
    private String wordToFind;
    private final Map<String, Integer> map = new HashMap<>();
    private final Set<String> pageLinks = new HashSet<>();
    Document doc;
    private final List<WordFinderThread> wordFinderThreads;

    public WordOccurrencesEventLoop(int nThreads) {
        this.wordFinderThreads = new LinkedList<>();
        this.nThreads = nThreads;
    }

    public void generateWordFinders(Set<String> webAddresses) {
        this.wordFinderThreads.clear();
        var iterator = webAddresses.iterator();
        final int webAddrPerThread = webAddresses.size() / nThreads;
        int remainingWebAddr = webAddresses.size() % nThreads;

        for (int i = 0; i < nThreads; i++) {
            WordFinderThread wf = new WordFinderThread(wordToFind);
            wordFinderThreads.add(wf);

            IntStream.range(0, webAddrPerThread).forEach(j -> wf.addWordFinder(new WordFinder(wordToFind, iterator.next())));

            if (remainingWebAddr > 0) {
                remainingWebAddr--;
                wf.addWordFinder(new WordFinder(wordToFind, iterator.next()));
            }
        }
    }

    @Override
    public Map<String, Integer> getWordOccurences(final String webAddress, final String wordToFind, final int depth) throws ExecutionException, InterruptedException {
        this.wordToFind = wordToFind;
        pageLinks.add(webAddress);
        Set<String> foundLinks = new HashSet<>();

        for (int i = 0; i <= depth; i++) {
            this.generateWordFinders(pageLinks);
            wordFinderThreads.forEach(Thread::start);
            pageLinks.clear();
            pageLinks.addAll(foundLinks);
        }
        return map;
    }
}
