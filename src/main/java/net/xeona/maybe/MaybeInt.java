package net.xeona.maybe;

import java.io.Serializable;
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

public abstract class MaybeInt implements Collection<Integer>, Serializable {

	private static final long serialVersionUID = 1L;

	public abstract int get();

	public abstract boolean isPresent();

	public abstract <X extends Throwable> void ifPresent(IntConsumer<X> consumer) throws X;

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

	public abstract int orElse(int other);

	public abstract <X extends Throwable> int orElseGet(IntProvider<? extends X> valueProvider) throws X;

	public abstract <X extends Throwable> int orElseThrow(Provider<? extends X, RuntimeException> throwableProvider)
			throws X;

	@Override
	public boolean add(Integer e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends Integer> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public static MaybeInt just(int value) {
		return new Just(value);
	}

	public static MaybeInt nothing() {
		return Nothing.instance();
	}

	public static class Just extends MaybeInt {

		private static final long serialVersionUID = 1L;

		private final int value;

		private Just(int value) {
			this.value = value;
		}

		@Override
		public int get() {
			return value;
		}

		@Override
		public boolean isPresent() {
			return true;
		}

		@Override
		public <X extends Throwable> void ifPresent(IntConsumer<X> consumer) throws X {
			consumer.consume(value);
		}

		@Override
		public <X extends Throwable> MaybeInt filter(IntToBooleanFunction<? extends X> predicate) throws X {
			return predicate.apply(value) ? this : nothing();
		}

		@Override
		public <X extends Throwable> MaybeBoolean mapToBoolean(IntToBooleanFunction<? extends X> function) throws X {
			return MaybeBoolean.just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeChar mapToChar(IntToCharFunction<? extends X> function) throws X {
			return MaybeChar.just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeByte mapToByte(IntToByteFunction<? extends X> function) throws X {
			return MaybeByte.just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeShort mapToShort(IntToShortFunction<? extends X> function) throws X {
			return MaybeShort.just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeInt mapToInt(IntUnaryOperator<? extends X> function) throws X {
			return just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeLong mapToLong(IntToLongFunction<? extends X> function) throws X {
			return MaybeLong.just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeFloat mapToFloat(IntToFloatFunction<? extends X> function) throws X {
			return MaybeFloat.just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeDouble mapToDouble(IntToDoubleFunction<? extends X> function) throws X {
			return MaybeDouble.just(function.apply(value));
		}

		@Override
		public <T, X extends Throwable> Maybe<T> map(IntFunction<? extends T, ? extends X> function) throws X {
			return Maybe.maybe(function.apply(value));
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
		public <X extends Throwable> int orElseThrow(Provider<? extends X, RuntimeException> throwableProvider) {
			return value;
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
		public boolean contains(Object o) {
			return o instanceof Integer && ((Integer) o).intValue() == value;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return c.stream().allMatch(this::contains);
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
		public Object[] toArray() {
			Object[] arr = new Object[1];
			arr[0] = value;
			return arr;
		}

		@Override
		public <T> T[] toArray(T[] a) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int hashCode() {
			return Integer.hashCode(value);
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof Just && ((Just) other).value == value;
		}

		@Override
		public String toString() {
			return "Just [" + value + "]";
		}

	}

	private static class Nothing extends MaybeInt {

		private static final long serialVersionUID = 1L;

		private static final Nothing INSTANCE = new Nothing();

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

		private Nothing() {}

		@Override
		public int get() {
			throw new NoSuchElementException();
		}

		@Override
		public boolean isPresent() {
			return false;
		}

		@Override
		public <X extends Throwable> void ifPresent(IntConsumer<X> consumer) throws X {}

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
			return MaybeInt.nothing();
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
		public int orElse(int other) {
			return other;
		}

		@Override
		public <X extends Throwable> int orElseGet(IntProvider<? extends X> valueProvider) throws X {
			return valueProvider.get();
		}

		@Override
		public <X extends Throwable> int orElseThrow(Provider<? extends X, RuntimeException> throwableProvider)
				throws X {
			throw throwableProvider.get();
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
		public boolean containsAll(Collection<?> c) {
			return c.isEmpty();
		}

		@Override
		public Iterator<Integer> iterator() {
			return ITERATOR;
		}

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

		public static Nothing instance() {
			return INSTANCE;
		}

	}

}
