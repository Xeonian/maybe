package net.xeona.maybe;

import java.io.Serializable;
import java.util.NoSuchElementException;

import net.xeona.function.CharConsumer;
import net.xeona.function.CharFunction;
import net.xeona.function.CharProvider;
import net.xeona.function.CharToBooleanFunction;
import net.xeona.function.CharToByteFunction;
import net.xeona.function.CharToDoubleFunction;
import net.xeona.function.CharToFloatFunction;
import net.xeona.function.CharToIntFunction;
import net.xeona.function.CharToLongFunction;
import net.xeona.function.CharToShortFunction;
import net.xeona.function.CharUnaryOperator;
import net.xeona.function.Provider;

public abstract class MaybeChar implements Serializable {

	private static final long serialVersionUID = 1L;

	public abstract boolean isPresent();

	public abstract char get();

	public abstract char orElse(char other);

	public abstract <X extends Throwable> char orElseGet(CharProvider<? extends X> provider) throws X;

	public abstract <X extends Throwable> char orElseThrow(Provider<? extends X, RuntimeException> provider) throws X;

	public abstract <X extends Throwable> void ifPresent(CharConsumer<? extends X> consumer) throws X;

	public abstract <X extends Throwable> MaybeChar filter(CharToBooleanFunction<? extends X> predicate) throws X;

	public abstract <X extends Throwable> MaybeBoolean mapToBoolean(CharToBooleanFunction<? extends X> function)
			throws X;

	public abstract <X extends Throwable> MaybeChar mapToChar(CharUnaryOperator<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeByte mapToByte(CharToByteFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeShort mapToShort(CharToShortFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeInt mapToInt(CharToIntFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeLong mapToLong(CharToLongFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeFloat mapToFloat(CharToFloatFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeDouble mapToDouble(CharToDoubleFunction<? extends X> function) throws X;

	public abstract <T, X extends Throwable> Maybe<T> map(CharFunction<? extends T, ? extends X> function) throws X;

	public static MaybeChar just(char value) {
		return new Just(value);
	}

	public static MaybeChar nothing() {
		return Nothing.instance();
	}

	private static class Just extends MaybeChar {

		private static final long serialVersionUID = 1L;

		private final char value;

		private Just(char value) {
			this.value = value;
		}

		@Override
		public boolean isPresent() {
			return true;
		}

		@Override
		public char get() {
			return value;
		}

		@Override
		public char orElse(char other) {
			return value;
		}

		@Override
		public <X extends Throwable> char orElseGet(CharProvider<? extends X> provider) throws X {
			return value;
		}

		@Override
		public <X extends Throwable> char orElseThrow(Provider<? extends X, RuntimeException> provider) throws X {
			return value;
		}

		@Override
		public <X extends Throwable> void ifPresent(CharConsumer<? extends X> consumer) throws X {
			consumer.consume(value);
		}

		@Override
		public <X extends Throwable> MaybeChar filter(CharToBooleanFunction<? extends X> predicate) throws X {
			return predicate.apply(value) ? this : nothing();
		}

		@Override
		public <X extends Throwable> MaybeBoolean mapToBoolean(CharToBooleanFunction<? extends X> function) throws X {
			return MaybeBoolean.just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeChar mapToChar(CharUnaryOperator<? extends X> function) throws X {
			return just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeByte mapToByte(CharToByteFunction<? extends X> function) throws X {
			return MaybeByte.just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeShort mapToShort(CharToShortFunction<? extends X> function) throws X {
			return MaybeShort.just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeInt mapToInt(CharToIntFunction<? extends X> function) throws X {
			return MaybeInt.just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeLong mapToLong(CharToLongFunction<? extends X> function) throws X {
			return MaybeLong.just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeFloat mapToFloat(CharToFloatFunction<? extends X> function) throws X {
			return MaybeFloat.just(function.apply(value));
		}

		@Override
		public <X extends Throwable> MaybeDouble mapToDouble(CharToDoubleFunction<? extends X> function) throws X {
			return MaybeDouble.just(function.apply(value));
		}

		@Override
		public <T, X extends Throwable> Maybe<T> map(CharFunction<? extends T, ? extends X> function) throws X {
			return Maybe.just(function.apply(value));
		}

		@Override
		public int hashCode() {
			return Character.hashCode(value);
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof Just && value == ((Just) other).value;
		}

	}

	private static class Nothing extends MaybeChar {

		private static final long serialVersionUID = 1L;

		private static final Nothing INSTANCE = new Nothing();

		@Override
		public boolean isPresent() {
			return false;
		}

		@Override
		public char get() {
			throw new NoSuchElementException();
		}

		@Override
		public char orElse(char other) {
			return other;
		}

		@Override
		public <X extends Throwable> char orElseGet(CharProvider<? extends X> provider) throws X {
			return provider.get();
		}

		@Override
		public <X extends Throwable> char orElseThrow(Provider<? extends X, RuntimeException> provider) throws X {
			throw provider.get();
		}

		@Override
		public <X extends Throwable> void ifPresent(CharConsumer<? extends X> consumer) throws X {}

		@Override
		public <X extends Throwable> MaybeChar filter(CharToBooleanFunction<? extends X> predicate) throws X {
			return this;
		}

		@Override
		public <X extends Throwable> MaybeBoolean mapToBoolean(CharToBooleanFunction<? extends X> function) throws X {
			return MaybeBoolean.nothing();
		}

		@Override
		public <X extends Throwable> MaybeChar mapToChar(CharUnaryOperator<? extends X> function) throws X {
			return MaybeChar.nothing();
		}

		@Override
		public <X extends Throwable> MaybeByte mapToByte(CharToByteFunction<? extends X> function) throws X {
			return MaybeByte.nothing();
		}

		@Override
		public <X extends Throwable> MaybeShort mapToShort(CharToShortFunction<? extends X> function) throws X {
			return MaybeShort.nothing();
		}

		@Override
		public <X extends Throwable> MaybeInt mapToInt(CharToIntFunction<? extends X> function) throws X {
			return MaybeInt.nothing();
		}

		@Override
		public <X extends Throwable> MaybeLong mapToLong(CharToLongFunction<? extends X> function) throws X {
			return MaybeLong.nothing();
		}

		@Override
		public <X extends Throwable> MaybeFloat mapToFloat(CharToFloatFunction<? extends X> function) throws X {
			return MaybeFloat.nothing();
		}

		@Override
		public <X extends Throwable> MaybeDouble mapToDouble(CharToDoubleFunction<? extends X> function) throws X {
			return MaybeDouble.nothing();
		}

		@Override
		public <T, X extends Throwable> Maybe<T> map(CharFunction<? extends T, ? extends X> function) throws X {
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
