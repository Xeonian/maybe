package net.xeona.maybe;

import static java.util.Objects.requireNonNull;
import static net.xeona.maybe.Maybe.maybe;
import static net.xeona.maybe.MaybeByte.justByte;
import static net.xeona.maybe.MaybeChar.justChar;
import static net.xeona.maybe.MaybeDouble.justDouble;
import static net.xeona.maybe.MaybeFloat.justFloat;
import static net.xeona.maybe.MaybeInt.justInt;
import static net.xeona.maybe.MaybeLong.justLong;
import static net.xeona.maybe.MaybeShort.justShort;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.xeona.function.BooleanConsumer;
import net.xeona.function.BooleanFunction;
import net.xeona.function.BooleanProvider;
import net.xeona.function.BooleanToByteFunction;
import net.xeona.function.BooleanToCharFunction;
import net.xeona.function.BooleanToDoubleFunction;
import net.xeona.function.BooleanToFloatFunction;
import net.xeona.function.BooleanToIntFunction;
import net.xeona.function.BooleanToLongFunction;
import net.xeona.function.BooleanToShortFunction;
import net.xeona.function.BooleanUnaryOperator;
import net.xeona.function.Provider;
import net.xeona.function.VoidFunction;

public abstract class MaybeBoolean implements Collection<Boolean>, Serializable {

	private static final long serialVersionUID = 1L;

	private MaybeBoolean() {}

	public abstract boolean isPresent();

	public abstract boolean get();

	public abstract boolean orElse(boolean other);

	public abstract <X extends Throwable> boolean orElseGet(BooleanProvider<? extends X> provider) throws X;

	public abstract <X extends Throwable, Y extends Throwable> boolean orElseThrow(
			Provider<? extends X, ? extends Y> provider) throws X, Y;

	public abstract <X extends Throwable> void ifPresent(BooleanConsumer<? extends X> consumer) throws X;

	public abstract <X extends Throwable> void ifAbsent(VoidFunction<? extends X> function) throws X;

	public abstract <X extends Throwable, Y extends Throwable> void byPresence(BooleanConsumer<? extends X> ifPresent,
			VoidFunction<? extends Y> ifAbsent) throws X, Y;

	public abstract <X extends Throwable> MaybeBoolean filter(BooleanUnaryOperator<? extends X> predicate) throws X;

	public abstract <X extends Throwable> MaybeBoolean mapToBoolean(BooleanUnaryOperator<? extends X> function)
			throws X;

