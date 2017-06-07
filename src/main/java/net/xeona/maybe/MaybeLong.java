package net.xeona.maybe;

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

public abstract class MaybeLong {

	public abstract boolean isPresent();

	public abstract long get();

	public abstract long orElse(long other);

	public abstract <X extends Throwable> long orElseGet(LongProvider<? extends X> provider) throws X;

	public abstract <X extends Throwable, Y extends Throwable> long orElseThrow(
			Provider<? extends X, ? extends Y> provider) throws X, Y;

	public abstract <X extends Throwable> void ifPresent(LongConsumer<? extends X> consumer) throws X;

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

	public static MaybeLong just(long value) {
		return new Just(value);
	}

	public static MaybeLong nothing() {
		return Nothing.instance();
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
		public <X extends Throwable> MaybeLong filter(LongToBooleanFunction<? extends X> predicate) throws X {
			return predicate.apply(value) ? this : nothing();
		}

		@Override
		public <X extends Throwable> MaybeBoolean mapToBoolean(LongToBooleanFunction<? extends X> function) throws X {
			return MaybeBoolean.just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeChar mapToChar(LongToCharFunction<? extends X> function) throws X {
			return MaybeChar.just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeByte mapToByte(LongToByteFunction<? extends X> function) throws X {
			return MaybeByte.just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeShort mapToShort(LongToShortFunction<? extends X> function) throws X {
			return MaybeShort.just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeInt mapToInt(LongToIntFunction<? extends X> function) throws X {
			return MaybeInt.just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeLong mapToLong(LongUnaryOperator<? extends X> function) throws X {
			return just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeFloat mapToFloat(LongToFloatFunction<? extends X> function) throws X {
			return MaybeFloat.just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeDouble mapToDouble(LongToDoubleFunction<? extends X> function) throws X {
			return MaybeDouble.just(function.apply(value));
		}

		@Override
		public <T, X extends Throwable> Maybe<T> map(LongFunction<? extends T, ? extends X> function) throws X {
			return Maybe.just(function.apply(value));
		}

		@Override
		public int hashCode() {
			return Long.hashCode(value);
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof Just && ((Just) other).value == value;
		}

	}

	private static class Nothing extends MaybeLong {

		private static final Nothing INSTANCE = new Nothing();

		private Nothing() {}

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
		public <X extends Throwable> MaybeLong filter(LongToBooleanFunction<? extends X> predicate) throws X {
			return this;
		}

		@Override
		public <X extends Throwable> MaybeBoolean mapToBoolean(LongToBooleanFunction<? extends X> function) throws X {
			return MaybeBoolean.nothing();
		}

		@Override
		public <X extends Throwable> MaybeChar mapToChar(LongToCharFunction<? extends X> function) throws X {
			return MaybeChar.nothing();
		}

		@Override
		public <X extends Throwable> MaybeByte mapToByte(LongToByteFunction<? extends X> function) throws X {
			return MaybeByte.nothing();
		}

		@Override
		public <X extends Throwable> MaybeShort mapToShort(LongToShortFunction<? extends X> function) throws X {
			return MaybeShort.nothing();
		}

		@Override
		public <X extends Throwable> MaybeInt mapToInt(LongToIntFunction<? extends X> function) throws X {
			return MaybeInt.nothing();
		}

		@Override
		public <X extends Throwable> MaybeLong mapToLong(LongUnaryOperator<? extends X> function) throws X {
			return MaybeLong.nothing();
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
		public int hashCode() {
			return 0;
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof Nothing;
		}

		public static Nothing instance() {
			return INSTANCE;
		}

	}

}
