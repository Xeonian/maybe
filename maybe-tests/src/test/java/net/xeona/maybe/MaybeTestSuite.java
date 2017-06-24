package net.xeona.maybe;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.RandomUtils;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;

import net.xeona.function.BinaryConsumer;
import net.xeona.function.BinaryFunction;
import net.xeona.function.Consumer;
import net.xeona.function.Function;
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
import net.xeona.maybe.matcher.MaybeBooleanMatcher;
import net.xeona.maybe.matcher.MaybeByteMatcher;
import net.xeona.maybe.matcher.MaybeCharMatcher;
import net.xeona.maybe.matcher.MaybeDoubleMatcher;
import net.xeona.maybe.matcher.MaybeFloatMatcher;
import net.xeona.maybe.matcher.MaybeIntMatcher;
import net.xeona.maybe.matcher.MaybeLongMatcher;
import net.xeona.maybe.matcher.MaybeMatcher;
import net.xeona.maybe.matcher.MaybeShortMatcher;

@SuppressWarnings("unchecked")
public abstract class MaybeTestSuite<M extends Collection<? super T>, T> {

	protected MaybeTestSuite() {}

	@Test
	public void justIsPresent() {
		assertTrue(isPresent(just(aRandomValue())));
	}

	@Test
	public void getOnJustReturnsValue() {
		extractValueFunctionOnJustReturnsValue(this::get);
	}

	@Test
	public void orElseOnJustReturnsValue() {
		extractValueFunctionOnJustReturnsValue(maybe -> orElse(maybe, aRandomValue()));
	}

	@Test
	public void orElseGetOnJustReturnsValue() {
		extractValueFunctionOnJustReturnsValue(
				maybe -> orElseGet(maybe, (Provider<T, RuntimeException>) mock(Provider.class)));
	}

	@Test
	public void orElseGetOnJustDoesNotInvokeProvider() {
		this.<T> orElseProvideFunctionOnJustDoesNotInvokeProvider(this::orElseGet);
	}

	@Test
	public void orElseThrowOnJustReturnsValue() {
		extractValueFunctionOnJustReturnsValue(
				maybe -> orElseThrow(maybe, (Provider<RuntimeException, RuntimeException>) mock(Provider.class)));
	}

	@Test
	public void orElseThrowOnJustDoesNotInvokeProvider() {
		this.<RuntimeException> orElseProvideFunctionOnJustDoesNotInvokeProvider(this::orElseThrow);
	}

	@Test
	public void ifPresentOnJustInvokesConsumerWithValue() {
		ifPresentFunctionOnJustInvokesConsumer(this::ifPresent);
	}

	@Test
	public void ifPresentOnJustWithThrowingConsumerPropagatesThrownException() {
		ifPresentFunctionOnJustWithThrowingConsumerPropagatesThrownException(this::ifPresent);
	}

	@Test
	public void ifPresentOnJustWithNullConsumerThrowsNullPointerException() {
		ifPresentFunctionOnJustWithNullConsumerThrowsNullPointerException(this::ifPresent);
	}

	@Test
	public void ifAbsentOnJustDoesNotInvokeFunction() {
		ifAbsentFunctionOnJustDoesNotInvokeFunction(this::ifAbsent);
	}

	@Test
	public void byPresenceOnJustInvokesIfPresentConsumerWithValue() {
		ifPresentFunctionOnJustInvokesConsumer(
				(maybe, ifPresentConsumer) -> byPresence(maybe, ifPresentConsumer, mock(VoidFunction.class)));
	}

	@Test
	public void byPresenceOnJustWithThrowingIfPresentConsumerPropagatesThrownException() {
		ifPresentFunctionOnJustWithThrowingConsumerPropagatesThrownException(
				(maybe, ifPresentConsumer) -> byPresence(maybe, ifPresentConsumer, mock(VoidFunction.class)));
	}

	@Test
	public void byPresenceOnJustWithNullIfPresentConsumerThrowsNullPointerException() {
		ifPresentFunctionOnJustWithNullConsumerThrowsNullPointerException(
				(maybe, ifPresentConsumer) -> byPresence(maybe, ifPresentConsumer, mock(VoidFunction.class)));
	}

