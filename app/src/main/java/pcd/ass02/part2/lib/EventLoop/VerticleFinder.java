package pcd.ass02.part2.lib.EventLoop;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VerticleFinder extends AbstractVerticle {

    private final Map<String, Integer> map = new HashMap<>();
    private final Set<String> pageLinks = new HashSet<>();
    private final int depth;
    private String webAddress;
    private int pageToVisit = 1;
    private String wordToFind;
    private final Consumer<Map<String, Integer>> result;

    public VerticleFinder(final String webAddress, final String wordToFind, final int depth, Consumer<Map<String, Integer>> result) {
        this.wordToFind = wordToFind;
        this.depth = depth;
        this.webAddress = webAddress;
        this.result = result;
    }

    public void start(final Promise<Void> promise) throws IOException {
        int actualDepth = 0;
        pageLinks.add(webAddress);
        log("START");
        this.getVertx().eventBus().consumer("word-found", message -> {
            computeWordFound(message.body().toString(), promise);
        });
        this.findWord(webAddress, promise, actualDepth);
    }

    private void computeWordFound(final String webAddress, final Promise<Void> promise) {
        this.map.put(webAddress, this.map.get(webAddress) == null ? 1 : this.map.get(webAddress) + 1);
        result.accept(map);
    }

    /*
     * @param webAddress where to find the word
     * @return the html document
     * @throws IOException if there are errors reading the web page
     */
    private void findWord(String webAddress, Promise<Void> promise, int actualDepth) throws IOException {
        Callable<Document> call = () -> {
            try {
                return Jsoup.connect(webAddress).get();
            } catch (Exception e) {
                log("ERROROR");
                return null;
            }
        };

        getVertx().executeBlocking(call)
                .onComplete(res -> {
                    try {
                        pageToVisit--;
                        Document doc = res.result();
                        if (doc == null) {
                            return;
                        }
                        Elements elements = doc.body().select("*");
                        for (Element element : elements) {
                            if (Pattern.compile(Pattern.quote(wordToFind), Pattern.CASE_INSENSITIVE).matcher(element.ownText()).find()) {
                                String[] words = element.ownText().split("[\\s\\p{Punct}]+");
                                for (String word : words) {
                                    if (word.equalsIgnoreCase(wordToFind)) {
                                        this.getVertx().eventBus().publish("word-found", webAddress);
                                    }
                                }
                            }
                        }
                        if (actualDepth < depth) {
                            findLinks(doc, promise, actualDepth + 1);
                        }
                    } finally {
                        if (pageToVisit == 0 && actualDepth == depth) {
                            promise.complete();
                        }
                    }
                });
    }

    public void findLinks(final Document doc, Promise<Void> promise, int actualDepth) {
        var newLinks = doc.getElementsByTag("a")
                .stream()
                .map(l -> l.attr("href"))
                .filter(l -> l.startsWith("http"))
                .collect(Collectors.toSet());
        this.pageLinks.clear();
        this.pageLinks.addAll(newLinks);

        pageToVisit = pageLinks.size();
        pageLinks.forEach(pl -> {
            try {
                if (!map.containsKey(pl)) {
                    findWord(pl, promise, actualDepth);
                }
            } catch (IOException e) {
                System.out.println("ERRORR");
            }
        });
    }


    private void log(final String str) {
        System.out.println("[MAIN LOOP]: " + str);
    }
}
