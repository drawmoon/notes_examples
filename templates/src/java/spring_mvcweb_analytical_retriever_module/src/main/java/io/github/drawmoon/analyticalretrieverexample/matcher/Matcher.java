package io.github.drawmoon.analyticalretrieverexample.matcher;

import java.util.function.Predicate;

@FunctionalInterface
public interface Matcher<T> extends Predicate<T> {}