	@Test
	public void byPresenceOnJustDoesNotInvokeIfAbsentFunction() {
		ifAbsentFunctionOnJustDoesNotInvokeFunction(
				(maybe, ifAbsentFunction) -> byPresence(maybe, mock(Consumer.class), ifAbsentFunction));
	}

	@Test
	public void filterOnJustInvokesPredicateWithValue() {
		this.<ToBooleanFunction<T, RuntimeException>> functionWithDelegateFunctionOnJustInvokesDelegateFunctionWithValue(
				this::filter, ToBooleanFunction.class, ToBooleanFunction::apply);
	}

	@Test
	public void filterOnJustWithPredicateReturningTrueReturnsJustValue() {
		this.<ToBooleanFunction<T, RuntimeException>, Boolean, M> functionWithDelegateFunctionOnJustReturnsValue(
				this::filter, ToBooleanFunction.class, ToBooleanFunction::apply, () -> true,
				(value, accept) -> justMatcher(value));
	}

	@Test
	public void filterOnJustWithPredicateReturningFalseReturnsNothing() {
		this.<ToBooleanFunction<T, RuntimeException>, Boolean, M> functionWithDelegateFunctionOnJustReturnsValue(
				this::filter, ToBooleanFunction.class, ToBooleanFunction::apply, () -> false,
				(value, accept) -> nothingMatcher());
	}

	@Test
	public void filterOnJustWithThrowingPredicatePropagatesThrownException() {
		this.<ToBooleanFunction<T, RuntimeException>> functionWithThrowingDelegateFunctionOnJustPropagatesThrownException(
				this::filter, ToBooleanFunction.class, ToBooleanFunction::apply);
	}

	@Test(expected = NullPointerException.class)
	public void filterOnJustWithNullPredicateThrowsNullPointerException() {
		filter(just(aRandomValue()), null);
	}

	@Test
	public void mapToBooleanOnJustInvokesFunctionWithValue() {
		this.<ToBooleanFunction<T, RuntimeException>> functionWithDelegateFunctionOnJustInvokesDelegateFunctionWithValue(
				this::mapToBoolean, ToBooleanFunction.class, ToBooleanFunction::apply);
	}

	@Test
	public void mapToBooleanOnJustReturnsJustBooleanOfDelegateFunctionReturnValue() {
		this.<ToBooleanFunction<T, RuntimeException>, Boolean, MaybeBoolean> functionWithDelegateFunctionOnJustReturnsValue(
				this::mapToBoolean, ToBooleanFunction.class, ToBooleanFunction::apply, RandomUtils::nextBoolean,
				(value, returnValue) -> MaybeBooleanMatcher.isJustBoolean(returnValue));
	}

	@Test
	public void mapToBooleanOnJustWithThrowingDelegateFunctionPropagatesThrownException() {
		this.<ToBooleanFunction<T, RuntimeException>> functionWithThrowingDelegateFunctionOnJustPropagatesThrownException(
				this::mapToBoolean, ToBooleanFunction.class, ToBooleanFunction::apply);
	}

	@Test(expected = NullPointerException.class)
	public void mapToBooleanOnJustWithNullDelegateFunctionThrowsNullPointerException() {
		mapToBoolean(just(aRandomValue()), null);
	}

	@Test
	public void mapToCharOnJustInvokesFunctionWithValue() {
		this.<ToCharFunction<T, RuntimeException>> functionWithDelegateFunctionOnJustInvokesDelegateFunctionWithValue(
				this::mapToChar, ToCharFunction.class, ToCharFunction::apply);
	}

	@Test
	public void mapToCharOnJustReturnsJustCharOfDelegateFunctionReturnValue() {
		this.<ToCharFunction<T, RuntimeException>, Character, MaybeChar> functionWithDelegateFunctionOnJustReturnsValue(
				this::mapToChar, ToCharFunction.class, ToCharFunction::apply, RandomNumberUtility::aRandomChar,
				(value, returnValue) -> MaybeCharMatcher.isJustChar(returnValue));
	}

	@Test
	public void mapToCharOnJustWithThrowingDelegateFunctionPropagatesThrownException() {
		this.<ToCharFunction<T, RuntimeException>> functionWithThrowingDelegateFunctionOnJustPropagatesThrownException(
				this::mapToChar, ToCharFunction.class, ToCharFunction::apply);
	}

