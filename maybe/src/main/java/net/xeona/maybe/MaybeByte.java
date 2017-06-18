package net.xeona.maybe;

import static java.util.Objects.requireNonNull;
import static net.xeona.maybe.MaybeBoolean.justBoolean;

import net.xeona.function.ByteConsumer;
import net.xeona.function.ByteFunction;
import net.xeona.function.ByteProvider;
import net.xeona.function.ByteToBooleanFunction;
import net.xeona.function.ByteToCharFunction;
import net.xeona.function.ByteToIntFunction;
import net.xeona.function.ByteUnaryOperator;
import net.xeona.function.Provider;

public abstract class MaybeByte {

	public abstract boolean isPresent();

	public abstract byte get();

	public abstract byte orElse(byte other);

	public abstract <X extends Throwable> byte orElseGet(ByteProvider<? extends X> provider) throws X;

	public abstract <X extends Throwable, Y extends Throwable> byte orElseThrow(
			Provider<? extends X, ? extends Y> provider) throws X, Y;

	public abstract <X extends Throwable> void ifPresent(ByteConsumer<? extends X> consumer) throws X;

	public abstract <X extends Throwable> MaybeByte filter(ByteToBooleanFunction<? extends X> predicate);

	public abstract <X extends Throwable> MaybeBoolean mapToBoolean(ByteToBooleanFunction<? extends X> function)
			throws X;

	public abstract <X extends Throwable> MaybeChar mapToChar(ByteToCharFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeByte mapToByte(ByteUnaryOperator<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeInt mapToInt(ByteToIntFunction<? extends X> function) throws X;

	public abstract <T, X extends Throwable> Maybe<T> map(ByteFunction<? extends T, ? extends X> function) throws X;

	public static MaybeByte justByte(byte value) {
		return new JustByte(value);
	}

	public static MaybeByte noByte() {
		return NoByte.instance();
	}

	public static class JustByte extends MaybeByte {

		private final byte value;

		private JustByte(byte value) {
			this.value = value;
		}

		@Override
		public boolean isPresent() {
			return true;
		}

		@Override
		public byte get() {
			return value;
		}

		@Override
		public byte orElse(byte other) {
			return value;
		}

		@Override
		public <X extends Throwable> byte orElseGet(ByteProvider<? extends X> provider) {
			return value;
		}

		@Override
		public <X extends Throwable, Y extends Throwable> byte orElseThrow(Provider<? extends X, ? extends Y> provider)
				throws X, Y {
			return value;
		}

		@Override
		public <X extends Throwable> void ifPresent(ByteConsumer<? extends X> consumer) throws X {
			// TODO Auto-generated method stub

		}

		@Override
		public <X extends Throwable> MaybeByte filter(ByteToBooleanFunction<? extends X> predicate) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <X extends Throwable> MaybeBoolean mapToBoolean(ByteToBooleanFunction<? extends X> function) throws X {
			return justBoolean(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <X extends Throwable> MaybeChar mapToChar(ByteToCharFunction<? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <X extends Throwable> MaybeByte mapToByte(ByteUnaryOperator<? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <X extends Throwable> MaybeInt mapToInt(ByteToIntFunction<? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T, X extends Throwable> Maybe<T> map(ByteFunction<? extends T, ? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int hashCode() {
			return Byte.hashCode(value);
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof JustByte && ((JustByte) other).value == value;
		}

	}

	public static class NoByte extends MaybeByte {

		private static final NoByte INSTANCE = new NoByte();

		@Override
		public boolean isPresent() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public byte get() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public byte orElse(byte other) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public <X extends Throwable> byte orElseGet(ByteProvider<? extends X> provider) throws X {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public <X extends Throwable, Y extends Throwable> byte orElseThrow(Provider<? extends X, ? extends Y> provider)
				throws X, Y {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public <X extends Throwable> void ifPresent(ByteConsumer<? extends X> consumer) throws X {
			// TODO Auto-generated method stub

		}

		@Override
		public <X extends Throwable> MaybeByte filter(ByteToBooleanFunction<? extends X> predicate) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <X extends Throwable> MaybeBoolean mapToBoolean(ByteToBooleanFunction<? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <X extends Throwable> MaybeChar mapToChar(ByteToCharFunction<? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <X extends Throwable> MaybeByte mapToByte(ByteUnaryOperator<? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <X extends Throwable> MaybeInt mapToInt(ByteToIntFunction<? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T, X extends Throwable> Maybe<T> map(ByteFunction<? extends T, ? extends X> function) throws X {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof NoByte;
		}

		public static NoByte instance() {
			return INSTANCE;
		}

	}

}
