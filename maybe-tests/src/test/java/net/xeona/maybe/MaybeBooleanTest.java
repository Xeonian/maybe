package net.xeona.maybe;

import static net.xeona.maybe.MaybeBoolean.justBoolean;
import static net.xeona.maybe.RandomNumberUtility.aRandomByte;
import static net.xeona.maybe.matcher.MaybeBooleanMatcher.isJustBoolean;
import static net.xeona.maybe.matcher.MaybeBooleanMatcher.isNoBoolean;
import static net.xeona.maybe.matcher.MaybeByteMatcher.isJustByte;
import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.hamcrest.Matcher;
import org.junit.Test;

import net.xeona.function.BinaryConsumer;
import net.xeona.function.BinaryFunction;
import net.xeona.function.BooleanConsumer;
import net.xeona.function.BooleanProvider;
import net.xeona.function.BooleanToByteFunction;
import net.xeona.function.BooleanToCharFunction;
import net.xeona.function.BooleanUnaryOperator;
import net.xeona.function.Provider;
import net.xeona.function.ToBooleanFunction;
import net.xeona.function.VoidFunction;
import net.xeona.maybe.matcher.MaybeCharMatcher;

@SuppressWarnings("unchecked")
public class MaybeBooleanTest {

	@Test
	public void justBooleanIsPresent() {
		assertTrue(justBoolean(nextBoolean()).isPresent());
	}

	@Test
	public void getOnJustBooleanReturnsValue() {
		extractValueFunctionOnJustBooleanReturnsValue(MaybeBoolean::get);
	}

	@Test
	public void orElseOnJustBooleanReturnsValue() {
		extractValueFunctionOnJustBooleanReturnsValue(maybeBoolean -> maybeBoolean.orElse(nextBoolean()));
	}

	@Test
	public void orElseGetOnJustBooleanReturnsValue() {
		extractValueFunctionOnJustBooleanReturnsValue(
				maybeBoolean -> maybeBoolean.orElseGet(mock(BooleanProvider.class)));
	}

	@Test
	public void orElseGetOnJustBooleanDoesNotInvokeProvider() {
		extractValueFunctionWithAlternativeProviderOnJustBooleanDoesNotInvokeProvider(MaybeBoolean::orElseGet,
				BooleanProvider.class);
	}

	@Test
	public void orElseThrowOnJustBooleanReturnsValue() {
		extractValueFunctionOnJustBooleanReturnsValue(maybeBoolean -> maybeBoolean.orElseThrow(mock(Provider.class)));
	}

	@Test
	public void orElseThrowOnJustBooleanDoesNotInvokeProvider() {
		extractValueFunctionWithAlternativeProviderOnJustBooleanDoesNotInvokeProvider(MaybeBoolean::orElseThrow,
				Provider.class);
	}

	@Test
	public void ifPresentOnJustBooleanInvokesConsumerWithValue() {
		functionWithIfPresentConsumerOnJustBooleanInvokesConsumerWithValue(MaybeBoolean::ifPresent);
	}

	@Test
	public void ifPresentOnJustBooleanWithThrowingConsumerPropagatesThrownException() {
		functionWithIfPresentConsumerOnJustBooleanWithThrowingConsumerPropagatesThrownException(
				MaybeBoolean::ifPresent);
	}

	@Test(expected = NullPointerException.class)
	public void ifPresentOnJustBooleanWithNullConsumerThrowsNullPointerException() {
		justBoolean(nextBoolean()).ifPresent(null);
	}

	@Test
	public void ifAbsentOnJustBooleanDoesNotInvokeFunction() {
		functionWithIfAbsentFunctionOnJustBooleanDoesNotInvokeIfAbsentFunction(MaybeBoolean::ifAbsent);
	}

	@Test
	public void byPresenceOnJustBooleanInvokesIfPresentConsumer() {
		functionWithIfPresentConsumerOnJustBooleanInvokesConsumerWithValue(
				(maybeBoolean, booleanConsumer) -> maybeBoolean.byPresence(booleanConsumer, mock(VoidFunction.class)));
	}