	@Test(expected = NullPointerException.class)
	public void mapToCharOnJustWithNullDelegateFunctionThrowsNullPointerException() {
		mapToChar(just(aRandomValue()), null);
	}

	@Test
	public void mapToByteOnJustInvokesFunctionWithValue() {
		this.<ToByteFunction<T, RuntimeException>> functionWithDelegateFunctionOnJustInvokesDelegateFunctionWithValue(
				this::mapToByte, ToByteFunction.class, ToByteFunction::apply);
	}

	@Test
	public void mapToByteOnJustReturnsJustByteOfDelegateFunctionReturnValue() {
		this.<ToByteFunction<T, RuntimeException>, Byte, MaybeByte> functionWithDelegateFunctionOnJustReturnsValue(
				this::mapToByte, ToByteFunction.class, ToByteFunction::apply, RandomNumberUtility::aRandomByte,
				(value, returnValue) -> MaybeByteMatcher.isJustByte(returnValue));
	}

	@Test
	public void mapToByteOnJustWithThrowingDelegateFunctionPropagatesThrownException() {
		this.<ToByteFunction<T, RuntimeException>> functionWithThrowingDelegateFunctionOnJustPropagatesThrownException(
				this::mapToByte, ToByteFunction.class, ToByteFunction::apply);
	}

	@Test(expected = NullPointerException.class)
	public void mapToByteOnJustWithNullDelegateFunctionThrowsNullPointerException() {
		mapToByte(just(aRandomValue()), null);
	}

	@Test
	public void mapToShortOnJustInvokesDelegateFunctionWithValue() {
		this.<ToShortFunction<T, RuntimeException>> functionWithDelegateFunctionOnJustInvokesDelegateFunctionWithValue(
				this::mapToShort, ToShortFunction.class, ToShortFunction::apply);
	}

	@Test
	public void mapToShortOnJustReturnsJustShortOfDelegateFunctionReturnValue() {
		this.<ToShortFunction<T, RuntimeException>, Short, MaybeShort> functionWithDelegateFunctionOnJustReturnsValue(
				this::mapToShort, ToShortFunction.class, ToShortFunction::apply, RandomNumberUtility::aRandomShort,
				(value, returnValue) -> MaybeShortMatcher.isJustShort(returnValue));
	}

	@Test
	public void mapToShortOnJustWithThrowingDelegateFunctionPropagatesThrownException() {
		this.<ToShortFunction<T, RuntimeException>> functionWithThrowingDelegateFunctionOnJustPropagatesThrownException(
				this::mapToShort, ToShortFunction.class, ToShortFunction::apply);
	}

	@Test(expected = NullPointerException.class)
	public void mapToShortOnJustWithNullDelegateFunctionThrowsNullPointerException() {
		mapToShort(just(aRandomValue()), null);
	}

	@Test
	public void mapToIntOnJustInvokesDelegateFunctionWithValue() {
		this.<ToIntFunction<T, RuntimeException>> functionWithDelegateFunctionOnJustInvokesDelegateFunctionWithValue(
				this::mapToInt, ToIntFunction.class, ToIntFunction::apply);
	}

	@Test
	public void mapToIntOnJustReturnsJustIntOfDelegateFunctionReturnValue() {
		this.<ToIntFunction<T, RuntimeException>, Integer, MaybeInt> functionWithDelegateFunctionOnJustReturnsValue(
				this::mapToInt, ToIntFunction.class, ToIntFunction::apply, RandomNumberUtility::aRandomInt,
				(value, returnValue) -> MaybeIntMatcher.isJustInt(returnValue));
	}

	@Test
	public void mapToIntOnJustWithThrowingDelegateFunctionPropagatesThrownException() {
		this.<ToIntFunction<T, RuntimeException>> functionWithThrowingDelegateFunctionOnJustPropagatesThrownException(
				this::mapToInt, ToIntFunction.class, ToIntFunction::apply);
	}

	@Test(expected = NullPointerException.class)
	public void mapToIntOnJustWithNullDelegateFunctionThrowsNullPointerException() {
		mapToInt(just(aRandomValue()), null);
	}

