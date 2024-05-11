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
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VerticleFinder extends AbstractVerticle {

    private final Map<String, Integer> map = new HashMap<>();
    private final Set<String> pageLinks = new HashSet<>();
    private final int depth;
    private String webAddress;
    private int actualDepth = 0;
    private String wordToFind;
    private final Consumer<Map<String, Integer>> result;

    public VerticleFinder(final String webAddress, final String wordToFind, final int depth, Consumer<Map<String, Integer>> result) {
        this.wordToFind = wordToFind;
        this.depth = depth;
        this.webAddress = webAddress;
        this.result = result;
    }

    public void start(final Promise<Void> promise) throws IOException {
        pageLinks.add(webAddress);

        log("START");
        this.getVertx().eventBus().consumer("word-found", message -> {
            computeWordFound(message.body().toString(), promise);
        });

        this.getVertx().eventBus().consumer("links-found", message -> {
            addLinks((Set<String>) message.body(), promise);
        });
        findWord(webAddress, promise);
    }

    private void addLinks(final Set<String> newLinks, final Promise<Void> promise) {
        this.pageLinks.clear();
        this.pageLinks.addAll(newLinks);
        pageLinks.forEach(pl -> {
            try {
                findWord(pl, promise);
            } catch (IOException e) {
                System.out.println("ERRORR");
            }
        });
        this.actualDepth++;
    }

    private void computeWordFound(final String webAddress, final Promise<Void> promise) {
        this.map.put(webAddress, this.map.get(webAddress) == null ? 1 : this.map.get(webAddress) + 1);
    }

     /*
     * @param webAddress where to find the word
     * @return the html document
     * @throws IOException if there are errors reading the web page
     */
    private void findWord(String webAddress, Promise<Void> promise) throws IOException {
        try {
            Document doc = Jsoup.connect(webAddress).timeout(2000).get();
            Elements elements = doc.body().select("*");
            for (Element element : elements) {
                if (Pattern.compile(Pattern.quote(wordToFind), Pattern.CASE_INSENSITIVE).matcher(element.ownText()).find()) {
                    String[] words = element.ownText().split("[\\s\\p{Punct}]+");
                    for (String word : words) {
                        if (word.equalsIgnoreCase(wordToFind)) {
                            vertx.eventBus().publish("word-found", webAddress);
                        }
                    }
                }
            }
            if (actualDepth < depth) {
                vertx.eventBus().publish("links-found", findLinks(doc));
            } else {
                promise.future().onComplete((fut) -> {
                    result.accept(map);
                });
                promise.complete();
            }
        } catch (Exception e) {
            System.out.println("[ERROR]: Can't load the page: " + webAddress);
        }
    }

    public Set<String> findLinks(final Document doc) {
        return doc.getElementsByTag("a")
                .stream()
                .map(l -> l.attr("href"))
                .filter(l -> l.startsWith("http"))
                .collect(Collectors.toSet());
    }


    private void log(final String str) {
        System.out.println("[MAIN LOOP]: " + str);
    }
}
