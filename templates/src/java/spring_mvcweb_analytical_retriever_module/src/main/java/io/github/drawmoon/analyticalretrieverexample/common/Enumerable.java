package io.github.drawmoon.analyticalretrieverexample.common;

import java.util.ArrayList;
import java.util.function.Function;
import javax.annotation.Nonnull;

/**
 * An interface for inherently recursive, multi-valued data structures. The order of elements is
 * determined by {@link Iterable#iterator()}, which may vary each time it is called.
 */
public interface Enumerable<T> extends Iterable<T> {

  /**
   * Collects all elements that are in the domain of the given <code>action</code> by mapping the
   * elements to type <code>R</code>.
   *
   * @param <R> the type of the result
   * @param function a function that is not necessarily defined of all elements of this traversable
   * @return a new <code>Traversable</code> instance containing elements of type <code>R</code>
   */
  @Nonnull
  <R> Enumerable<R> collect(Function<? super T, ? extends R> function);

  /**
   * Returns a list containing all elements of this collection.
   *
   * @return the new list
   */
  @Nonnull
  ArrayList<T> toList();
}
