package pcd.ass02.part2.lib.EventLoop;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pcd.ass02.part2.lib.WordOccurrences;
import io.vertx.core.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WordOccurrencesEventLoop extends AbstractVerticle implements WordOccurrences {

  //    private final int nThreads;
  private int counter = 0;
  private String wordToFind;

//    Map<> result;

  private Map<String, Integer> result = new HashMap<>();
  private final Set<String> pageLinks = new HashSet<>();
  Document doc;

  @Override
  public Map<String, Integer> getWordOccurences(final String webAddress, final String wordToFind, final int depth) {
    Vertx vertx = Vertx.vertx();
    Future<String> future = vertx.deployVerticle(new VerticleFinder(webAddress, wordToFind, depth, res -> {
              System.out.println("RESULT " + res); // Ogni occorenza viene stampata
              result.putAll(res);
            }));

    while (!future.isComplete()) {
      System.out.println("Waiting for future...");
    }
    System.out.println(future.result());
    vertx.close();
    return result;
  }
}
