package net.xeona.maybe;

import static java.util.Objects.requireNonNull;
import static net.xeona.maybe.MaybeInt.justInt;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import net.xeona.function.Consumer;
import net.xeona.function.Function;
import net.xeona.function.Provider;
import net.xeona.function.ToBooleanFunction;
import net.xeona.function.ToByteFunction;
import net.xeona.function.ToCharFunction;
import net.xeona.function.ToDoubleFunction;
import net.xeona.function.ToFloatFunction;
import net.xeona.function.ToIntFunction;
import net.xeona.function.ToLongFunction;
import net.xeona.function.ToShortFunction;
import net.xeona.function.VoidFunction;

/**
 * A class for containing values that may be explicitly absent.
 * <p />
 * Intended as an alternative to {@link Optional} with extended functionality.
 *
 * @param <E>
 *            The type of the value contained by this instance
 */
public abstract class Maybe<E> implements Collection<E>, Serializable {

	private static final long serialVersionUID = 1L;

	private Maybe() {}

	public abstract boolean isPresent();

	public abstract E get();

	public abstract E orElse(E other);

	public abstract <X extends Throwable> E orElseGet(Provider<? extends E, ? extends X> provider) throws X;

	public abstract <X extends Throwable, Y extends Throwable> E orElseThrow(
			Provider<? extends X, ? extends Y> provider) throws X, Y;

	public abstract <X extends Throwable> void ifPresent(Consumer<? super E, ? extends X> consumer) throws X;

	public abstract <X extends Throwable> void ifAbsent(VoidFunction<? extends X> application) throws X;

	public abstract <X extends Throwable, Y extends Throwable> void byPresence(
			Consumer<? super E, ? extends X> ifPresent, VoidFunction<? extends Y> ifAbsent) throws X, Y;

	public abstract <X extends Throwable> Maybe<E> filter(ToBooleanFunction<? super E, ? extends X> predicate) throws X;

	public abstract <X extends Throwable> MaybeBoolean mapToBoolean(ToBooleanFunction<? super E, ? extends X> function)
			throws X;