	@Test
	public void mapToLongOnJustInvokesDelegateFunctionWithValue() {
		this.<ToLongFunction<T, RuntimeException>> functionWithDelegateFunctionOnJustInvokesDelegateFunctionWithValue(
				this::mapToLong, ToLongFunction.class, ToLongFunction::apply);
	}

	@Test
	public void mapToLongOnJustReturnsJustLongOfDelegateFunctionReturnValue() {
		this.<ToLongFunction<T, RuntimeException>, Long, MaybeLong> functionWithDelegateFunctionOnJustReturnsValue(
				this::mapToLong, ToLongFunction.class, ToLongFunction::apply, RandomNumberUtility::aRandomLong,
				(value, returnValue) -> MaybeLongMatcher.isJustLong(returnValue));
	}

	@Test
	public void mapToLongOnJustWithThrowingDelegateFunctionPropagatesThrownException() {
		this.<ToLongFunction<T, RuntimeException>> functionWithThrowingDelegateFunctionOnJustPropagatesThrownException(
				this::mapToLong, ToLongFunction.class, ToLongFunction::apply);
	}

	@Test(expected = NullPointerException.class)
	public void mapToLongOnJustWithNullDelegateFunctionThrowsNullPointerException() {
		mapToLong(just(aRandomValue()), null);
	}

	@Test
	public void mapToFloatOnJustInvokesDelegateFunctionWithValue() {
		this.<ToFloatFunction<T, RuntimeException>> functionWithDelegateFunctionOnJustInvokesDelegateFunctionWithValue(
				this::mapToFloat, ToFloatFunction.class, ToFloatFunction::apply);
	}

	@Test
	public void mapToFloatOnJustReturnsJustFloatOfDelegateFunctionReturnValue() {
		this.<ToFloatFunction<T, RuntimeException>, Float, MaybeFloat> functionWithDelegateFunctionOnJustReturnsValue(
				this::mapToFloat, ToFloatFunction.class, ToFloatFunction::apply, RandomNumberUtility::aRandomFloat,
				(value, returnValue) -> MaybeFloatMatcher.isJustFloat(NumberMatchers.floatBinaryEqualTo(returnValue)));
	}

	@Test
	public void mapToFloatOnJustWithThrowingDelegateFunctionPropagatesThrownException() {
		this.<ToFloatFunction<T, RuntimeException>> functionWithThrowingDelegateFunctionOnJustPropagatesThrownException(
				this::mapToFloat, ToFloatFunction.class, ToFloatFunction::apply);
	}

	@Test(expected = NullPointerException.class)
	public void mapToFloatOnJustWithNullDelegateFunctionThrowsNullPointerException() {
		mapToFloat(just(aRandomValue()), null);
	}

	@Test
	public void mapToDoubleOnJustInvokesDelegateFunctionWithValue() {
		this.<ToDoubleFunction<T, RuntimeException>> functionWithDelegateFunctionOnJustInvokesDelegateFunctionWithValue(
				this::mapToDouble, ToDoubleFunction.class, ToDoubleFunction::apply);
	}

	@Test
	public void mapToDoubleOnJustReturnsJustDoubleOfDelegateFunctionReturnValue() {
		this.<ToDoubleFunction<T, RuntimeException>, Double, MaybeDouble> functionWithDelegateFunctionOnJustReturnsValue(
				this::mapToDouble, ToDoubleFunction.class, ToDoubleFunction::apply, RandomNumberUtility::aRandomDouble,
				(value, returnValue) -> MaybeDoubleMatcher
						.isJustDouble(NumberMatchers.doubleBinaryEqualTo(returnValue)));
	}

	@Test
	public void mapToDoubleOnJustWithThrowingToDoubleFunctionPropagatesThrownException() {
		this.<ToDoubleFunction<T, RuntimeException>> functionWithThrowingDelegateFunctionOnJustPropagatesThrownException(
				this::mapToDouble, ToDoubleFunction.class, ToDoubleFunction::apply);
	}

	@Test(expected = NullPointerException.class)
	public void mapToDoubleOnJustWithNullToDoubleFunctionThrowsNullPointerException() {
		mapToDouble(just(aRandomValue()), null);
	}

	@Test
	public void mapOnJustInvokesFunctionWithValue() {
		this.<Function<T, Object, RuntimeException>> functionWithDelegateFunctionOnJustInvokesDelegateFunctionWithValue(
				this::map, Function.class, Function::apply);
	}

