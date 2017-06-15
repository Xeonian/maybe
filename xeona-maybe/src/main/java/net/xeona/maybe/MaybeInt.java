package net.xeona.maybe;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.xeona.function.IntConsumer;
import net.xeona.function.IntFunction;
import net.xeona.function.IntProvider;
import net.xeona.function.IntToBooleanFunction;
import net.xeona.function.IntToByteFunction;
import net.xeona.function.IntToCharFunction;
import net.xeona.function.IntToDoubleFunction;
import net.xeona.function.IntToFloatFunction;
import net.xeona.function.IntToLongFunction;
import net.xeona.function.IntToShortFunction;
import net.xeona.function.IntUnaryOperator;
import net.xeona.function.Provider;
import net.xeona.function.VoidFunction;

public abstract class MaybeInt implements Collection<Integer>, Serializable {

	private static final long serialVersionUID = 1L;

	private MaybeInt() {}

	public abstract boolean isPresent();

	public abstract int get();

	public abstract int orElse(int other);

	public abstract <X extends Throwable> int orElseGet(IntProvider<? extends X> valueProvider) throws X;

	public abstract <X extends Throwable, Y extends Throwable> int orElseThrow(
			Provider<? extends X, ? extends Y> throwableProvider) throws X, Y;

	public abstract <X extends Throwable> void ifPresent(IntConsumer<? extends X> consumer) throws X;

	public abstract <X extends Throwable> void ifAbsent(VoidFunction<? extends X> function) throws X;

	public abstract <X extends Throwable, Y extends Throwable> void byPresence(IntConsumer<? extends X> ifPresent,
			VoidFunction<? extends Y> ifAbsent) throws X, Y;

	public abstract <X extends Throwable> MaybeInt filter(IntToBooleanFunction<? extends X> predicate) throws X;

	public abstract <X extends Throwable> MaybeBoolean mapToBoolean(IntToBooleanFunction<? extends X> function)
			throws X;