	public abstract <X extends Throwable> MaybeChar mapToChar(ToCharFunction<? super E, ? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeByte mapToByte(ToByteFunction<? super E, ? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeShort mapToShort(ToShortFunction<? super E, ? extends X> function)
			throws X;

	public abstract <X extends Throwable> MaybeInt mapToInt(ToIntFunction<? super E, ? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeLong mapToLong(ToLongFunction<? super E, ? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeFloat mapToFloat(ToFloatFunction<? super E, ? extends X> function)
			throws X;

	public abstract <X extends Throwable> MaybeDouble mapToDouble(ToDoubleFunction<? super E, ? extends X> function)
			throws X;

	public abstract <T, X extends Throwable> Maybe<T> map(Function<? super E, ? extends T, ? extends X> function)
			throws X;

	/**
	 * @throws UnsupportedOperationException
	 *             Instances of Maybe are immutable
	 */
	@Override
	public boolean add(E elementToAdd) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends E> elementsToAdd) {
		if (elementsToAdd.isEmpty()) {
			return false;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	public static <T> Maybe<T> maybe(T value) {
		return value != null ? just(value) : nothing();
	}

	public static <T> Maybe<T> just(T value) {
		return new Just<T>(value);
	}

	public static <T> Maybe<T> nothing() {
		return Nothing.instance();
	}

	public static <T> Maybe<T> reduce(Maybe<Maybe<T>> maybe) {
		return maybe.orElseGet(Maybe::nothing);
	}

	public static <T> Maybe<T> fromOptional(Optional<T> optional) {
		return optional.map(Maybe::just).orElseGet(Maybe::nothing);
	}

	public static <T> Optional<T> toOptional(Maybe<T> maybe) {
		return maybe.map(Optional::of).orElseGet(Optional::empty);
	}

	private static class Just<E> extends Maybe<E> {

		private static final long serialVersionUID = 1L;

		private final E value;

		private Just(E value) {
			this.value = requireNonNull(value, "Optional value must not be null");
		}

		@Override
		public boolean isPresent() {
			return true;
		}

		@Override
		public E get() {
			return value;
		}

		@Override
		public E orElse(E other) {
			return value;
		}

		@Override
		public <X extends Throwable> E orElseGet(Provider<? extends E, ? extends X> provider) {
			return value;
		}

		@Override
		public <X extends Throwable, Y extends Throwable> E orElseThrow(Provider<? extends X, ? extends Y> provider) {
			return value;
		}

		@Override
		public <X extends Throwable> void ifPresent(Consumer<? super E, ? extends X> consumer) throws X {
			requireNonNull(consumer, "Consumer must not be null").consume(value);
		}

		@Override
		public <X extends Throwable> void ifAbsent(VoidFunction<? extends X> application) {}

		@Override
		public <X extends Throwable, Y extends Throwable> void byPresence(Consumer<? super E, ? extends X> ifPresent,
				VoidFunction<? extends Y> ifAbsent) throws X {
			ifPresent(ifPresent);
		}

		@Override
		public <X extends Throwable> Maybe<E> filter(ToBooleanFunction<? super E, ? extends X> predicate) throws X {
			return requireNonNull(predicate, "Predicate must not be null").apply(value) ? this : nothing();
		}

		@Override
		public <X extends Throwable> MaybeBoolean mapToBoolean(ToBooleanFunction<? super E, ? extends X> function)
				throws X {
			return MaybeBoolean.justBoolean(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <X extends Throwable> MaybeChar mapToChar(ToCharFunction<? super E, ? extends X> function) throws X {
			return MaybeChar.justChar(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <X extends Throwable> MaybeByte mapToByte(ToByteFunction<? super E, ? extends X> function) throws X {
			return MaybeByte.justByte(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <X extends Throwable> MaybeShort mapToShort(ToShortFunction<? super E, ? extends X> function) throws X {
			return MaybeShort.justShort(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <X extends Throwable> MaybeInt mapToInt(ToIntFunction<? super E, ? extends X> function) throws X {
			return justInt(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <X extends Throwable> MaybeLong mapToLong(ToLongFunction<? super E, ? extends X> function) throws X {
			return MaybeLong.justLong(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <X extends Throwable> MaybeFloat mapToFloat(ToFloatFunction<? super E, ? extends X> function) throws X {
			return MaybeFloat.justFloat(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <X extends Throwable> MaybeDouble mapToDouble(ToDoubleFunction<? super E, ? extends X> function)
				throws X {
			return MaybeDouble.justDouble(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <U, X extends Throwable> Maybe<U> map(Function<? super E, ? extends U, ? extends X> function) throws X {
			return maybe(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public boolean contains(Object elementToTest) {
			return value.equals(elementToTest);
		}

		@Override
		public boolean containsAll(Collection<?> elementsToTest) {
			return elementsToTest.stream().allMatch(this::contains);
		}

		@Override
		public boolean remove(Object elementToRemove) {
			if (value.equals(elementToRemove)) {
				throw new UnsupportedOperationException();
			} else {
				return false;
			}
		}

		@Override
		public boolean removeAll(Collection<?> elementsToRemove) {
			if (elementsToRemove.contains(value)) {
				throw new UnsupportedOperationException();
			} else {
				return false;
			}
		}

		@Override
		public boolean retainAll(Collection<?> elementsToRetain) {
			if (elementsToRetain.contains(value)) {
				return false;
			} else {
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object[] toArray() {
			return new Object[] { value };
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> T[] toArray(T[] a) {
			Object[] valueArr = new Object[] { value };

			T[] returnArr;
			if (a.length == 0) {
				returnArr = (T[]) Arrays.copyOf(valueArr, 1, a.getClass());
			} else {
				System.arraycopy(valueArr, 0, a, 0, 1);
				if (a.length > 1) {
					a[1] = null;
				}
				returnArr = a;
			}

			return returnArr;
		}

		@Override
		public Iterator<E> iterator() {
			return new Iterator<E>() {

				boolean hasNext = true;

				@Override
				public boolean hasNext() {
					return hasNext;
				}

				@Override
				public E next() {
					if (hasNext) {
						hasNext = false;
						return value;
					} else {
						throw new NoSuchElementException();
					}
				}

			};
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(value);
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof Just && value.equals(((Just<?>) other).value);
		}

		@Override
		public String toString() {
			return "Just [" + value + "]";
		}

	}

	private static class Nothing<E> extends Maybe<E> {

		private static final long serialVersionUID = 1L;

		private static final Nothing<?> INSTANCE = new Nothing<>();

		private static final Iterator<?> ITERATOR = new Iterator<Object>() {

			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public Object next() {
				throw new NoSuchElementException();
			}

		};

		private Nothing() {}

		@Override
		public boolean isPresent() {
			return false;
		}

		@Override
		public E get() {
			throw new NoSuchElementException();
		}

		@Override
		public E orElse(E other) {
			return other;
		}

		@Override
		public <X extends Throwable> E orElseGet(Provider<? extends E, ? extends X> provider) throws X {
			return provider.get();
		}

		@Override
		public <X extends Throwable, Y extends Throwable> E orElseThrow(Provider<? extends X, ? extends Y> s)
				throws X, Y {
			throw s.get();
		}

		@Override
		public <X extends Throwable> void ifPresent(Consumer<? super E, ? extends X> consumer) {}

		@Override
		public <X extends Throwable> void ifAbsent(VoidFunction<? extends X> application) throws X {
			application.apply();
		}

		@Override
		public <X extends Throwable, Y extends Throwable> void byPresence(Consumer<? super E, ? extends X> ifPresent,
				VoidFunction<? extends Y> ifAbsent) throws Y {
			ifAbsent(ifAbsent);
		}

		@Override
		public <X extends Throwable> Maybe<E> filter(ToBooleanFunction<? super E, ? extends X> predicate) {
			return this;
		}

		@Override
		public <X extends Throwable> MaybeBoolean mapToBoolean(ToBooleanFunction<? super E, ? extends X> function) {
			return MaybeBoolean.noBoolean();
		}

		@Override
		public <X extends Throwable> MaybeChar mapToChar(ToCharFunction<? super E, ? extends X> function) {
			return MaybeChar.nothing();
		}

		@Override
		public <X extends Throwable> MaybeByte mapToByte(ToByteFunction<? super E, ? extends X> function) {
			return MaybeByte.noByte();
		}

		@Override
		public <X extends Throwable> MaybeShort mapToShort(ToShortFunction<? super E, ? extends X> function) {
			return MaybeShort.noShort();
		}

		@Override
		public <X extends Throwable> MaybeInt mapToInt(ToIntFunction<? super E, ? extends X> function) {
			return MaybeInt.noInt();
		}

		@Override
		public <X extends Throwable> MaybeLong mapToLong(ToLongFunction<? super E, ? extends X> function) {
			return MaybeLong.noLong();
		}

		@Override
		public <X extends Throwable> MaybeFloat mapToFloat(ToFloatFunction<? super E, ? extends X> function) {
			return MaybeFloat.nothing();
		}

		@Override
		public <X extends Throwable> MaybeDouble mapToDouble(ToDoubleFunction<? super E, ? extends X> function) {
			return MaybeDouble.nothing();
		}

		@Override
		public <U, X extends Throwable> Maybe<U> map(Function<? super E, ? extends U, ? extends X> function) {
			return nothing();
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public boolean contains(Object elementToTest) {
			return false;
		}

		@Override
		public boolean containsAll(Collection<?> elementsToTest) {
			return elementsToTest.isEmpty();
		}

		@Override
		public boolean remove(Object elementToRemove) {
			return false;
		}

		@Override
		public boolean removeAll(Collection<?> elementsToRemove) {
			return false;
		}

		@Override
		public boolean retainAll(Collection<?> elementsToRetain) {
			return false;
		}

		@Override
		public void clear() {}

		@Override
		public Object[] toArray() {
			return new Object[0];
		}

		@Override
		public <T> T[] toArray(T[] a) {
			if (a.length > 0) {
				a[0] = null;
			}
			return a;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Iterator<E> iterator() {
			return (Iterator<E>) ITERATOR;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof Nothing;
		}

		@Override
		public String toString() {
			return "Nothing []";
		}

		@SuppressWarnings("unchecked")
		private static <T> Nothing<T> instance() {
			return (Nothing<T>) INSTANCE;
		}

	}

}
