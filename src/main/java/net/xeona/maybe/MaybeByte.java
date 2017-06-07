package net.xeona.maybe;

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

	public abstract <X extends Throwable> char orElseGet(ByteProvider<? extends X> provider) throws X;

	public abstract <X extends Throwable, Y extends Throwable> char orElseThrow(
			Provider<? extends X, ? extends Y> provider) throws X, Y;

	public abstract <X extends Throwable> void ifPresent(ByteConsumer<? extends X> consumer) throws X;

	public abstract <X extends Throwable> MaybeByte filter(ByteToBooleanFunction<? extends X> predicate);

	public abstract <X extends Throwable> MaybeBoolean mapToBoolean(ByteToBooleanFunction<? extends X> function)
			throws X;

	public abstract <X extends Throwable> MaybeChar mapToChar(ByteToCharFunction<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeByte mapToByte(ByteUnaryOperator<? extends X> function) throws X;

	public abstract <X extends Throwable> MaybeInt mapToInt(ByteToIntFunction<? extends X> function) throws X;

	public abstract <T, X extends Throwable> Maybe<T> map(ByteFunction<? extends T, ? extends X> function) throws X;

	public static MaybeByte just(byte value) {
		// TODO Auto-generated method stub
		return null;
	}

	public static MaybeByte nothing() {
		// TODO Auto-generated method stub
		return null;
	}

}
