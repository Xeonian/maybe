package net.xeona.maybe;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.OptionalDouble;

import net.xeona.function.DoubleConsumer;
import net.xeona.function.DoubleFunction;
import net.xeona.function.DoubleProvider;
import net.xeona.function.DoubleToBooleanFunction;
import net.xeona.function.DoubleToByteFunction;
import net.xeona.function.DoubleToCharFunction;
import net.xeona.function.DoubleToFloatFunction;
import net.xeona.function.DoubleToIntFunction;
import net.xeona.function.DoubleToLongFunction;
import net.xeona.function.DoubleToShortFunction;
import net.xeona.function.DoubleUnaryOperator;
import net.xeona.function.Provider;

public abstract class MaybeDouble implements Serializable {

	private static final long serialVersionUID = 1L;

	private MaybeDouble() {}

	public abstract boolean isPresent();

	public abstract double get();

	public abstract double orElse(double other);

	public abstract <X extends Throwable> double orElseGet(DoubleProvider<? extends X> provider) throws X;

	public abstract <X extends Throwable, Y extends Throwable> double orElseThrow(
			Provider<? extends X, ? extends Y> provider) throws X, Y;

	public abstract <X extends Throwable> void ifPresent(DoubleConsumer<? extends X> consumer) throws X;

	public abstract <X extends Throwable> MaybeDouble filter(DoubleToBooleanFunction<? extends X> predicate) throws X;

	public abstract <X extends Throwable> MaybeBoolean mapToBoolean(DoubleToBooleanFunction<? extends X> function)
			throws X;

	public abstract <X extends Throwable> MaybeChar mapToChar(DoubleToCharFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeByte mapToByte(DoubleToByteFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeShort mapToShort(DoubleToShortFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeInt mapToInt(DoubleToIntFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeLong mapToLong(DoubleToLongFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeFloat mapToFloat(DoubleToFloatFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeDouble mapToDouble(DoubleUnaryOperator<? extends X> function) throws X;

	public abstract <R, X extends Throwable> Maybe<R> map(DoubleFunction<? extends R, ? extends X> function) throws X;

	public static MaybeDouble just(double value) {
		return new Just(value);
	}

	public static MaybeDouble nothing() {
		return Nothing.instance();
	}

	public static MaybeDouble fromOptionalDouble(OptionalDouble optionalDouble) {
		return optionalDouble.isPresent() ? just(optionalDouble.getAsDouble()) : nothing();
	}

	public static OptionalDouble toOptionalDouble(MaybeDouble maybeDouble) {
		return maybeDouble.map(OptionalDouble::of).orElseGet(OptionalDouble::empty);
	}

	private static class Just extends MaybeDouble {

		private static final long serialVersionUID = 1L;

		private final double value;

		private Just(double value) {
			this.value = value;
		}

		@Override
		public boolean isPresent() {
			return true;
		}

		@Override
		public double get() {
			return value;
		}

		@Override
		public double orElse(double other) {
			return value;
		}

		@Override
		public <X extends Throwable> double orElseGet(DoubleProvider<? extends X> provider) {
			return value;
		}

		@Override
		public <X extends Throwable, Y extends Throwable> double orElseThrow(
				Provider<? extends X, ? extends Y> provider) {
			return value;
		}

		@Override
		public <X extends Throwable> void ifPresent(DoubleConsumer<? extends X> consumer) throws X {
			consumer.consume(value);
		}

		@Override
		public <X extends Throwable> MaybeDouble filter(DoubleToBooleanFunction<? extends X> predicate) throws X {
			return predicate.apply(value) ? this : nothing();
		}

		@Override
		public <X extends Throwable> MaybeBoolean mapToBoolean(DoubleToBooleanFunction<? extends X> function) throws X {
			return MaybeBoolean.just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeChar mapToChar(DoubleToCharFunction<? extends X> function) throws X {
			return MaybeChar.just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeByte mapToByte(DoubleToByteFunction<? extends X> function) throws X {
			return MaybeByte.just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeShort mapToShort(DoubleToShortFunction<? extends X> function) throws X {
			return MaybeShort.just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeInt mapToInt(DoubleToIntFunction<? extends X> function) throws X {
			return MaybeInt.just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeLong mapToLong(DoubleToLongFunction<? extends X> function) throws X {
			return MaybeLong.just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeFloat mapToFloat(DoubleToFloatFunction<? extends X> function) throws X {
			return MaybeFloat.just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeDouble mapToDouble(DoubleUnaryOperator<? extends X> function) throws X {
			return MaybeDouble.just(function.apply(value));
		}

		@Override
		public <R, X extends Throwable> Maybe<R> map(DoubleFunction<? extends R, ? extends X> function) throws X {
			return Maybe.maybe(function.apply(value));
		}

		@Override
		public int hashCode() {
			return Double.hashCode(value);
		}

		public boolean equals(Object other) {
			return other instanceof Just && ((Just) other).value == value;
		}

	}

	private static class Nothing extends MaybeDouble {

		private static final long serialVersionUID = 1L;

		private static final Nothing INSTANCE = new Nothing();

		private Nothing() {}

		@Override
		public boolean isPresent() {
			return false;
		}

		@Override
		public double get() {
			throw new NoSuchElementException();
		}

		@Override
		public double orElse(double other) {
			return other;
		}

		@Override
		public <X extends Throwable> double orElseGet(DoubleProvider<? extends X> provider) throws X {
			return provider.get();
		}

		@Override
		public <X extends Throwable, Y extends Throwable> double orElseThrow(
				Provider<? extends X, ? extends Y> provider) throws X, Y {
			throw provider.get();
		}

		@Override
		public <X extends Throwable> void ifPresent(DoubleConsumer<? extends X> consumer) throws X {}

		@Override
		public <X extends Throwable> MaybeDouble filter(DoubleToBooleanFunction<? extends X> predicate) throws X {
			return this;
		}

		@Override
		public <X extends Throwable> MaybeBoolean mapToBoolean(DoubleToBooleanFunction<? extends X> function) throws X {
			return MaybeBoolean.nothing();
		}

		@Override
		public <X extends Throwable> MaybeChar mapToChar(DoubleToCharFunction<? extends X> function) throws X {
			return MaybeChar.nothing();
		}

		@Override
		public <X extends Throwable> MaybeByte mapToByte(DoubleToByteFunction<? extends X> function) throws X {
			return MaybeByte.nothing();
		}

		@Override
		public <X extends Throwable> MaybeShort mapToShort(DoubleToShortFunction<? extends X> function) throws X {
			return MaybeShort.nothing();
		}

		@Override
		public <X extends Throwable> MaybeInt mapToInt(DoubleToIntFunction<? extends X> function) throws X {
			return MaybeInt.nothing();
		}

		@Override
		public <X extends Throwable> MaybeLong mapToLong(DoubleToLongFunction<? extends X> function) throws X {
			return MaybeLong.nothing();
		}

		@Override
		public <X extends Throwable> MaybeFloat mapToFloat(DoubleToFloatFunction<? extends X> function) throws X {
			return MaybeFloat.nothing();
		}

		@Override
		public <X extends Throwable> MaybeDouble mapToDouble(DoubleUnaryOperator<? extends X> function) throws X {
			return this;
		}

		@Override
		public <R, X extends Throwable> Maybe<R> map(DoubleFunction<? extends R, ? extends X> function) throws X {
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
