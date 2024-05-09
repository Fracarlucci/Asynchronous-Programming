package pcd.ass02.part2.lib.EventLoop;

import org.jsoup.nodes.Document;
import pcd.ass02.part2.lib.WordOccurrences;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class WordOccurrencesEventLoop implements WordOccurrences {

    private final int nThreads;
    private int counter = 0;
    private String wordToFind;


    private final Map<String, Integer> map = new HashMap<>();
    private final Set<String> pageLinks = new HashSet<>();
    Document doc;
    private final List<WordFinderThread> wordFinderThreads;
//    private final Deque<Event> events;
    private Monitor counterMonitor = new Monitor();
    private Monitor linksMonitor = new Monitor();

    public WordOccurrencesEventLoop(int nThreads) {
//        this.events = new ArrayDeque<>();
        this.wordFinderThreads = new LinkedList<>();
        this.nThreads = nThreads;
    }

    public void generateWordFinders(Set<String> webAddresses) throws IOException {
        this.wordFinderThreads.clear();
        var iterator = webAddresses.iterator();
        final int webAddrPerThread = webAddresses.size() / nThreads;
        int remainingWebAddr = webAddresses.size() % nThreads;

        for (int i = 0; i < nThreads; i++) {
            WordFinderThread wf = new WordFinderThread(this.map, this.pageLinks, this.counterMonitor, this.linksMonitor);
            wordFinderThreads.add(wf);

            IntStream.range(0, webAddrPerThread).forEach(j -> wf.addWordFinder(new WordFinder(wordToFind, iterator.next())));
            if (remainingWebAddr > 0) {
                remainingWebAddr--;
                wf.addWordFinder(new WordFinder(wordToFind, iterator.next()));
            }
        }
    }

    @Override
    public Map<String, Integer> getWordOccurences(final String webAddress, final String wordToFind, final int depth) throws ExecutionException, InterruptedException, IOException {
        this.wordToFind = wordToFind;
        pageLinks.add(webAddress);
        Set<String> foundLinks = new HashSet<>();
//        Event event;

        for (int i = 0; i <= depth; i++) {
                this.generateWordFinders(pageLinks);
                wordFinderThreads.forEach(Thread::start);
                this.linksMonitor.requestWrite();
//                pageLinks.clear();
                pageLinks.addAll(foundLinks);
                this.linksMonitor.releaseWrite();
//            event = events.poll();
//            if (event != null) {
//                System.out.println("EVENTO RICEVUTO");
//                System.out.println(this.getCounter());
//            }
        }

        return getMap();
    }

    private int getCounter() {
        this.counterMonitor.requestRead();
        final int count = this.counter;
        this.counterMonitor.releaseRead();
        return count;
    }
    public Map<String, Integer> getMap() {
        this.counterMonitor.requestRead();
        final Map<String, Integer> map = this.map;
        this.counterMonitor.releaseRead();
        return map;
    }
}
