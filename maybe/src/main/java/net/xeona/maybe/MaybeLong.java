package net.xeona.maybe;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.xeona.function.LongConsumer;
import net.xeona.function.LongFunction;
import net.xeona.function.LongProvider;
import net.xeona.function.LongToBooleanFunction;
import net.xeona.function.LongToByteFunction;
import net.xeona.function.LongToCharFunction;
import net.xeona.function.LongToDoubleFunction;
import net.xeona.function.LongToFloatFunction;
import net.xeona.function.LongToIntFunction;
import net.xeona.function.LongToShortFunction;
import net.xeona.function.LongUnaryOperator;
import net.xeona.function.Provider;
import net.xeona.function.VoidFunction;

public abstract class MaybeLong implements Collection<Long> {

	public abstract boolean isPresent();

	public abstract long get();

	public abstract long orElse(long other);

	public abstract <X extends Throwable> long orElseGet(LongProvider<? extends X> provider) throws X;

	public abstract <X extends Throwable, Y extends Throwable> long orElseThrow(
			Provider<? extends X, ? extends Y> provider) throws X, Y;

	public abstract <X extends Throwable> void ifPresent(LongConsumer<? extends X> consumer) throws X;

	public abstract <X extends Throwable> void ifAbsent(VoidFunction<? extends X> function) throws X;

	public abstract <X extends Throwable, Y extends Throwable> void byPresence(LongConsumer<? extends X> ifPresent,
			VoidFunction<? extends Y> ifAbsent) throws X, Y;

	public abstract <X extends Throwable> MaybeLong filter(LongToBooleanFunction<? extends X> predicate) throws X;

	public abstract <X extends Throwable> MaybeBoolean mapToBoolean(LongToBooleanFunction<? extends X> function)
			throws X;

