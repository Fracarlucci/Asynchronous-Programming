package pcd.ass02.part2.lib.virtualThread;

import org.jsoup.nodes.Document;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class LinkFinderFuture implements Callable<Set<String>> {

  private final String webAddress;
  private final Document doc;
  private Set<String> links;

  public LinkFinderFuture(String webAddress, Document doc) {
    this.webAddress = webAddress;
    this.doc = doc;
  }

  @Override
  public Set<String> call() throws Exception {

    return Set.of();
  }

  private Set<String> findLinks() {
    return this.doc.getElementsByTag("a")
            .stream()
            .map(l -> l.attr("href"))
            .filter(l -> l.startsWith("http"))
            .collect(Collectors.toSet());
  }
}