	@Test
	public void mapOnJustWithFunctionReturningValueReturnsJustOfFunctionReturnValue() {
		this.<Function<T, Object, RuntimeException>, Object, Maybe<Object>> functionWithDelegateFunctionOnJustReturnsValue(
				this::map, Function.class, Function::apply, Object::new,
				(value, returnValue) -> MaybeMatcher.isJust(returnValue));
	}

	@Test
	public void mapOnJustWithFunctionReturningNullReturnsNothing() {
		this.<Function<T, Object, RuntimeException>, Object, Maybe<Object>> functionWithDelegateFunctionOnJustReturnsValue(
				this::map, Function.class, Function::apply, () -> null,
				(value, returnValue) -> MaybeMatcher.isNothing());
	}

	@Test
	public void mapOnJustWithThrowingFunctionPropagatesThrownException() {
		this.<Function<T, Object, RuntimeException>> functionWithThrowingDelegateFunctionOnJustPropagatesThrownException(
				this::map, Function.class, Function::apply);
	}

	@Test(expected = NullPointerException.class)
	public void mapOnJustWithNullFunctionThrowsNullPointerException() {
		map(just(aRandomValue()), null);
	}

	@Test
	public void justIsNotEmpty() {
		assertThat(just(aRandomValue()), is(not(empty())));
	}

	@Test
	public void justHasSize1() {
		assertThat(just(aRandomValue()), hasSize(1));
	}

	@Test
	public void justIntContainsOwnValue() {
		T value = aRandomValue();
		assertTrue(just(value).contains(value));
	}

	@Test
	public void nothingIsNotPresent() {
		assertFalse(isPresent(nothing()));
	}

	@Test(expected = NoSuchElementException.class)
	public void getOnNothingThrowsNoSuchElementException() {
		get(nothing());
	}

	@Test
	public void orElseOnNothingReturnsOtherValue() {
		T other = aRandomValue();
		assertThat(orElse(nothing(), other), is(other));
	}

	@Test
	public void orElseGetOnNothingInvokesProvider() {
		orElseProvideFunctionOnNothingInvokesProvider(this::orElseGet, this::aRandomValue);
	}

	@Test
	public void orElseGetOnNothingReturnsProvidedValue() {
		T providedValue = aRandomValue();
		Provider<T, RuntimeException> providerMock = mock(Provider.class);
		when(providerMock.get()).thenReturn(providedValue);
		assertThat(orElseGet(nothing(), providerMock), is(providedValue));
	}

	@Test
	public void orElseGetOnNothingWithThrowingProviderPropagatesThrownException() {
		this.<T> orElseProvideFunctionOnNothingWithThrowingProviderPropagatesThrownException(this::orElseGet);
	}

	@Test
	public void orElseThrowOnNothingInvokesProvider() {
		orElseProvideFunctionOnNothingInvokesProvider(this::orElseThrow, RuntimeException::new);
	}

	@Test
	public void orElseThrowOnNothingThrowsProvidedException() {
		RuntimeException providedException = new RuntimeException();
		Provider<RuntimeException, RuntimeException> providerMock = mock(Provider.class);
		when(providerMock.get()).thenReturn(providedException);
		try {
			orElseThrow(nothing(), providerMock);
		} catch (RuntimeException e) {
			assertThat(e, is(sameInstance(providedException)));
		}
	}

	@Test
	public void orElseThrowOnNothingWithThrowingProviderPropagatesThrownException() {
		this.<RuntimeException> orElseProvideFunctionOnNothingWithThrowingProviderPropagatesThrownException(
				this::orElseThrow);
	}

	protected abstract T aRandomValue();

	protected abstract M just(T value);

	protected abstract M nothing();

	protected abstract boolean isPresent(M maybe);

	protected abstract T get(M maybe);

	protected abstract T orElse(M maybe, T other);

	protected abstract <X extends Throwable> T orElseGet(M maybe, Provider<? extends T, ? extends X> provider) throws X;

	protected abstract <X extends Throwable, Y extends Throwable> T orElseThrow(M maybe,
			Provider<? extends X, ? extends Y> provider) throws X, Y;

