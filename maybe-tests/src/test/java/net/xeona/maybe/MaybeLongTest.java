package net.xeona.maybe;

import org.hamcrest.Matcher;

import net.xeona.function.Consumer;
import net.xeona.function.Function;
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
import net.xeona.function.ToBooleanFunction;
import net.xeona.function.ToByteFunction;
import net.xeona.function.ToCharFunction;
import net.xeona.function.ToDoubleFunction;
import net.xeona.function.ToFloatFunction;
import net.xeona.function.ToIntFunction;
import net.xeona.function.ToLongFunction;
import net.xeona.function.ToShortFunction;
import net.xeona.function.VoidFunction;
import net.xeona.maybe.matcher.MaybeLongMatcher;

public class MaybeLongTest extends MaybeTestSuite<MaybeLong, Long> {

	@Override
	protected Long aRandomValue() {
		return RandomNumberUtility.aRandomLong();
	}

	@Override
	protected MaybeLong just(Long value) {
		return MaybeLong.justLong(value.longValue());
	}

	@Override
	protected MaybeLong nothing() {
		return MaybeLong.noLong();
	}

	@Override
	protected boolean isPresent(MaybeLong maybe) {
		return maybe.isPresent();
	}

	@Override
	protected Long get(MaybeLong maybe) {
		return Long.valueOf(maybe.get());
	}

	@Override
	protected Long orElse(MaybeLong maybe, Long other) {
		return Long.valueOf(maybe.orElse(other.longValue()));
	}

	@Override
	protected <X extends Throwable> Long orElseGet(MaybeLong maybe, Provider<? extends Long, ? extends X> provider)
			throws X {
		return Long.valueOf(maybe.orElseGet(LongProvider.fromBoxedProvider(provider)));
	}

	@Override
	protected <X extends Throwable, Y extends Throwable> Long orElseThrow(MaybeLong maybe,
			Provider<? extends X, ? extends Y> provider) throws X, Y {
		return Long.valueOf(maybe.orElseThrow(provider));
	}

	@Override
	protected <X extends Throwable> void ifPresent(MaybeLong maybe, Consumer<? super Long, ? extends X> consumer)
			throws X {
		maybe.ifPresent(LongConsumer.fromBoxedConsumer(consumer));
	}

	@Override
	protected <X extends Throwable> void ifAbsent(MaybeLong maybe, VoidFunction<? extends X> function) throws X {
		maybe.ifAbsent(function);
	}

	@Override
	protected <X extends Throwable, Y extends Throwable> void byPresence(MaybeLong maybe,
			Consumer<? super Long, ? extends X> ifPresent, VoidFunction<? extends Y> ifAbsent) throws X, Y {
		maybe.byPresence(LongConsumer.fromBoxedConsumer(ifPresent), ifAbsent);
	}

	@Override
	protected <X extends Throwable> MaybeLong filter(MaybeLong maybe,
			ToBooleanFunction<? super Long, ? extends X> predicate) throws X {
		return maybe.filter(LongToBooleanFunction.fromBoxedFunction(predicate));
	}

	@Override
	protected <X extends Throwable> MaybeBoolean mapToBoolean(MaybeLong maybe,
			ToBooleanFunction<? super Long, ? extends X> function) throws X {
		return maybe.mapToBoolean(LongToBooleanFunction.fromBoxedFunction(function));
	}

	@Override
	protected <X extends Throwable> MaybeChar mapToChar(MaybeLong maybe,
			ToCharFunction<? super Long, ? extends X> function) throws X {
		return maybe.mapToChar(LongToCharFunction.fromBoxedFunction(function));
	}

	@Override
	protected <X extends Throwable> MaybeByte mapToByte(MaybeLong maybe,
			ToByteFunction<? super Long, ? extends X> function) throws X {
		return maybe.mapToByte(LongToByteFunction.fromBoxedFunction(function));
	}

	@Override
	protected <X extends Throwable> MaybeShort mapToShort(MaybeLong maybe,
			ToShortFunction<? super Long, ? extends X> function) throws X {
		return maybe.mapToShort(LongToShortFunction.fromBoxedFunction(function));
	}

	@Override
	protected <X extends Throwable> MaybeInt mapToInt(MaybeLong maybe,
			ToIntFunction<? super Long, ? extends X> function) throws X {
		return maybe.mapToInt(LongToIntFunction.fromBoxedFunction(function));
	}

	@Override
	protected <X extends Throwable> MaybeLong mapToLong(MaybeLong maybe,
			ToLongFunction<? super Long, ? extends X> function) throws X {
		return maybe.mapToLong(LongUnaryOperator.fromBoxedFunction(function));
	}

	@Override
	protected <X extends Throwable> MaybeFloat mapToFloat(MaybeLong maybe,
			ToFloatFunction<? super Long, ? extends X> function) throws X {
		return maybe.mapToFloat(LongToFloatFunction.fromBoxedFunction(function));
	}

	@Override
	protected <X extends Throwable> MaybeDouble mapToDouble(MaybeLong maybe,
			ToDoubleFunction<? super Long, ? extends X> function) throws X {
		return maybe.mapToDouble(LongToDoubleFunction.fromUnboxedFunction(function));
	}

	@Override
	protected <T, X extends Throwable> Maybe<T> map(MaybeLong maybe,
			Function<? super Long, ? extends T, ? extends X> function) throws X {
		return maybe.map(LongFunction.fromBoxedFunction(function));
	}

	@Override
	protected Matcher<? super MaybeLong> justMatcher(Long value) {
		return MaybeLongMatcher.isJustLong(value.longValue());
	}

	@Override
	protected Matcher<? super MaybeLong> nothingMatcher() {
		return MaybeLongMatcher.isNoLong();
	}

}