	public abstract <X extends Throwable> MaybeChar mapToChar(BooleanToCharFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeByte mapToByte(BooleanToByteFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeShort mapToShort(BooleanToShortFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeInt mapToInt(BooleanToIntFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeLong mapToLong(BooleanToLongFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeFloat mapToFloat(BooleanToFloatFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeDouble mapToDouble(BooleanToDoubleFunction<? extends X> function)
			throws X;

	public abstract <T, X extends Throwable> Maybe<T> map(BooleanFunction<? extends T, ? extends X> function) throws X;

	@Override
	public boolean add(Boolean element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends Boolean> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		throw new UnsupportedOperationException();
	}

	public static MaybeBoolean justBoolean(boolean value) {
		return value ? JustBoolean.TRUE_INSTANCE : JustBoolean.FALSE_INSTANCE;
	}

	public static MaybeBoolean noBoolean() {
		return NoBoolean.INSTANCE;
	}

	public static class JustBoolean extends MaybeBoolean {

		private static final long serialVersionUID = 1L;

		private static final JustBoolean TRUE_INSTANCE = new JustBoolean(true);
		private static final JustBoolean FALSE_INSTANCE = new JustBoolean(false);

		private final boolean value;

		private JustBoolean(boolean value) {
			this.value = value;
		}

		@Override
		public boolean isPresent() {
			return true;
		}

		@Override
		public boolean get() {
			return value;
		}

		@Override
		public boolean orElse(boolean other) {
			return value;
		}

		@Override
		public <X extends Throwable> boolean orElseGet(BooleanProvider<? extends X> provider) throws X {
			return value;
		}

		@Override
		public <X extends Throwable, Y extends Throwable> boolean orElseThrow(
				Provider<? extends X, ? extends Y> provider) throws X {
			return value;
		}

		@Override
		public <X extends Throwable> void ifPresent(BooleanConsumer<? extends X> consumer) throws X {
			consumer.consume(value);
		}

		@Override
		public <X extends Throwable> void ifAbsent(VoidFunction<? extends X> function) {}

		@Override
		public <X extends Throwable, Y extends Throwable> void byPresence(BooleanConsumer<? extends X> ifPresent,
				VoidFunction<? extends Y> ifAbsent) throws X {
			ifPresent(ifPresent);
		}

		@Override
		public <X extends Throwable> MaybeBoolean filter(BooleanUnaryOperator<? extends X> predicate) throws X {
			return predicate.apply(value) ? this : NoBoolean.INSTANCE;
		}

		@Override
		public <X extends Throwable> MaybeBoolean mapToBoolean(BooleanUnaryOperator<? extends X> function) throws X {
			return justBoolean(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <X extends Throwable> MaybeChar mapToChar(BooleanToCharFunction<? extends X> function) throws X {
			return justChar(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <X extends Throwable> MaybeByte mapToByte(BooleanToByteFunction<? extends X> function) throws X {
			return justByte(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <X extends Throwable> MaybeShort mapToShort(BooleanToShortFunction<? extends X> function) throws X {
			return justShort(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <X extends Throwable> MaybeInt mapToInt(BooleanToIntFunction<? extends X> function) throws X {
			return justInt(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <X extends Throwable> MaybeLong mapToLong(BooleanToLongFunction<? extends X> function) throws X {
			return justLong(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <X extends Throwable> MaybeFloat mapToFloat(BooleanToFloatFunction<? extends X> function) throws X {
			return justFloat(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <X extends Throwable> MaybeDouble mapToDouble(BooleanToDoubleFunction<? extends X> function) throws X {
			return justDouble(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <T, X extends Throwable> Maybe<T> map(BooleanFunction<? extends T, ? extends X> function) throws X {
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
			return elementToTest instanceof Boolean && ((Boolean) elementToTest).booleanValue() == value;
		}

		@Override
		public boolean containsAll(Collection<?> collection) {
			return collection.stream().allMatch(element -> contains(element));
		}

		@Override
		public Object[] toArray() {
			Object[] arr = new Object[1];
			arr[0] = value;
			return arr;
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
		public Iterator<Boolean> iterator() {
			return new Iterator<Boolean>() {

				private boolean hasNext = true;

				@Override
				public boolean hasNext() {
					return hasNext;
				}

				@Override
				public Boolean next() {
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
			return Boolean.hashCode(value);
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof JustBoolean && ((JustBoolean) other).value == value;
		}

		@Override
		public String toString() {
			return "JustBoolean [" + value + "]";
		}

	}

	private static class NoBoolean extends MaybeBoolean {

		private static final long serialVersionUID = 1L;

		private static final NoBoolean INSTANCE = new NoBoolean();

		private static final Iterator<Boolean> ITERATOR = new Iterator<Boolean>() {

			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public Boolean next() {
				throw new NoSuchElementException();
			}
		};

		@Override
		public boolean isPresent() {
			return false;
		}

		@Override
		public boolean get() {
			throw new NoSuchElementException();
		}

		@Override
		public boolean orElse(boolean other) {
			return other;
		}

		@Override
		public <X extends Throwable> boolean orElseGet(BooleanProvider<? extends X> provider) throws X {
			return provider.get();
		}

		@Override
		public <X extends Throwable, Y extends Throwable> boolean orElseThrow(
				Provider<? extends X, ? extends Y> provider) throws X, Y {
			throw provider.get();
		}

		@Override
		public <X extends Throwable> void ifPresent(BooleanConsumer<? extends X> consumer) {}

		@Override
		public <X extends Throwable> void ifAbsent(VoidFunction<? extends X> function) throws X {
			function.apply();
		}

		@Override
		public <X extends Throwable, Y extends Throwable> void byPresence(BooleanConsumer<? extends X> ifPresent,
				VoidFunction<? extends Y> ifAbsent) throws Y {
			ifAbsent(ifAbsent);
		}

		@Override
		public <X extends Throwable> MaybeBoolean filter(BooleanUnaryOperator<? extends X> predicate) {
			return INSTANCE;
		}

		@Override
		public <X extends Throwable> MaybeBoolean mapToBoolean(BooleanUnaryOperator<? extends X> function) {
			return INSTANCE;
		}

		@Override
		public <X extends Throwable> MaybeChar mapToChar(BooleanToCharFunction<? extends X> function) {
			return MaybeChar.nothing();
		}

		@Override
		public <X extends Throwable> MaybeByte mapToByte(BooleanToByteFunction<? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <X extends Throwable> MaybeShort mapToShort(BooleanToShortFunction<? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <X extends Throwable> MaybeInt mapToInt(BooleanToIntFunction<? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <X extends Throwable> MaybeLong mapToLong(BooleanToLongFunction<? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <X extends Throwable> MaybeFloat mapToFloat(BooleanToFloatFunction<? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <X extends Throwable> MaybeDouble mapToDouble(BooleanToDoubleFunction<? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T, X extends Throwable> Maybe<T> map(BooleanFunction<? extends T, ? extends X> function) throws X {
			return Maybe.nothing();
		}

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
		public Iterator<Boolean> iterator() {
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

		@Override
		public boolean containsAll(Collection<?> c) {
			return c.isEmpty();
		}

		@Override
		public String toString() {
			return "Nothing []";
		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof NoBoolean;
		}

	}

}