	@Test
	public void byPresenceOnJustBooleanDoesNotInvokeIfAbsentFunction() {
		functionWithIfAbsentFunctionOnJustBooleanDoesNotInvokeIfAbsentFunction(
				(maybeBoolean, voidFunction) -> maybeBoolean.byPresence(mock(BooleanConsumer.class), voidFunction));
	}

	@Test
	public void byPresenceOnJustBooleanWithThrowingConsumerPropagatesThrownException() {
		functionWithIfPresentConsumerOnJustBooleanWithThrowingConsumerPropagatesThrownException(
				(maybeBoolean, booleanConsumer) -> maybeBoolean.byPresence(booleanConsumer,
						(VoidFunction<RuntimeException>) mock(VoidFunction.class)));
	}

	@Test(expected = NullPointerException.class)
	public void byPresenceOnJustBooleanWithNullIfPresentConsumerThrowsNullPointerException() {
		justBoolean(nextBoolean()).byPresence(null, mock(VoidFunction.class));
	}

	@Test
	public void filterOnJustBooleanInvokesPredicate() {
		MaybeBooleanTest
				.<BooleanUnaryOperator<RuntimeException>> functionWithDelegateFunctionOnJustBooleanInvokesDelegateFunctionWithValue(
						MaybeBoolean::filter, BooleanUnaryOperator.class, BooleanUnaryOperator::apply);
	}

	@Test
	public void filterOnJustBooleanWithPredicateReturningTrueReturnsJustBooleanOfValue() {
		MaybeBooleanTest
				.<BooleanUnaryOperator<RuntimeException>, Boolean, MaybeBoolean> functionWithDelegateFunctionOnJustBooleanReturnsValue(
						MaybeBoolean::filter, BooleanUnaryOperator.class, BooleanUnaryOperator::apply, true,
						(value, filtered) -> isJustBoolean(value));
	}

	@Test
	public void filterOnJustBooleanWithPredicateReturningFalseReturnsNoBoolean() {
		MaybeBooleanTest
				.<BooleanUnaryOperator<RuntimeException>, Boolean, MaybeBoolean> functionWithDelegateFunctionOnJustBooleanReturnsValue(
						MaybeBoolean::filter, BooleanUnaryOperator.class, BooleanUnaryOperator::apply, false,
						(value, filtered) -> isNoBoolean());
	}

	@Test
	public void filterOnJustBooleanWithThrowingPredicatePropagatesThrownException() {
		MaybeBooleanTest
				.<BooleanUnaryOperator<RuntimeException>> functionWithThrowingDelegateFunctionPropagatesThrownException(
						MaybeBoolean::filter, BooleanUnaryOperator.class, BooleanUnaryOperator::apply);
	}

	@Test(expected = NullPointerException.class)
	public void filterOnJustBooleanWithNullPredicateThrowsNullPointerException() {
		justBoolean(nextBoolean()).filter(null);
	}

	@Test
	public void mapToBooleanOnJustBooleanInvokesMapFunction() {
		MaybeBooleanTest
				.<BooleanUnaryOperator<RuntimeException>> functionWithDelegateFunctionOnJustBooleanInvokesDelegateFunctionWithValue(
						MaybeBoolean::mapToBoolean, BooleanUnaryOperator.class, BooleanUnaryOperator::apply);
	}

	@Test
	public void mapToBooleanOnJustBooleanReturnsJustBooleanOfMapFunctionReturnValue() {
		MaybeBooleanTest
				.<BooleanUnaryOperator<RuntimeException>, Boolean, MaybeBoolean> functionWithDelegateFunctionOnJustBooleanReturnsValue(
						MaybeBoolean::mapToBoolean, BooleanUnaryOperator.class, BooleanUnaryOperator::apply,
						nextBoolean(), (value, mappedValue) -> isJustBoolean(mappedValue));
	}

	@Test
	public void mapToBooleanOnJustBooleanWithThrowingMapFunctionPropagatesThrownException() {
		MaybeBooleanTest
				.<BooleanUnaryOperator<RuntimeException>> functionWithThrowingDelegateFunctionPropagatesThrownException(
						MaybeBoolean::mapToBoolean, BooleanUnaryOperator.class, BooleanUnaryOperator::apply);
	}