	protected abstract <X extends Throwable> void ifPresent(M maybe, Consumer<? super T, ? extends X> consumer)
			throws X;

	protected abstract <X extends Throwable> void ifAbsent(M maybe, VoidFunction<? extends X> function) throws X;

	protected abstract <X extends Throwable, Y extends Throwable> void byPresence(M maybe,
			Consumer<? super T, ? extends X> ifPresent, VoidFunction<? extends Y> ifAbsent) throws X, Y;

	protected abstract <X extends Throwable> M filter(M maybe, ToBooleanFunction<? super T, ? extends X> predicate)
			throws X;

	protected abstract <X extends Throwable> MaybeBoolean mapToBoolean(M maybe,
			ToBooleanFunction<? super T, ? extends X> function) throws X;

	protected abstract <X extends Throwable> MaybeChar mapToChar(M maybe,
			ToCharFunction<? super T, ? extends X> function) throws X;

	protected abstract <X extends Throwable> MaybeByte mapToByte(M maybe,
			ToByteFunction<? super T, ? extends X> function) throws X;

	protected abstract <X extends Throwable> MaybeShort mapToShort(M maybe,
			ToShortFunction<? super T, ? extends X> function) throws X;

	protected abstract <X extends Throwable> MaybeInt mapToInt(M maybe, ToIntFunction<? super T, ? extends X> function)
			throws X;

	protected abstract <X extends Throwable> MaybeLong mapToLong(M maybe,
			ToLongFunction<? super T, ? extends X> function) throws X;

	protected abstract <X extends Throwable> MaybeFloat mapToFloat(M maybe,
			ToFloatFunction<? super T, ? extends X> function) throws X;

	protected abstract <X extends Throwable> MaybeDouble mapToDouble(M maybe,
			ToDoubleFunction<? super T, ? extends X> function) throws X;

	protected abstract <U, X extends Throwable> Maybe<U> map(M maybe,
			Function<? super T, ? extends U, ? extends X> function) throws X;

	protected abstract Matcher<? super M> justMatcher(T value);

	protected abstract Matcher<? super M> nothingMatcher();

	private void extractValueFunctionOnJustReturnsValue(
			Function<? super M, ? extends T, ? extends RuntimeException> extractFunction) {
		T value = aRandomValue();
		assertEquals(extractFunction.apply(just(value)), value);
	}

	private <U> void orElseProvideFunctionOnJustDoesNotInvokeProvider(
			BinaryFunction<? super M, ? super Provider<? extends U, ? extends RuntimeException>, ? extends T, ? extends RuntimeException> orElseProviderFunction) {
		Provider<U, RuntimeException> providerMock = mock(Provider.class);
		orElseProviderFunction.apply(just(aRandomValue()), providerMock);
		verifyNoMoreInteractions(providerMock);
	}

	private void ifPresentFunctionOnJustInvokesConsumer(
			BinaryConsumer<? super M, ? super Consumer<? super T, ? extends RuntimeException>, ? extends RuntimeException> ifPresentFunction) {
		T value = aRandomValue();
		Consumer<T, RuntimeException> consumerMock = mock(Consumer.class);
		ifPresentFunction.consume(just(value), consumerMock);
		verify(consumerMock).consume(value);
		verifyNoMoreInteractions(consumerMock);
	}

	private void ifPresentFunctionOnJustWithThrowingConsumerPropagatesThrownException(
			BinaryConsumer<? super M, ? super Consumer<? super T, ? extends RuntimeException>, ? extends RuntimeException> ifPresentFunction) {
		T value = aRandomValue();
		RuntimeException exception = new RuntimeException();
		Consumer<T, RuntimeException> consumerMock = mock(Consumer.class);
		doThrow(exception).when(consumerMock).consume(value);
		try {
			ifPresentFunction.consume(just(value), consumerMock);
		} catch (RuntimeException e) {
			assertThat(e, is(sameInstance(exception)));
		}
	}

	private void ifPresentFunctionOnJustWithNullConsumerThrowsNullPointerException(
			BinaryConsumer<? super M, ? super Consumer<? super T, ? extends RuntimeException>, ? extends RuntimeException> ifPresentFunction) {
		try {
			ifPresentFunction.consume(just(aRandomValue()), null);
			fail();
		} catch (NullPointerException e) {}
	}

