package io.github.drawmoon.analyticalretrieverexample.common;

import static com.google.common.base.Preconditions.*;

import com.google.common.collect.AbstractIterator;
import com.google.errorprone.annotations.ForOverride;
import java.io.Serializable;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import javax.annotation.*;

/** Utility methods pertaining to Collection instances. */
public final class Sequence<T> implements Enumerable<T>, Serializable {

  private static final long serialVersionUID = -8492251942206794476L;

  private final Iterable<T> itr;
  private final boolean parallel;

  /**
   * Constructs.
   *
   * @param itr the iterator
   */
  private Sequence(@CheckForNull Iterable<T> itr) {
    this(itr, false);
  }

  /**
   * Constructs.
   *
   * @param itr the iterator
   * @param parallel whether to use parallel
   */
  private Sequence(@CheckForNull Iterable<T> itr, boolean parallel) {
    this.itr = checkNotNull(itr);
    this.parallel = parallel;
  }

  /**
   * Returns an empty Sequence.
   *
   * @param <T> the type of elements in the list
   * @return the new sequence
   */
  public static <T> Sequence<T> empty() {
    return it(
        new Iterator<T>() {
          @Override
          public boolean hasNext() {
            return false;
          }

          @Override
          public T next() {
            throw new NoSuchElementException();
          }
        });
  }

  /**
   * Returns a sequential ordered Sequence from start (inclusive) to end (inclusive) by an
   * incremental step of 1.
   *
   * @param start the (inclusive) initial value
   * @param end the inclusive upper bound
   * @return the new sequence
   */
  public static Sequence<Integer> range(int start, int end) {
    return it(new RangeIntTransform(start, end));
  }

  /**
   * Creates a Sequence from a list of elements.
   *
   * @param <T> the type of elements in the list
   * @param itr the elements
   * @return the new sequence
   */
  public static <T> Sequence<T> it(Iterator<T> itr) {
    return it(() -> itr);
  }

  /**
   * Creates a Sequence from a list of elements.
   *
   * @param <T> the type of elements in the list
   * @param itr the elements
   * @return the new sequence
   */
  public static <T> Sequence<T> it(Iterable<T> itr) {
    return new Sequence<>(itr);
  }

  /**
   * Returns a parallel Sequence.
   *
   * @return the new sequence
   */
  public Sequence<T> asParallel() {
    return new Sequence<>(this.itr, true);
  }

  /**
   * Returns a {@link Sequence} containing only elements matching the given predicate.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("apple", "banana", "pear");
   * Sequence<String> seq = Sequence.it(list).filter(s -> s.startsWith("a"));
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code ["apple"]}</pre>
   *
   * @param predicate a non-interfering, stateless predicate to apply to each element to determine
   *     if it should be included
   * @return the filtered Sequence
   */
  public Sequence<T> filter(Predicate<? super T> predicate) {
    return this.filter((t, i) -> predicate.test(t));
  }

  /**
   * Returns a {@link Sequence} containing only elements matching the given predicate.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("apple", "banana", "pear");
   * Sequence<String> seq = Sequence.it(list).filter((s, i) -> s.startsWith("a") || i % 2 == 0);
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code ["apple", "pear"]}</pre>
   *
   * @param predicate a non-interfering, stateless predicate to apply to each element to determine
   *     if it should be included
   * @return the new Sequence
   */
  public Sequence<T> filter(BiPredicate<? super T, Integer> predicate) {
    return it(new FilteringTransform<>(this, predicate));
  }