	@Test(expected = NullPointerException.class)
	public void mapToBooleanOnJustBooleanWithNullMapFunctionThrowsNullPointerException() {
		justBoolean(nextBoolean()).mapToBoolean(null);
	}

	@Test
	public void mapToCharOnJustBooleanInvokesMapFunction() {
		MaybeBooleanTest
				.<BooleanToCharFunction<RuntimeException>> functionWithDelegateFunctionOnJustBooleanInvokesDelegateFunctionWithValue(
						MaybeBoolean::mapToChar, BooleanToCharFunction.class, BooleanToCharFunction::apply);
	}

	@Test
	public void mapToCharOnJustBooleanReturnsJustCharOfMapFunctionReturnValue() {
		MaybeBooleanTest
				.<BooleanToCharFunction<RuntimeException>, Character, MaybeChar> functionWithDelegateFunctionOnJustBooleanReturnsValue(
						MaybeBoolean::mapToChar, BooleanToCharFunction.class, BooleanToCharFunction::apply, nextChar(),
						(value, mappedValue) -> MaybeCharMatcher.isJustChar(mappedValue));
	}

	@Test
	public void mapToCharOnJustBooleanWithThrowingMapFunctionPropagatesThrownException() {
		MaybeBooleanTest
				.<BooleanToCharFunction<RuntimeException>> functionWithThrowingDelegateFunctionPropagatesThrownException(
						MaybeBoolean::mapToChar, BooleanToCharFunction.class, BooleanToCharFunction::apply);
	}

	@Test(expected = NullPointerException.class)
	public void mapToCharOnJustBooleanWithNullMapFunctionThrowsNullPointerException() {
		justBoolean(nextBoolean()).mapToChar(null);
	}

	@Test
	public void mapToByteOnJustBooleanInvokesMapFunction() {
		MaybeBooleanTest
				.<BooleanToByteFunction<RuntimeException>> functionWithDelegateFunctionOnJustBooleanInvokesDelegateFunctionWithValue(
						MaybeBoolean::mapToByte, BooleanToByteFunction.class, BooleanToByteFunction::apply);
	}

	@Test
	public void mapToByteOnJustBooleanReturnsJustByteOfMapFunctionReturnValue() {
		MaybeBooleanTest
				.<BooleanToByteFunction<RuntimeException>, Byte, MaybeByte> functionWithDelegateFunctionOnJustBooleanReturnsValue(
						MaybeBoolean::mapToByte, BooleanToByteFunction.class, BooleanToByteFunction::apply,
						aRandomByte(), (value, mappedValue) -> isJustByte(mappedValue));
	}

	@Test
	public void mapToByteOnJustBooleanWithThrowingMapFunctionPropagatesThrownException() {
		MaybeBooleanTest
				.<BooleanToByteFunction<RuntimeException>> functionWithThrowingDelegateFunctionPropagatesThrownException(
						MaybeBoolean::mapToByte, BooleanToByteFunction.class, BooleanToByteFunction::apply);
	}

	@Test(expected = NullPointerException.class)
	public void mapToByteOnJustBooleanWithNullMapFunctionThrowsNullPointerException() {
		justBoolean(nextBoolean()).mapToByte(null);
	}

	private static void extractValueFunctionOnJustBooleanReturnsValue(
			ToBooleanFunction<? super MaybeBoolean, ? extends RuntimeException> extractFunction) {
		boolean value = nextBoolean();
		assertEquals(extractFunction.apply(justBoolean(value)), value);
	}

	private static <P> void extractValueFunctionWithAlternativeProviderOnJustBooleanDoesNotInvokeProvider(
			BinaryFunction<? super MaybeBoolean, ? super P, ?, RuntimeException> extractFunction,
			Class<? super P> providerClass) {
		P providerMock = (P) mock(providerClass);
		extractFunction.apply(justBoolean(nextBoolean()), providerMock);
		verifyNoMoreInteractions(providerMock);
	}

