package io.github.drawmoon.analyticalretrieverexample.common;

/** Represents a 1-tuple, or singleton. */
public final class Tuple<T> {

  private final T item;

  /**
   * Constructs.
   *
   * @param item the value of the current tuple object's single component.
   */
  public Tuple(T item) {
    this.item = item;
  }

  /**
   * Gets the value of the tuple object's single component.
   *
   * @return the value of the tuple
   */
  public T item() {
    return item;
  }
}