  /**
   * Returns a {@link Sequence} containing all elements not matching the given predicate.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("apple", "banana", "pear");
   * Sequence<String> seq = Sequence.it(list).filterNot(s -> s.startsWith("a"));
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code ["apple"]}</pre>
   *
   * @param predicate a non-interfering, stateless predicate to apply to each element to determine
   *     if it should be included
   * @return the new Sequence
   */
  public Sequence<T> filterNot(Predicate<? super T> predicate) {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns a {@link Sequence} containing all elements not matching the given predicate.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("apple", "banana", "pear");
   * Sequence<String> seq = Sequence.it(list).filterNot((s, i) -> s.startsWith("a") || i % 2 == 0);
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code ["apple", "pear"]}</pre>
   *
   * @param predicate a non-interfering, stateless predicate to apply to each element to determine
   *     if it should be included
   * @return the new Sequence
   */
  public Sequence<T> filterNot(BiPredicate<? super T, Integer> predicate) {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns a Sequence containing all elements that are not <code>null</code>.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("apple", null, "pear");
   * Sequence<String> seq = Sequence.it(list).filterNotNull();
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code ["apple", "pear"]}</pre>
   *
   * @return the new Sequence
   */
  public Sequence<T> filterNotNull() {
    return this.filter(Objects::nonNull);
  }

  /**
   * Returns a Sequence containing the results of applying the given mapper function to each element
   * in the original Sequence.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * Employee e1 = new Employee("John", 30);
   * Employee e2 = new Employee("Jane", 25);
   *
   * List<Employee> list = Arrays.asList(e1, e2);
   * Sequence<String> seq = Sequence.it(list).map(Employee::getName);
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code ["John", "Jane"]}</pre>
   *
   * @param <R> the type of the elements in the resulting Sequence
   * @param mapper the function to apply to each element
   * @return the new Sequence
   */
  public <R> Sequence<R> map(Function<? super T, ? extends R> mapper) {
    return it(new MappingTransform<>(this, mapper));
  }

  /**
   * Returns a Sequence containing only the non-none results of applying the given mapper function
   * to each element in the original Sequence.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * Employee e1 = new Employee("John", 30);
   * Employee e2 = new Employee(null, 25);
   *
   * List<Employee> list = Arrays.asList(e1, e2);
   * Sequence<String> seq = Sequence.it(list).mapNotNull(Employee::getName);
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code ["John"]}</pre>
   *
   * @param <R> the type of the elements in the resulting Sequence
   * @param mapper the function to apply to each element
   * @return the new Sequence
   */
  public <R> Sequence<R> mapNotNull(Function<? super T, ? extends R> mapper) {
    return this.filterNotNull().map(mapper);
  }

  /**
   * Returns a single list of all elements yielded from results of function being invoked on each
   * element of original collection.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * String[] e1 = new String[]{"a", "b"};
   * String[] e2 = new String[]{"c", "d"};
   *
   * List<String[]> list = Arrays.asList(e1, e2);
   * Sequence<String> seq = Sequence.it(list).flatMap(x -> x);
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code ["a", "b", "c", "d"]}</pre>
   *
   * @param <R> the type of the elements in the resulting Sequence
   * @param mapper the function to apply to each element
   * @return the new Sequence
   */
  public <R> Sequence<R> flatMap(Function<? super T, ? extends R> mapper) {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the first element matching the given predicate.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("apple", "banana", "pear");
   * Optional<String> op = Sequence.it(list).find(s -> s.startsWith("a"));
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code "apple"}</pre>
   *
   * @param predicate a non-interfering, stateless predicate to apply to each element to determine
   *     if it should be included
   * @return returns the {@link Optional} instance
   */
  public Optional<T> find(Predicate<? super T> predicate) {
    return Optional.ofNullable(this.filter(predicate).firstOrNull());
  }

  /**
   * Returns the last element matching the given predicate.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("apple", "banana", "pear");
   * Optional<String> op = Sequence.it(list).findLast(s -> s.startsWith("a"));
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code "apple"}</pre>
   *
   * @param predicate a non-interfering, stateless predicate to apply to each element to determine
   *     if it should be included
   * @return returns the {@link Optional} instance
   */
  public Optional<T> findLast(Predicate<? super T> predicate) {
    Sequence<T> seq = this.filter(predicate);
    return Optional.ofNullable(seq.elementAt(seq.size() - 1));
  }

