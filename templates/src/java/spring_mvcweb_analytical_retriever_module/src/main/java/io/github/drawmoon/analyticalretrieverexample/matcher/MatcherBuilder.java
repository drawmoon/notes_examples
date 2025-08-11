package io.github.drawmoon.analyticalretrieverexample.matcher;

import io.github.drawmoon.analyticalretrieverexample.common.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MatcherBuilder<T> {
  // TODO Use caffeine cache
  private final Map<Integer, Sequence<T>> cache = new ConcurrentHashMap<>();

  protected final Sequence<T> seq;

  public MatcherBuilder(Sequence<T> seq) {
    this.seq = seq;
  }

  public MatcherBuilder<T> apply(Matcher<T> matcher) {
    Sequence<T> newSeq = this.seq;
    int key = Objects.hash(System.identityHashCode(seq), System.identityHashCode(matcher));

    if (cache.containsKey(key)) {
      log.info("Matcher cache hit");
      newSeq = cache.get(key);
    } else {
      log.info("Matcher cache miss");
      newSeq = seq.filter(matcher);
      cache.put(key, newSeq);
    }

    return new MatcherBuilder<>(newSeq);
  }

  public Sequence<T> finish() {
    return seq;
  }

  public Iterator<T> iterator() {
    return seq.iterator();
  }
}