	private static void functionWithIfPresentConsumerOnJustBooleanInvokesConsumerWithValue(
			BinaryConsumer<? super MaybeBoolean, ? super BooleanConsumer<RuntimeException>, ? extends RuntimeException> functionInvocation) {
		boolean value = nextBoolean();
		BooleanConsumer<RuntimeException> consumerMock = mock(BooleanConsumer.class);
		functionInvocation.consume(justBoolean(value), consumerMock);
		verify(consumerMock).consume(value);
		verifyNoMoreInteractions(consumerMock);
	}

	private static void functionWithIfPresentConsumerOnJustBooleanWithThrowingConsumerPropagatesThrownException(
			BinaryConsumer<? super MaybeBoolean, ? super BooleanConsumer<RuntimeException>, ? extends RuntimeException> functionInvocation) {
		boolean value = nextBoolean();
		BooleanConsumer<RuntimeException> consumerMock = mock(BooleanConsumer.class);
		RuntimeException exception = new RuntimeException();
		doThrow(exception).when(consumerMock).consume(value);
		try {
			functionInvocation.consume(justBoolean(value), consumerMock);
			fail();
		} catch (RuntimeException e) {
			assertThat(e, is(sameInstance(exception)));
		}
	}

	private static void functionWithIfAbsentFunctionOnJustBooleanDoesNotInvokeIfAbsentFunction(
			BinaryConsumer<? super MaybeBoolean, ? super VoidFunction<RuntimeException>, ? extends RuntimeException> functionInvocation) {
		VoidFunction<RuntimeException> functionMock = mock(VoidFunction.class);
		functionInvocation.consume(justBoolean(nextBoolean()), functionMock);
		verifyNoMoreInteractions(functionMock);
	}

	private static <F> void functionWithDelegateFunctionOnJustBooleanInvokesDelegateFunctionWithValue(
			BinaryConsumer<? super MaybeBoolean, ? super F, ? extends RuntimeException> functionInvocation,
			Class<? super F> delegateFunctionClass,
			BinaryConsumer<? super F, ? super Boolean, ? extends RuntimeException> delegateFunctionInvocation) {
		boolean value = nextBoolean();
		F delegateFunctionMock = (F) mock(delegateFunctionClass);
		functionInvocation.consume(justBoolean(value), delegateFunctionMock);
		delegateFunctionInvocation.consume(verify(delegateFunctionMock), value);
		verifyNoMoreInteractions(delegateFunctionMock);
	}

	private static <F, T, U> void functionWithDelegateFunctionOnJustBooleanReturnsValue(
			BinaryFunction<? super MaybeBoolean, ? super F, ? extends U, ? extends RuntimeException> functionInvocation,
			Class<? super F> delegateFunctionClass,
			BinaryFunction<? super F, ? super Boolean, T, ? extends RuntimeException> delegateFunctionInvocation,
			T delegateFunctionReturnValue,
			BinaryFunction<? super Boolean, ? super T, ? extends Matcher<? super U>, ? extends RuntimeException> expectedReturnValueMatcherFunction) {
		boolean value = nextBoolean();
		F delegateFunctionMock = (F) mock(delegateFunctionClass);
		when(delegateFunctionInvocation.apply(delegateFunctionMock, value)).thenReturn(delegateFunctionReturnValue);
		assertThat(functionInvocation.apply(justBoolean(value), delegateFunctionMock),
				expectedReturnValueMatcherFunction.apply(value, delegateFunctionReturnValue));
	}

	private static <F> void functionWithThrowingDelegateFunctionPropagatesThrownException(
			BinaryConsumer<? super MaybeBoolean, ? super F, ? extends RuntimeException> functionInvocation,
			Class<? super F> delegateFunctionClass,
			BinaryConsumer<? super F, ? super Boolean, ? extends RuntimeException> delegateFunctionInvocation) {
		boolean value = nextBoolean();
		F delegateFunctionMock = (F) mock(delegateFunctionClass);
		RuntimeException exception = new RuntimeException();
		delegateFunctionInvocation.consume(doThrow(exception).when(delegateFunctionMock), value);
		try {
			functionInvocation.consume(justBoolean(value), delegateFunctionMock);
			fail();
		} catch (RuntimeException e) {
			assertThat(e, is(sameInstance(exception)));
		}
	}

	private static char nextChar() {
		return (char) nextInt(Character.MIN_VALUE, Character.MAX_VALUE);
	}

}