  /**
   * Returns the single element matching the given predicate.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("apple", "banana", "pear");
   * Optional<String> op = Sequence.it(list).single(s -> s.startsWith("a"));
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code ["apple"]}</pre>
   *
   * @param predicate a non-interfering, stateless predicate to apply to each element to determine
   *     if it should be included
   * @return returns the {@link Optional} instance
   * @throws IllegalStateException when there is more than one element that matches the predicate
   */
  public Optional<T> single(Predicate<? super T> predicate) {
    Sequence<T> seq = this.filter(predicate);
    T single = null;
    boolean found = false;
    for (T t : seq) {
      if (found) throw new IllegalStateException("More than one element matches the predicate");
      found = true;
      single = t;
    }
    return Optional.ofNullable(single);
  }

  /**
   * Returns the first element.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("apple", "banana", "pear");
   * String firstElement = Sequence.it(list).first();
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code "apple"}</pre>
   *
   * @return the first element
   * @throws NullPointerException if Sequence is empty or the selected element is null
   */
  public T first() {
    if (!this.hasNext()) throw new NullPointerException("Sequence is empty");

    T t = this.iterator().next();
    if (t == null) throw new NullPointerException("Sequence is empty");

    return t;
  }

  /**
   * Returns the first element.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList(null, "banana", "pear");
   * String firstElement = Sequence.it(list).firstNotNull();
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code "banana"}</pre>
   *
   * @return the first element
   * @throws NullPointerException if Sequence is empty or the all element is null
   */
  public T firstNotNull() {
    if (!this.hasNext()) throw new NullPointerException("Sequence is empty");

    Iterator<T> itr = this.iterator();
    while (itr.hasNext()) {
      T t = itr.next();
      if (t != null) return t;
    }

    throw new NullPointerException("Sequence all elements are null");
  }

  /**
   * Returns the first element.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("apple", "banana", "pear");
   * String firstElement = Sequence.it(list).firstOrNull();
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code "apple"}</pre>
   *
   * @return returns null when Sequence is empty, or when the first element selected is null
   */
  @CheckForNull
  public T firstOrNull() {
    return this.hasNext() ? this.iterator().next() : null;
  }

  /**
   * Returns the first element, or the given default value if the Sequence is empty.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("apple", "banana", "pear");
   * String firstElement = Sequence.it(list).firstOrDefault("mango");
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code "apple"}</pre>
   *
   * @param defaultValue the default value
   * @return the first element, or the given default value
   * @throws NullPointerException if the default value is null
   */
  public T firstOrDefault(@CheckForNull T defaultValue) {
    if (defaultValue == null) throw new NullPointerException("defaultValue is null");

    return Optional.ofNullable(this.firstOrNull()).orElse(defaultValue);
  }

  /**
   * Returns the last element.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("apple", "banana", "pear");
   * String lastElement = Sequence.it(list).last();
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code "pear"}</pre>
   *
   * @return the last element
   * @throws NullPointerException if Sequence is empty or the selected element is null
   */
  public T last() {
    long size = this.size();
    if (size <= 0) throw new NullPointerException("Sequence is empty");

    T t = this.elementAt(size - 1);
    if (t == null) throw new NullPointerException("Element is null");

    return t;
  }

  /**
   * Returns the last element.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("apple", "banana", null);
   * String lastElement = Sequence.it(list).lastNotNull();
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code "banana"}</pre>
   *
   * @return the last element
   * @throws NullPointerException if Sequence is empty or the selected element is null
   */
  public T lastNotNull() {
    long size = this.size();
    if (size <= 0) throw new NullPointerException("Sequence is empty");

    for (int i = (int) size - 1; i >= 0; i--) {
      T t = this.elementAt(i);
      if (t != null) return t;
    }

    throw new NullPointerException("Sequence all elements are null");
  }

  /**
   * Returns the last element.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("apple", "banana", "pear");
   * String lastElement = Sequence.it(list).lastOrNull();
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code "pear"}</pre>
   *
   * @return returns null when Sequence is empty, or when the last element selected is null
   */
  public T lastOrNull() {
    long size = this.size();
    if (size <= 0) throw new NullPointerException("Sequence is empty");

    return this.elementAt(size - 1);
  }