	private void ifAbsentFunctionOnJustDoesNotInvokeFunction(
			BinaryConsumer<? super M, ? super VoidFunction<? extends RuntimeException>, ? extends RuntimeException> ifAbsentFunction) {
		VoidFunction<RuntimeException> functionMock = mock(VoidFunction.class);
		ifAbsentFunction.consume(just(aRandomValue()), functionMock);
		verifyNoMoreInteractions(functionMock);
	}

	private <F> void functionWithDelegateFunctionOnJustInvokesDelegateFunctionWithValue(
			BinaryConsumer<? super M, ? super F, ? extends RuntimeException> functionInvocation,
			Class<? super F> delegateFunctionClass,
			BinaryConsumer<? super F, ? super T, ? extends RuntimeException> delegateFunctionInvocation) {
		T value = aRandomValue();
		F delegateFunctionMock = (F) mock(delegateFunctionClass);
		functionInvocation.consume(just(value), delegateFunctionMock);
		delegateFunctionInvocation.consume(verify(delegateFunctionMock), value);
		verifyNoMoreInteractions(delegateFunctionMock);
	}

	private <F, R, N> void functionWithDelegateFunctionOnJustReturnsValue(
			BinaryFunction<? super M, ? super F, ? extends N, ? extends RuntimeException> functionInvocation,
			Class<? super F> delegateFunctionClass,
			BinaryFunction<? super F, ? super T, R, ? extends RuntimeException> delegateFunctionInvocation,
			Provider<? extends R, ? extends RuntimeException> delegateFunctionReturnValueProvider,
			BinaryFunction<? super T, ? super R, ? extends Matcher<? super N>, ? extends RuntimeException> returnValueMatcherFunction) {
		T value = aRandomValue();
		F delegateFunctionMock = (F) mock(delegateFunctionClass);
		R delegateFunctionReturnValue = delegateFunctionReturnValueProvider.get();
		when(delegateFunctionInvocation.apply(delegateFunctionMock, value)).thenReturn(delegateFunctionReturnValue);
		assertThat(functionInvocation.apply(just(value), delegateFunctionMock),
				returnValueMatcherFunction.apply(value, delegateFunctionReturnValue));
	}

	private <F> void functionWithThrowingDelegateFunctionOnJustPropagatesThrownException(
			BinaryConsumer<? super M, ? super F, ? extends RuntimeException> functionInvocation,
			Class<? super F> delegateFunctionClass,
			BinaryConsumer<? super F, ? super T, ? extends RuntimeException> delegateFunctionInvocation) {
		T value = aRandomValue();
		RuntimeException exception = new RuntimeException();
		F delegateFunctionMock = (F) mock(delegateFunctionClass);
		delegateFunctionInvocation.consume(doThrow(exception).when(delegateFunctionMock), value);
		try {
			functionInvocation.consume(just(value), delegateFunctionMock);
		} catch (RuntimeException e) {
			assertThat(e, is(sameInstance(exception)));
		}
	}

	private <U> void orElseProvideFunctionOnNothingInvokesProvider(
			BinaryFunction<? super M, ? super Provider<? extends U, ? extends RuntimeException>, ? extends T, ? extends RuntimeException> orElseProvideFunction,
			Provider<? extends U, ? extends RuntimeException> providedValueProvider) {
		Provider<U, RuntimeException> providerMock = mock(Provider.class);
		when(providerMock.get()).thenReturn(providedValueProvider.get());
		try {
			orElseProvideFunction.apply(nothing(), providerMock);
		} catch (RuntimeException e) {}
		verify(providerMock).get();
		verifyNoMoreInteractions(providerMock);
	}

	private <U> void orElseProvideFunctionOnNothingWithThrowingProviderPropagatesThrownException(
			BinaryFunction<? super M, ? super Provider<? extends U, ? extends RuntimeException>, ? extends T, ? extends RuntimeException> orElseProvideFunction) {
		RuntimeException exception = new RuntimeException();
		Provider<U, RuntimeException> providerMock = mock(Provider.class);
		when(providerMock.get()).thenThrow(exception);
		try {
			orElseProvideFunction.apply(nothing(), providerMock);
		} catch (RuntimeException e) {
			assertThat(e, is(sameInstance(exception)));
		}
	}

}