	public abstract <X extends Throwable> MaybeChar mapToChar(IntToCharFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeByte mapToByte(IntToByteFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeShort mapToShort(IntToShortFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeInt mapToInt(IntUnaryOperator<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeLong mapToLong(IntToLongFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeFloat mapToFloat(IntToFloatFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeDouble mapToDouble(IntToDoubleFunction<? extends X> function) throws X;

	public abstract <T, X extends Throwable> Maybe<T> map(IntFunction<? extends T, ? extends X> function) throws X;

	@Override
	public boolean add(Integer elementToAdd) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends Integer> elementsToAdd) {
		if (requireNonNull(elementsToAdd, "Collection of elements to add must not be null").isEmpty()) {
			return false;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	public static MaybeInt maybeInt(Integer value) {
		return value != null ? justInt(value) : noInt();
	}

	public static MaybeInt justInt(int value) {
		return new JustInt(value);
	}

	public static MaybeInt noInt() {
		return NoInt.instance();
	}

	public static class JustInt extends MaybeInt {

		private static final long serialVersionUID = 1L;

		private final int value;

		private JustInt(int value) {
			this.value = value;
		}

		@Override
		public boolean isPresent() {
			return true;
		}

		@Override
		public int get() {
			return value;
		}

		@Override
		public int orElse(int other) {
			return value;
		}

		@Override
		public <X extends Throwable> int orElseGet(IntProvider<? extends X> valueProvider) {
			return value;
		}

		@Override
		public <X extends Throwable, Y extends Throwable> int orElseThrow(
				Provider<? extends X, ? extends Y> throwableProvider) {
			return value;
		}

		@Override
		public <X extends Throwable> void ifPresent(IntConsumer<? extends X> consumer) throws X {
			requireNonNull(consumer, "Consumer must not be null").consume(value);
		}

		@Override
		public <X extends Throwable> void ifAbsent(VoidFunction<? extends X> function) {}

		@Override
		public <X extends Throwable, Y extends Throwable> void byPresence(IntConsumer<? extends X> ifPresent,
				VoidFunction<? extends Y> ifAbsent) throws X {
			requireNonNull(ifPresent, "Consumer must not be null").consume(value);
		}

		@Override
		public <X extends Throwable> MaybeInt filter(IntToBooleanFunction<? extends X> predicate) throws X {
			return requireNonNull(predicate, "Predicate must not be null").apply(value) ? this : noInt();
		}

		@Override
		public <X extends Throwable> MaybeBoolean mapToBoolean(IntToBooleanFunction<? extends X> function) throws X {
			return MaybeBoolean.just(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <X extends Throwable> MaybeChar mapToChar(IntToCharFunction<? extends X> function) throws X {
			return MaybeChar.just(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <X extends Throwable> MaybeByte mapToByte(IntToByteFunction<? extends X> function) throws X {
			return MaybeByte.just(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <X extends Throwable> MaybeShort mapToShort(IntToShortFunction<? extends X> function) throws X {
			return MaybeShort.just(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <X extends Throwable> MaybeInt mapToInt(IntUnaryOperator<? extends X> function) throws X {
			return justInt(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <X extends Throwable> MaybeLong mapToLong(IntToLongFunction<? extends X> function) throws X {
			return MaybeLong.just(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <X extends Throwable> MaybeFloat mapToFloat(IntToFloatFunction<? extends X> function) throws X {
			return MaybeFloat.just(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <X extends Throwable> MaybeDouble mapToDouble(IntToDoubleFunction<? extends X> function) throws X {
			return MaybeDouble.just(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <T, X extends Throwable> Maybe<T> map(IntFunction<? extends T, ? extends X> function) throws X {
			return Maybe.maybe(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public boolean contains(Object elementToTest) {
			return elementToTest instanceof Integer && ((Integer) elementToTest).intValue() == value;
		}

		@Override
		public boolean containsAll(Collection<?> elementsToTest) {
			return requireNonNull(elementsToTest, "Collection of elements to test must not be null").stream()
					.allMatch(this::contains);
		}

		@Override
		public boolean remove(Object elementToRemove) {
			if (contains(elementToRemove)) {
				throw new UnsupportedOperationException();
			} else {
				return false;
			}
		}

		@Override
		public boolean removeAll(Collection<?> elementsToRemove) {
			if (requireNonNull(elementsToRemove, "Collection of elements to remove must not be null").stream()
					.anyMatch(this::contains)) {
				throw new UnsupportedOperationException();
			} else {
				return false;
			}
		}

		@Override
		public boolean retainAll(Collection<?> elementsToRetain) {
			if (requireNonNull(elementsToRetain, "Collection of elements to retain must not be null").stream()
					.anyMatch(this::contains)) {
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
			Object[] arr = new Object[1];
			arr[0] = value;
			return arr;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> T[] toArray(T[] targetArray) {
			requireNonNull(targetArray, "Target array must not be null");
			Object[] valueArr = new Object[] { value };
			T[] returnArr;
			if (targetArray.length == 0) {
				returnArr = (T[]) Arrays.copyOf(valueArr, 1, targetArray.getClass());
			} else {
				System.arraycopy(valueArr, 0, targetArray, 0, 1);
				if (targetArray.length > 1) {
					targetArray[1] = null;
				}
				returnArr = targetArray;
			}
			return returnArr;
		}

		@Override
		public Iterator<Integer> iterator() {
			return new Iterator<Integer>() {

				private boolean hasNext = true;

				@Override
				public boolean hasNext() {
					return hasNext;
				}

				@Override
				public Integer next() {
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
			return Integer.hashCode(value);
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof JustInt && ((JustInt) other).value == value;
		}

		@Override
		public String toString() {
			return "JustInt [" + value + "]";
		}

	}

	private static class NoInt extends MaybeInt {

		private static final long serialVersionUID = 1L;

		private static final NoInt INSTANCE = new NoInt();

		private static final Iterator<Integer> ITERATOR = new Iterator<Integer>() {

			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public Integer next() {
				throw new NoSuchElementException();
			}

		};

		private NoInt() {}

		@Override
		public boolean isPresent() {
			return false;
		}

		@Override
		public int get() {
			throw new NoSuchElementException();
		}

		@Override
		public int orElse(int other) {
			return other;
		}

		@Override
		public <X extends Throwable> int orElseGet(IntProvider<? extends X> valueProvider) throws X {
			return requireNonNull(valueProvider, "Provider must not be null").get();
		}

		@Override
		public <X extends Throwable, Y extends Throwable> int orElseThrow(
				Provider<? extends X, ? extends Y> throwableProvider) throws X, Y {
			throw requireNonNull(throwableProvider, "Provider must not be null").get();
		}

		@Override
		public <X extends Throwable> void ifPresent(IntConsumer<? extends X> consumer) {}

		@Override
		public <X extends Throwable> void ifAbsent(VoidFunction<? extends X> function) throws X {
			requireNonNull(function, "Function must not be null").apply();
		}

		@Override
		public <X extends Throwable, Y extends Throwable> void byPresence(IntConsumer<? extends X> ifPresent,
				VoidFunction<? extends Y> ifAbsent) throws Y {
			requireNonNull(ifAbsent, "Function must not be null").apply();
		}

		@Override
		public <X extends Throwable> MaybeInt filter(IntToBooleanFunction<? extends X> predicate) throws X {
			return this;
		}

		@Override
		public <X extends Throwable> MaybeBoolean mapToBoolean(IntToBooleanFunction<? extends X> function) throws X {
			return MaybeBoolean.nothing();
		}

		@Override
		public <X extends Throwable> MaybeChar mapToChar(IntToCharFunction<? extends X> function) throws X {
			return MaybeChar.nothing();
		}

		@Override
		public <X extends Throwable> MaybeByte mapToByte(IntToByteFunction<? extends X> function) throws X {
			return MaybeByte.nothing();
		}

		@Override
		public <X extends Throwable> MaybeShort mapToShort(IntToShortFunction<? extends X> function) throws X {
			return MaybeShort.nothing();
		}

		@Override
		public <X extends Throwable> MaybeInt mapToInt(IntUnaryOperator<? extends X> function) throws X {
			return noInt();
		}

		@Override
		public <X extends Throwable> MaybeLong mapToLong(IntToLongFunction<? extends X> function) throws X {
			return MaybeLong.nothing();
		}

		@Override
		public <X extends Throwable> MaybeFloat mapToFloat(IntToFloatFunction<? extends X> function) throws X {
			return MaybeFloat.nothing();
		}

		@Override
		public <X extends Throwable> MaybeDouble mapToDouble(IntToDoubleFunction<? extends X> function) throws X {
			return MaybeDouble.nothing();
		}

		@Override
		public <T, X extends Throwable> Maybe<T> map(IntFunction<? extends T, ? extends X> function) throws X {
			return Maybe.nothing();
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public boolean contains(Object o) {
			return false;
		}

		@Override
		public boolean containsAll(Collection<?> elementsToTest) {
			return requireNonNull(elementsToTest, "Collection of elements to test must not be null").isEmpty();
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
		public Iterator<Integer> iterator() {
			return ITERATOR;
		}

		@Override
		public Object[] toArray() {
			return new Object[0];
		}

		@Override
		public <T> T[] toArray(T[] targetArray) {
			requireNonNull(targetArray, "Target array must not be null");
			if (targetArray.length > 0) {
				targetArray[0] = null;
			}
			return targetArray;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof NoInt;
		}

		@Override
		public String toString() {
			return "NoInt []";
		}

		public static NoInt instance() {
			return INSTANCE;
		}

	}

}