	public abstract <X extends Throwable> MaybeChar mapToChar(LongToCharFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeByte mapToByte(LongToByteFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeShort mapToShort(LongToShortFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeInt mapToInt(LongToIntFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeLong mapToLong(LongUnaryOperator<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeFloat mapToFloat(LongToFloatFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeDouble mapToDouble(LongToDoubleFunction<? extends X> function) throws X;

	public abstract <T, X extends Throwable> Maybe<T> map(LongFunction<? extends T, ? extends X> function) throws X;

	@Override
	public boolean add(Long elementToAdd) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends Long> elementsToAdd) {
		if (requireNonNull(elementsToAdd, "Collection of elements to add must not be null").isEmpty()) {
			return false;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	public static MaybeLong justLong(long value) {
		return new Just(value);
	}

	public static MaybeLong noLong() {
		return NoLong.instance();
	}

	private static class Just extends MaybeLong {

		private final long value;

		private Just(long value) {
			this.value = value;
		}

		@Override
		public boolean isPresent() {
			return true;
		}

		@Override
		public long get() {
			return value;
		}

		@Override
		public long orElse(long other) {
			return value;
		}

		@Override
		public <X extends Throwable> long orElseGet(LongProvider<? extends X> provider) throws X {
			return value;
		}

		@Override
		public <X extends Throwable, Y extends Throwable> long orElseThrow(Provider<? extends X, ? extends Y> provider)
				throws X, Y {
			return value;
		}

		@Override
		public <X extends Throwable> void ifPresent(LongConsumer<? extends X> consumer) throws X {
			consumer.consume(value);
		}

		@Override
		public <X extends Throwable> void ifAbsent(VoidFunction<? extends X> function) {}

		@Override
		public <X extends Throwable, Y extends Throwable> void byPresence(LongConsumer<? extends X> ifPresent,
				VoidFunction<? extends Y> ifAbsent) throws X {
			ifPresent.consume(value);
		}

		@Override
		public <X extends Throwable> MaybeLong filter(LongToBooleanFunction<? extends X> predicate) throws X {
			return predicate.apply(value) ? this : noLong();
		}

		@Override
		public <X extends Throwable> MaybeBoolean mapToBoolean(LongToBooleanFunction<? extends X> function) throws X {
			return MaybeBoolean.justBoolean(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeChar mapToChar(LongToCharFunction<? extends X> function) throws X {
			return MaybeChar.justChar(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeByte mapToByte(LongToByteFunction<? extends X> function) throws X {
			return MaybeByte.justByte(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeShort mapToShort(LongToShortFunction<? extends X> function) throws X {
			return MaybeShort.justShort(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeInt mapToInt(LongToIntFunction<? extends X> function) throws X {
			return MaybeInt.justInt(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeLong mapToLong(LongUnaryOperator<? extends X> function) throws X {
			return justLong(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeFloat mapToFloat(LongToFloatFunction<? extends X> function) throws X {
			return MaybeFloat.justFloat(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeDouble mapToDouble(LongToDoubleFunction<? extends X> function) throws X {
			return MaybeDouble.justDouble(function.apply(value));
		}

		@Override
		public <T, X extends Throwable> Maybe<T> map(LongFunction<? extends T, ? extends X> function) throws X {
			return Maybe.maybe(function.apply(value));
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
			return elementToTest instanceof Long && ((Long) elementToTest).longValue() == value;
		}

		@Override
		public Iterator<Long> iterator() {
			return new Iterator<Long>() {

				private boolean hasNext = true;

				@Override
				public boolean hasNext() {
					return hasNext;
				}

				@Override
				public Long next() {
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
			return new Object[] { value };
		}

		@Override
		public <T> T[] toArray(T[] a) {
			// TODO Auto-generated method stub
			return null;
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
		public boolean containsAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub

		}

		@Override
		public int hashCode() {
			return Long.hashCode(value);
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof Just && ((Just) other).value == value;
		}

		@Override
		public String toString() {
			return "JustLong [" + value + "]";
		}

	}

	private static class NoLong extends MaybeLong {

		private static final NoLong INSTANCE = new NoLong();

		private NoLong() {}

		@Override
		public boolean isPresent() {
			return false;
		}

		@Override
		public long get() {
			throw new NoSuchElementException();
		}

		@Override
		public long orElse(long other) {
			return other;
		}

		@Override
		public <X extends Throwable> long orElseGet(LongProvider<? extends X> provider) throws X {
			return provider.get();
		}

		@Override
		public <X extends Throwable, Y extends Throwable> long orElseThrow(Provider<? extends X, ? extends Y> provider)
				throws X, Y {
			throw provider.get();
		}

		@Override
		public <X extends Throwable> void ifPresent(LongConsumer<? extends X> consumer) throws X {}

		@Override
		public <X extends Throwable> void ifAbsent(VoidFunction<? extends X> function) throws X {
			function.apply();
		}

		@Override
		public <X extends Throwable, Y extends Throwable> void byPresence(LongConsumer<? extends X> ifPresent,
				VoidFunction<? extends Y> ifAbsent) throws Y {
			ifAbsent.apply();
		}

		@Override
		public <X extends Throwable> MaybeLong filter(LongToBooleanFunction<? extends X> predicate) throws X {
			return this;
		}

		@Override
		public <X extends Throwable> MaybeBoolean mapToBoolean(LongToBooleanFunction<? extends X> function) throws X {
			return MaybeBoolean.noBoolean();
		}

		@Override
		public <X extends Throwable> MaybeChar mapToChar(LongToCharFunction<? extends X> function) throws X {
			return MaybeChar.nothing();
		}

		@Override
		public <X extends Throwable> MaybeByte mapToByte(LongToByteFunction<? extends X> function) throws X {
			return MaybeByte.noByte();
		}

		@Override
		public <X extends Throwable> MaybeShort mapToShort(LongToShortFunction<? extends X> function) throws X {
			return MaybeShort.noShort();
		}

		@Override
		public <X extends Throwable> MaybeInt mapToInt(LongToIntFunction<? extends X> function) throws X {
			return MaybeInt.noInt();
		}

		@Override
		public <X extends Throwable> MaybeLong mapToLong(LongUnaryOperator<? extends X> function) throws X {
			return MaybeLong.noLong();
		}

		@Override
		public <X extends Throwable> MaybeFloat mapToFloat(LongToFloatFunction<? extends X> function) throws X {
			return MaybeFloat.nothing();
		}

		@Override
		public <X extends Throwable> MaybeDouble mapToDouble(LongToDoubleFunction<? extends X> function) throws X {
			return MaybeDouble.nothing();
		}

		@Override
		public <T, X extends Throwable> Maybe<T> map(LongFunction<? extends T, ? extends X> function) throws X {
			return Maybe.nothing();
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean contains(Object o) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Iterator<Long> iterator() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object[] toArray() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T> T[] toArray(T[] a) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean remove(Object o) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub

		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof NoLong;
		}

		@Override
		public String toString() {
			return "NoLong []";
		}

		public static NoLong instance() {
			return INSTANCE;
		}

	}

}