  /**
   * Returns the last element, or the given default value if the Sequence is empty.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("apple", "banana", "pear");
   * String lastElement = Sequence.it(list).lastOrDefault("mango");
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code "pear"}</pre>
   *
   * @param defaultValue the default value
   * @return the last element, or the given default value
   */
  public T lastOrDefault(@CheckForNull T defaultValue) {
    if (defaultValue == null) throw new NullPointerException("defaultValue is null");

    return Optional.ofNullable(this.lastOrNull()).orElse(defaultValue);
  }

  /**
   * Returns first index of element.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("apple", "banana", "pear");
   * int elementIndex = Sequence.it(list).indexOf("apple");
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code 0}</pre>
   *
   * @param element the element
   * @return the index of element
   */
  public int indexOf(T element) {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns a Sequence containing all elements except first count elements.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("apple", "banana", "pear");
   * Sequence<String> seq = Sequence.it(list).skip(2);
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code ["pear"]}</pre>
   *
   * @param count the skip count
   * @return the new Sequence
   */
  public Sequence<T> skip(int count) {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns an Sequence containing first count elements.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("apple", "banana", "pear");
   * Sequence<String> seq = Sequence.it(list).take(2);
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code ["apple", "banana"]}</pre>
   *
   * @param count the take count
   * @return the new Sequence
   */
  public Sequence<T> take(int count) {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns an Sequence with elements in reversed order.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("apple", "banana", "pear");
   * Sequence<String> seq = Sequence.it(list).reversed();
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code ["pear", "banana", "apple"]}</pre>
   *
   * @return the new Sequence
   */
  public Sequence<T> reversed() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns an Sequence that yields elements of this Sequence sorted according to their natural
   * sort order.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("a", "b", "c");
   * Sequence<String> seq = Sequence.it(list).sorted();
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code ["a", "b", "c"]}</pre>
   *
   * @return the new Sequence
   */
  public Sequence<T> sorted() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns an Sequence that yields elements of this sequence sorted according to natural sort
   * order of the value returned by specified function.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * Employee e1 = new Employee("John", 30);
   * Employee e2 = new Employee("Jane", 25);
   *
   * List<Employee> list = Arrays.asList(e1, e2);
   * Sequence<Employee> seq = Sequence.it(list).sorted(Employee::getAge);
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code [Employee{"Jane", 25}, Employee{"John", 30}]}</pre>
   *
   * @param comparator a non-interfering, stateless {@link Comparator} to be used to compare stream
   *     elements
   * @return the new Sequence
   */
  public Sequence<T> sorted(Comparator<? super T> comparator) {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns an Sequence of all elements sorted descending according to their natural sort order.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("a", "b", "c");
   * Sequence<String> seq = Sequence.it(list).sortedDescending();
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code ["c", "b", "a"]}</pre>
   *
   * @return the new Sequence
   */
  public Sequence<T> sortedDescending() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns an Sequence that yields elements of this sequence sorted descending according to
   * natural sort order of the value returned by specified function.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * Employee e1 = new Employee("John", 30);
   * Employee e2 = new Employee("Jane", 25);
   *
   * List<Employee> list = Arrays.asList(e1, e2);
   * Sequence<Employee> seq = Sequence.it(list).sortedDescending(Employee::getAge);
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code [Employee{"John", 30}, Employee{"Jane", 25}]}</pre>
   *
   * @param comparator a non-interfering, stateless {@link Comparator} to be used to compare stream
   *     elements
   * @return the new Sequence
   */
  public Sequence<T> sortedDescending(Comparator<? super T> comparator) {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns True if all elements of the Sequence satisfy the specified predicate function.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<Integer> list = Arrays.asList(1, 2, 3);
   * boolean b = Sequence.it(list).all(x -> x > 0);
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code true}</pre>
   *
   * @param predicate a non-interfering, stateless predicate to apply to each element to determine
   *     if it should be included
   * @return true, or false
   */
  public boolean all(Predicate<T> predicate) {
    for (T t : this) {
      if (!predicate.test(t)) return false;
    }
    return true;
  }

  /**
   * Returns True if any elements of the Sequence satisfy the specified predicate function.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<Integer> list = Arrays.asList(1, 2, 3);
   * boolean b = Sequence.it(list).any(x -> x > 0);
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code true}</pre>
   *
   * @param predicate a non-interfering, stateless predicate to apply to each element to determine
   *     if it should be included
   * @return true, or false
   */
  public boolean any(Predicate<T> predicate) {
    for (T t : this) {
      if (predicate.test(t)) return true;
    }
    return false;
  }

  /**
   * Returns the number of elements in the Sequence that satisfy the specified predicate function.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<Integer> list = Arrays.asList(1, 2, 3);
   * int num = Sequence.it(list).count(x -> x > 0);
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code 3}</pre>
   *
   * @param predicate a non-interfering, stateless predicate to apply to each element to determine
   *     if it should be included
   * @return the count of elements that match the predicate
   */
  public int count(Predicate<T> predicate) {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns True if the Sequence contains the specified value.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<Integer> list = Arrays.asList(1, 2, 3);
   * boolean b = Sequence.it(list).contains(1);
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code true}</pre>
   *
   * @param value the value
   * @return true, or false
   */
  public boolean contains(T value) {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the element at the specified index in the Sequence.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<Integer> list = Arrays.asList(1, 2, 3);
   * Integer element = Sequence.it(list).elementAt(1);
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code 1}</pre>
   *
   * @param index index of the element to return
   * @return the element at the specified position in this Sequence
   * @throws IndexOutOfBoundsException if the index is out of range {@code (index < 0 || index >=
   *     size())}
   */
  public T elementAt(long index) {
    return filter((t, i) -> i == index).firstOrNull();
  }

  /**
   * Returns the element at the specified index in the Sequence or the default value if the index is
   * out of bounds.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<Integer> list = Arrays.asList(1, 2, 3);
   * Integer element = Sequence.it(list).elementAtOrElse(1, 1);
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code 1}</pre>
   *
   * @param index index of the element to return
   * @param defaultValue the default value to return if the index is out of bounds
   * @return the element at the specified position in this Sequence
   * @throws IndexOutOfBoundsException if the index is out of range {@code (index < 0 || index >=
   *     size())}
   */
  public T elementAtOrElse(int index, T defaultValue) {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the element at the specified index in the Sequence or the default value if the index is
   * out of bounds.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<Integer> list = Arrays.asList(1, 2, 3);
   * Integer element = Sequence.it(list).elementAtOrElse(1, 1);
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code 1}</pre>
   *
   * @param index index of the element to return
   * @param function the callable function
   * @return the element at the specified position in this Sequence
   * @throws IndexOutOfBoundsException if the index is out of range {@code (index < 0 || index >=
   *     size())}
   */
  public T elementAtOrElse(int index, Function<? super T, ? extends T> function) {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns a new Sequence containing the distinct elements of the given Sequence.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<Integer> list = Arrays.asList(1, 1, 2, 3);
   * Sequence<Integer> seq = Sequence.it(list).distinct();
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code [1, 2, 3]}</pre>
   *
   * @return the new Sequence
   */
  public Sequence<T> distinct() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the result of applying the specified function to the given Sequence's elements.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<Integer> list = Arrays.asList(1, 2, 3);
   * Optional<Integer> op = Sequence.it(list).reduce((x, y) -> x + y);
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code 3}</pre>
   *
   * @param function the function to apply to each element
   * @return the result
   */
  public Optional<T> reduce(BiFunction<? super T, ? super T, ? extends T> function) {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns a dictionary with keys being the result of function being invoked on each element of
   * original collection and values being the corresponding elements of original collection.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<Integer> list = Arrays.asList(1, 2, 3, 4);
   * Sequence<Grouping<Integer, Integer>> seq = Sequence.it(list).groupBy(x -> x % 2);
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code [(1, [1, 3]), (0, [2, 4])]}</pre>
   *
   * @param <K> the type of the keys
   * @param function the function mapping input elements to keys
   * @return the new Sequence
   */
  public <K> Sequence<Grouping<K, T>> groupBy(Function<? super T, ? extends K> function) {
    throw new UnsupportedOperationException();
  }

  /**
   * Invokes action function on each element of the given Sequence.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("a", "b", "c");
   * Sequence.it(list).forEach((i, x) -> {
   *   System.out.println(i + " " + x);
   * });
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code
   * 0 a
   * 1 b
   * 2 c
   * }</pre>
   *
   * @param action The action to be performed for each element
   */
  public void forEach(BiConsumer<Integer, ? super T> action) {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns a sequence of all elements from all sequences in this sequence.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<List<String>> list = Arrays.asList(Arrays.asList("a", "b"), Arrays.asList("c", "d"));
   * Sequence.it(list).flatten();
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code ["a", "b", "c", "d"]}</pre>
   *
   * @return the new Sequence
   */
  public Sequence<T> flatten() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns a new Sequence of tuples, where each tuple contains two elements.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("a", "b", "c");
   * List<Integer> list2 = Arrays.asList(1, 2, 3);
   * Sequence.it(list).zip(list2);
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code [("a", 1), ("b", 2), ("c", 3)]}</pre>
   *
   * @param <U> the type of the second element
   * @param coll the collection to zip with
   * @return the new Sequence
   */
  public <U> Sequence<BiTuple<T, U>> zip(Collection<? extends U> coll) {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns consecutive sub-lists of a list, each of the same size (the final list may be smaller).
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("a", "b", "c", "d", "e");
   * Sequence.it(list).partition(3);
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code [["a", "b", "c"], ["d", "e"]]}</pre>
   *
   * @param size the desired size of each sub-list (the last may be smaller)
   * @return the new Sequence
   */
  public Sequence<Sequence<T>> partition(int size) {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns a sequence whose elements are shuffled randomly.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("a", "b", "c");
   * Sequence.it(list).shuffled();
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code ["b", "a", "c"]}</pre>
   *
   * @return the new Sequence
   */
  public Sequence<T> shuffled() {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns a Sequence of all elements of the given Sequence, followed by all elements of the given
   * Sequence.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("a", "b");
   * List<String> list2 = Arrays.asList("c", "d");
   * Sequence.it(list).concat(list2);
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code ["a", "b", "c", "d"]}</pre>
   *
   * @param coll the collection to concat with
   * @return the new Sequence
   */
  public Sequence<T> concat(Collection<? extends T> coll) {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns a set containing all elements that are contained by both this collection and the
   * specified collection.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("a", "b");
   * List<String> list2 = Arrays.asList("b", "c");
   * Sequence.it(list).intersect(list2);
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code ["b"]}</pre>
   *
   * @param coll the collection to intersect with
   * @return the new Sequence
   */
  public Sequence<T> intersect(Collection<? extends T> coll) {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns a set containing all distinct elements from both collections.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("a", "a", "b", "c");
   * List<String> list2 = Arrays.asList("x", "v", "c");
   * Sequence.it(list).union(list2);
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code ["a", "b", "c", "x", "v"]}</pre>
   *
   * @param coll the collection to union with
   * @return the new Sequence
   */
  public Sequence<T> union(Collection<? extends T> coll) {
    throw new UnsupportedOperationException();
  }

  /**
   * Joins the elements of the given text into a string.
   *
   * <p>Example usages:
   *
   * <pre>{@code
   * List<String> list = Arrays.asList("a", "b", "c");
   * Sequence.it(list).join(", ");
   * }</pre>
   *
   * <p>Example output:
   *
   * <pre>{@code "a, b, c"}</pre>
   *
   * @param text the text to join
   * @return the joined string, not null
   */
  public String join(CharSequence text) {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the number of elements in this collection.
   *
   * @return the number
   */
  public long size() {
    return StreamSupport.stream(itr.spliterator(), this.parallel).count();
  }

  /**
   * Returns a list containing all elements of this collection.
   *
   * @return the new list
   */
  @Override
  @Nonnull
  public ArrayList<T> toList() {
    ArrayList<T> arrayList = new ArrayList<>();
    for (T e : this) arrayList.add(e);
    return arrayList;
  }

  /**
   * Returns a list containing all elements of this collection.
   *
   * @return the new list
   */
  public ArrayDeque<T> toDequeList() {
    ArrayDeque<T> arrayDeque = new ArrayDeque<>();
    for (T e : this) arrayDeque.add(e);
    return arrayDeque;
  }

  /**
   * Returns an array containing all elements of this collection.
   *
   * @return the new array
   */
  public T[] toArray(T[] a) {
    return toList().toArray(a);
  }

  @Override
  @Nonnull
  public <R> Enumerable<R> collect(Function<? super T, ? extends R> function) {
    return this.map(function);
  }

  @Override
  @Nonnull
  public Iterator<T> iterator() {
    return itr.iterator();
  }

  private boolean hasNext() {
    return this.iterator().hasNext();
  }

  public static final class Grouping<K, V> implements Enumerable<V> {

    private final K key;
    private final Sequence<V> values;

    public Grouping(@CheckForNull K key, Sequence<V> values) {
      this.key = checkNotNull(key);
      this.values = values;
    }

    /**
     * Gets the key.
     *
     * @return the key, not null
     */
    public K key() {
      return key;
    }

    /**
     * Gets the values.
     *
     * @return the values
     */
    public Sequence<V> values() {
      return values;
    }

    @Nonnull
    @Override
    public <R> Enumerable<R> collect(Function<? super V, ? extends R> function) {
      throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public Iterator<V> iterator() {
      throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public ArrayList<V> toList() {
      throw new UnsupportedOperationException();
    }
  }

  public static final class IndexedValue<T> {

    private final int index;
    private final T value;

    public IndexedValue(int index, T value) {
      this.index = index;
      this.value = value;
    }

    /**
     * Gets the index.
     *
     * @return the index, not null
     */
    public int index() {
      return index;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public T value() {
      return value;
    }
  }

  static final class RangeIntTransform implements Iterable<Integer> {

    private int from;
    private final int upTo;

    public RangeIntTransform(int from, int upTo) {
      this.from = from - 1;
      this.upTo = upTo;
    }

    public int estimateSize() {
      return upTo - from + 1;
    }

    @Nonnull
    @Override
    public Iterator<Integer> iterator() {
      return new AbstractIterator<Integer>() {
        @Override
        protected Integer computeNext() {
          int size = estimateSize();
          if (size <= 1 || from == upTo) return endOfData();
          from++;
          return from;
        }
      };
    }
  }

  abstract static class SequenceTransform<T, R> implements Iterable<R> {

    private final ArrayList<R> cache;
    protected final Iterable<T> itr;

    public SequenceTransform(Iterable<T> itr) {
      this.itr = itr;
      this.cache = new ArrayList<>();
    }

    @Nonnull
    @Override
    public Iterator<R> iterator() {
      if (cache.isEmpty()) {
        Iterator<R> iterator = tryTransform();
        while (iterator.hasNext()) {
          R next = iterator.next();
          cache.add(next);
        }
      }
      return cache.iterator();
    }

    @ForOverride
    protected abstract Iterator<R> tryTransform();
  }

  static final class FilteringTransform<T> extends SequenceTransform<T, T> {

    private final BiPredicate<? super T, Integer> predicate;

    public FilteringTransform(Iterable<T> itr, BiPredicate<? super T, Integer> predicate) {
      super(itr);
      this.predicate = predicate;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected Iterator<T> tryTransform() {
      Iterator<T> in = itr.iterator();

      return new AbstractIterator() {
        @Override
        protected @Nullable Object computeNext() {
          for (int i = 0; in.hasNext(); ++i) {
            T current = in.next();
            if (predicate.test(current, i)) {
              return current;
            }
          }
          return endOfData();
        }
      };
    }
  }

  static final class MappingTransform<T, R> extends SequenceTransform<T, R> {

    private final Function<? super T, ? extends R> mapper;

    public MappingTransform(Iterable<T> itr, Function<? super T, ? extends R> mapper) {
      super(itr);
      this.mapper = mapper;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected Iterator<R> tryTransform() {
      Iterator<T> in = itr.iterator();

      return new AbstractIterator() {
        @Override
        protected @Nullable Object computeNext() {
          while (in.hasNext()) {
            T next = in.next();
            return mapper.apply(next);
          }
          return endOfData();
        }
      };
    }
  }
}
