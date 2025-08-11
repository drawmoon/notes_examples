package io.github.drawmoon.analyticalretrieverexample.common;

/** Represents an 2-tuple, or pair. */
public final class BiTuple<T1, T2> {

  private final T1 item1;
  private final T2 item2;

  /**
   * Constructs.
   *
   * @param item1 the value of the current tuple object's first component
   * @param item2 the value of the current tuple object's second component
   */
  public BiTuple(T1 item1, T2 item2) {
    this.item1 = item1;
    this.item2 = item2;
  }

  /**
   * Gets the value of the current tuple object's first component.
   *
   * @return the value of the tuple
   */
  public T1 item1() {
    return item1;
  }

  /**
   * Gets the value of the current tuple object's second component.
   *
   * @return the value of the tuple
   */
  public T2 item2() {
    return item2;
  }
}
