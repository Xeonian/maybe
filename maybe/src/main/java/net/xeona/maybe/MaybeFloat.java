package net.xeona.maybe;

import java.util.NoSuchElementException;

import net.xeona.function.FloatConsumer;
import net.xeona.function.FloatFunction;
import net.xeona.function.FloatProvider;
import net.xeona.function.FloatToBooleanFunction;
import net.xeona.function.FloatToByteFunction;
import net.xeona.function.FloatToCharFunction;
import net.xeona.function.FloatToDoubleFunction;
import net.xeona.function.FloatToIntFunction;
import net.xeona.function.FloatToLongFunction;
import net.xeona.function.FloatToShortFunction;
import net.xeona.function.FloatUnaryOperator;
import net.xeona.function.Provider;

public abstract class MaybeFloat {

	private MaybeFloat() {}

	public abstract boolean isPresent();

	public abstract float get();

	public abstract float orElse(float other);

	public abstract <X extends Throwable> float orElseGet(FloatProvider<? extends X> provider) throws X;

	public abstract <X extends Throwable, Y extends Throwable> float orElseThrow(
			Provider<? extends X, ? extends Y> provider) throws X, Y;

	public abstract <X extends Throwable> void ifPresent(FloatConsumer<? extends X> consumer) throws X;

	public abstract <X extends Throwable> MaybeFloat filter(FloatToBooleanFunction<? extends X> predicate) throws X;

	public abstract <X extends Throwable> MaybeBoolean mapToBoolean(FloatToBooleanFunction<? extends X> function)
			throws X;

	public abstract <X extends Throwable> MaybeChar mapToChar(FloatToCharFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeByte mapToByte(FloatToByteFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeShort mapToShort(FloatToShortFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeInt mapToInt(FloatToIntFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeLong mapToLong(FloatToLongFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeFloat mapToFloat(FloatUnaryOperator<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeDouble mapToDouble(FloatToDoubleFunction<? extends X> function) throws X;

	public abstract <R, X extends Throwable> Maybe<R> map(FloatFunction<? extends R, ? extends X> function) throws X;

	public static MaybeFloat justFloat(float value) {
		return new Just(value);
	}

	public static MaybeFloat nothing() {
		return Nothing.instance();
	}

	private static class Just extends MaybeFloat {

		private final float value;

		private Just(float value) {
			this.value = value;
		}

		@Override
		public boolean isPresent() {
			return true;
		}

		@Override
		public float get() {
			return value;
		}

		@Override
		public float orElse(float other) {
			return value;
		}

		@Override
		public <X extends Throwable> float orElseGet(FloatProvider<? extends X> provider) {
			return value;
		}

		@Override
		public <X extends Throwable, Y extends Throwable> float orElseThrow(
				Provider<? extends X, ? extends Y> provider) {
			return value;
		}

		@Override
		public <X extends Throwable> void ifPresent(FloatConsumer<? extends X> consumer) throws X {
			consumer.consume(value);
		}

		@Override
		public <X extends Throwable> MaybeFloat filter(FloatToBooleanFunction<? extends X> predicate) throws X {
			return predicate.apply(value) ? this : nothing();
		}

		@Override
		public <X extends Throwable> MaybeBoolean mapToBoolean(FloatToBooleanFunction<? extends X> function) throws X {
			return MaybeBoolean.justBoolean(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeChar mapToChar(FloatToCharFunction<? extends X> function) throws X {
			return MaybeChar.justChar(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeByte mapToByte(FloatToByteFunction<? extends X> function) throws X {
			return MaybeByte.justByte(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeShort mapToShort(FloatToShortFunction<? extends X> function) throws X {
			return MaybeShort.justShort(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeInt mapToInt(FloatToIntFunction<? extends X> function) throws X {
			return MaybeInt.justInt(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeLong mapToLong(FloatToLongFunction<? extends X> function) throws X {
			return MaybeLong.justLong(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeFloat mapToFloat(FloatUnaryOperator<? extends X> function) throws X {
			return justFloat(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeDouble mapToDouble(FloatToDoubleFunction<? extends X> function) throws X {
			return MaybeDouble.justDouble(function.apply(value));
		}

		@Override
		public <R, X extends Throwable> Maybe<R> map(FloatFunction<? extends R, ? extends X> function) throws X {
			return Maybe.maybe(function.apply(value));
		}

		@Override
		public int hashCode() {
			return Float.hashCode(value);
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof Just && ((Just) other).value == value;
		}

	}

	private static class Nothing extends MaybeFloat {

		private static final Nothing INSTANCE = new Nothing();

		private Nothing() {}

		@Override
		public boolean isPresent() {
			return false;
		}

		@Override
		public float get() {
			throw new NoSuchElementException();
		}

		@Override
		public float orElse(float other) {
			return other;
		}

		@Override
		public <X extends Throwable> float orElseGet(FloatProvider<? extends X> provider) throws X {
			return provider.get();
		}

		@Override
		public <X extends Throwable, Y extends Throwable> float orElseThrow(Provider<? extends X, ? extends Y> provider)
				throws X, Y {
			throw provider.get();
		}

		@Override
		public <X extends Throwable> void ifPresent(FloatConsumer<? extends X> consumer) throws X {}

		@Override
		public <X extends Throwable> MaybeFloat filter(FloatToBooleanFunction<? extends X> predicate) throws X {
			return this;
		}

		@Override
		public <X extends Throwable> MaybeBoolean mapToBoolean(FloatToBooleanFunction<? extends X> function) throws X {
			return MaybeBoolean.noBoolean();
		}

		@Override
		public <X extends Throwable> MaybeChar mapToChar(FloatToCharFunction<? extends X> function) throws X {
			return MaybeChar.nothing();
		}

		@Override
		public <X extends Throwable> MaybeByte mapToByte(FloatToByteFunction<? extends X> function) throws X {
			return MaybeByte.noByte();
		}

		@Override
		public <X extends Throwable> MaybeShort mapToShort(FloatToShortFunction<? extends X> function) throws X {
			return MaybeShort.noShort();
		}

		@Override
		public <X extends Throwable> MaybeInt mapToInt(FloatToIntFunction<? extends X> function) throws X {
			return MaybeInt.noInt();
		}

		@Override
		public <X extends Throwable> MaybeLong mapToLong(FloatToLongFunction<? extends X> function) throws X {
			return MaybeLong.noLong();
		}

		@Override
		public <X extends Throwable> MaybeFloat mapToFloat(FloatUnaryOperator<? extends X> function) throws X {
			return this;
		}

		@Override
		public <X extends Throwable> MaybeDouble mapToDouble(FloatToDoubleFunction<? extends X> function) throws X {
			return MaybeDouble.nothing();
		}

		@Override
		public <R, X extends Throwable> Maybe<R> map(FloatFunction<? extends R, ? extends X> function) throws X {
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
