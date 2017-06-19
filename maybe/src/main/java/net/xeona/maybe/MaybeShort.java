package net.xeona.maybe;

import static java.util.Objects.requireNonNull;
import static net.xeona.maybe.MaybeBoolean.justBoolean;

import net.xeona.function.Provider;
import net.xeona.function.ShortConsumer;
import net.xeona.function.ShortFunction;
import net.xeona.function.ShortProvider;
import net.xeona.function.ShortToBooleanFunction;
import net.xeona.function.ShortToByteFunction;
import net.xeona.function.ShortToCharFunction;
import net.xeona.function.ShortToIntFunction;
import net.xeona.function.ShortUnaryOperator;

public abstract class MaybeShort {

	public abstract boolean isPresent();

	public abstract short get();

	public abstract short orElse(short other);

	public abstract <X extends Throwable> short orElseGet(ShortProvider<? extends X> provider) throws X;

	public abstract <X extends Throwable, Y extends Throwable> short orElseThrow(
			Provider<? extends X, ? extends Y> provider) throws X, Y;

	public abstract <X extends Throwable> void ifPresent(ShortConsumer<? extends X> consumer) throws X;

	public abstract <X extends Throwable> MaybeShort filter(ShortToBooleanFunction<? extends X> predicate);

	public abstract <X extends Throwable> MaybeBoolean mapToBoolean(ShortToBooleanFunction<? extends X> function)
			throws X;

	public abstract <X extends Throwable> MaybeChar mapToChar(ShortToCharFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeByte mapToByte(ShortToByteFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeShort mapToShort(ShortUnaryOperator<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeInt mapToInt(ShortToIntFunction<? extends X> function) throws X;

	public abstract <T, X extends Throwable> Maybe<T> map(ShortFunction<? extends T, ? extends X> function) throws X;

	public static MaybeShort justShort(short value) {
		return new JustShort(value);
	}

	public static MaybeShort noShort() {
		return NoShort.instance();
	}

	public static class JustShort extends MaybeShort {

		private final short value;

		public JustShort(short value) {
			this.value = value;
		}

		@Override
		public boolean isPresent() {
			return true;
		}

		@Override
		public short get() {
			return value;
		}

		@Override
		public short orElse(short other) {
			return value;
		}

		@Override
		public <X extends Throwable> short orElseGet(ShortProvider<? extends X> provider) {
			return value;
		}

		@Override
		public <X extends Throwable, Y extends Throwable> short orElseThrow(
				Provider<? extends X, ? extends Y> provider) {
			return value;
		}

		@Override
		public <X extends Throwable> void ifPresent(ShortConsumer<? extends X> consumer) throws X {
			// TODO Auto-generated method stub

		}

		@Override
		public <X extends Throwable> MaybeShort filter(ShortToBooleanFunction<? extends X> predicate) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <X extends Throwable> MaybeBoolean mapToBoolean(ShortToBooleanFunction<? extends X> function) throws X {
			return justBoolean(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <X extends Throwable> MaybeChar mapToChar(ShortToCharFunction<? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <X extends Throwable> MaybeByte mapToByte(ShortToByteFunction<? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <X extends Throwable> MaybeShort mapToShort(ShortUnaryOperator<? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <X extends Throwable> MaybeInt mapToInt(ShortToIntFunction<? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T, X extends Throwable> Maybe<T> map(ShortFunction<? extends T, ? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int hashCode() {
			return Short.hashCode(value);
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof JustShort && ((JustShort) other).value == value;
		}

	}

	public static class NoShort extends MaybeShort {

		private static final NoShort INSTANCE = new NoShort();

		@Override
		public boolean isPresent() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public short get() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public short orElse(short other) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public <X extends Throwable> short orElseGet(ShortProvider<? extends X> provider) throws X {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public <X extends Throwable, Y extends Throwable> short orElseThrow(Provider<? extends X, ? extends Y> provider)
				throws X, Y {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public <X extends Throwable> void ifPresent(ShortConsumer<? extends X> consumer) throws X {
			// TODO Auto-generated method stub

		}

		@Override
		public <X extends Throwable> MaybeShort filter(ShortToBooleanFunction<? extends X> predicate) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <X extends Throwable> MaybeBoolean mapToBoolean(ShortToBooleanFunction<? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <X extends Throwable> MaybeChar mapToChar(ShortToCharFunction<? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <X extends Throwable> MaybeByte mapToByte(ShortToByteFunction<? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <X extends Throwable> MaybeShort mapToShort(ShortUnaryOperator<? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <X extends Throwable> MaybeInt mapToInt(ShortToIntFunction<? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T, X extends Throwable> Maybe<T> map(ShortFunction<? extends T, ? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof NoShort;
		}

		public static NoShort instance() {
			return INSTANCE;
		}

	}

}
