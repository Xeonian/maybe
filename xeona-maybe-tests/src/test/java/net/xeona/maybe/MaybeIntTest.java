package net.xeona.maybe;

import static java.util.Arrays.asList;
import static net.xeona.maybe.MaybeInt.justInt;
import static net.xeona.maybe.MaybeInt.noInt;
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

import org.junit.Test;

import net.xeona.function.BinaryConsumer;
import net.xeona.function.BinaryFunction;
import net.xeona.function.IntConsumer;
import net.xeona.function.IntFunction;
import net.xeona.function.IntProvider;
import net.xeona.function.IntToBooleanFunction;
import net.xeona.function.IntToByteFunction;
import net.xeona.function.IntToCharFunction;
import net.xeona.function.IntToDoubleFunction;
import net.xeona.function.IntToFloatFunction;
import net.xeona.function.IntToLongFunction;
import net.xeona.function.IntToShortFunction;
import net.xeona.function.IntUnaryOperator;
import net.xeona.function.Provider;
import net.xeona.function.VoidFunction;

@SuppressWarnings("unchecked")
public class MaybeIntTest {

	@Test
	public void justIntIsPresent() {
		assertTrue(justInt(0).isPresent());
	}

	@Test
	public void getOnJustIntReturnsValue() {
		int value = 0;
		assertEquals(justInt(value).get(), value);
	}

	@Test
	public void orElseOnJustReturnsValue() {
		int value = 0;
		assertEquals(justInt(value).orElse(1), value);
	}

	@Test
	public void orElseGetOnJustReturnsValueAndDoesNotInvokeProvider() {
		int value = 0;
		IntProvider<RuntimeException> providerMock = mock(IntProvider.class);

		assertEquals(justInt(value).orElseGet(providerMock), value);
		verifyNoMoreInteractions(providerMock);
	}

	@Test
	public void orElseThrowOnJustReturnsValueAndDoesNotInvokeProvider() {
		int value = 0;
		Provider<RuntimeException, RuntimeException> providerMock = mock(Provider.class);

		assertEquals(justInt(value).orElseThrow(providerMock), value);
		verifyNoMoreInteractions(providerMock);
	}

	@Test
	public void ifPresentOnJustInvokesConumserWithValue() {
		int value = 0;
		IntConsumer<RuntimeException> consumerMock = mock(IntConsumer.class);

		justInt(value).ifPresent(consumerMock);

		verify(consumerMock).consume(value);
		verifyNoMoreInteractions(consumerMock);
	}

	@Test
	public void ifPresentOnJustIntWithThrowingConsumerPropagatesThrownException() {
		consumerInvocationOnJustIntWithThrowingConsumerPropagatesThrownException(MaybeInt::ifPresent);
	}

	@Test(expected = NullPointerException.class)
	public void ifPresentOnJustIntWithNullConsumerThrowsNullPointerException() {
		justInt(0).ifPresent(null);
	}

	@Test
	public void ifAbsentOnJustIntDoesNotInvokeFunction() {
		VoidFunction<RuntimeException> functionMock = mock(VoidFunction.class);
		justInt(0).ifAbsent(functionMock);
		verifyNoMoreInteractions(functionMock);
	}

	@Test
	public void byPresenceOnJustIntInvokesIfPresentConsumer() {
		int value = 0;
		IntConsumer<RuntimeException> ifPresentMock = mock(IntConsumer.class);
		VoidFunction<RuntimeException> ifAbsentMock = mock(VoidFunction.class);

		justInt(value).byPresence(ifPresentMock, ifAbsentMock);

		verify(ifPresentMock).consume(value);
		verifyNoMoreInteractions(ifPresentMock, ifAbsentMock);
	}

	@Test
	public void byPresenceOnJustIntWithThrowingIfPresentConsumerPropagatesThrownException() {
		consumerInvocationOnJustIntWithThrowingConsumerPropagatesThrownException(
				(maybeInt, consumer) -> maybeInt.byPresence(consumer, mock(VoidFunction.class)));
	}

	@Test(expected = NullPointerException.class)
	public void byPresenceOnJustIntWithNullIfPresentConsumerThrowsNullPointerException() {
		justInt(0).byPresence(null, mock(VoidFunction.class));
	}

	@Test
	public void filterOnJustIntWithAcceptingPredicateInvokesPredicateWithValueAndReturnsJustValue() {
		MaybeIntTest
				.<IntToBooleanFunction<RuntimeException>, Boolean, MaybeInt> functionInvocationOnJustIntInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
						MaybeInt::filter, IntToBooleanFunction.class, IntToBooleanFunction::apply, true,
						(value, accept) -> justInt(value));
	}

	@Test
	public void filterOnJustIntWithRejectingPredicateInvokesPredicateWithValueAndReturnsNoInt() {
		MaybeIntTest
				.<IntToBooleanFunction<RuntimeException>, Boolean, MaybeInt> functionInvocationOnJustIntInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
						MaybeInt::filter, IntToBooleanFunction.class, IntToBooleanFunction::apply, false,
						(value, accept) -> noInt());
	}

	@Test
	public void filterOnJustIntWithThrowingPredicatePropagatesThrownException() {
		MaybeIntTest
				.<IntToBooleanFunction<RuntimeException>> functionInvocationOnJustIntWithThrowingFunctionPropagatesThrownException(
						MaybeInt::filter, IntToBooleanFunction.class, IntToBooleanFunction::apply);
	}

	@Test(expected = NullPointerException.class)
	public void filterOnJustIntWithNullPredicateThrowsNullPointerException() {
		justInt(0).filter(null);
	}

	@Test
	public void mapToBooleanOnJustIntInvokesMapFunctionAndReturnsJustBooleanOfFunctionReturnValue() {
		MaybeIntTest
				.<IntToBooleanFunction<RuntimeException>, Boolean, MaybeBoolean> functionInvocationOnJustIntInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
						MaybeInt::mapToBoolean, IntToBooleanFunction.class, IntToBooleanFunction::apply, true,
						(value, functionReturnValue) -> MaybeBoolean.just(functionReturnValue));
	}

	@Test
	public void mapToBooleanOnJustIntWithThrowingMapFunctionPropagatesThrownException() {
		MaybeIntTest
				.<IntToBooleanFunction<RuntimeException>> functionInvocationOnJustIntWithThrowingFunctionPropagatesThrownException(
						MaybeInt::mapToBoolean, IntToBooleanFunction.class, IntToBooleanFunction::apply);
	}

	@Test(expected = NullPointerException.class)
	public void mapToBooleanOnJustIntWithNullMapFunctionThrowsNullPointerException() {
		justInt(0).mapToBoolean(null);
	}

	@Test
	public void mapToCharOnJustIntInvokesMapFunctionAndReturnsJustCharOfFunctionReturnValue() {
		MaybeIntTest
				.<IntToCharFunction<RuntimeException>, Character, MaybeChar> functionInvocationOnJustIntInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
						MaybeInt::mapToChar, IntToCharFunction.class, IntToCharFunction::apply, 'a',
						(value, functionReturnValue) -> MaybeChar.just(functionReturnValue));
	}

	@Test
	public void mapToCharOnJustIntWithThrowingMapFunctionPropagatesThrownException() {
		MaybeIntTest
				.<IntToCharFunction<RuntimeException>> functionInvocationOnJustIntWithThrowingFunctionPropagatesThrownException(
						MaybeInt::mapToChar, IntToCharFunction.class, IntToCharFunction::apply);
	}

	@Test(expected = NullPointerException.class)
	public void mapToCharOnJustIntWithNullMapFunctionThrowsNullPointerException() {
		justInt(0).mapToChar(null);
	}

	@Test
	public void mapToByteOnJustIntInvokesMapFunctionAndReturnsJustByteOfFunctionReturnValue() {
		MaybeIntTest
				.<IntToByteFunction<RuntimeException>, Byte, MaybeByte> functionInvocationOnJustIntInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
						MaybeInt::mapToByte, IntToByteFunction.class, IntToByteFunction::apply, (byte) 42,
						(value, functionReturnValue) -> MaybeByte.just(functionReturnValue));
	}

	@Test
	public void mapToByteOnJustIntWithThrowingMapFunctionPropagatesThrownException() {
		MaybeIntTest
				.<IntToByteFunction<RuntimeException>> functionInvocationOnJustIntWithThrowingFunctionPropagatesThrownException(
						MaybeInt::mapToByte, IntToByteFunction.class, IntToByteFunction::apply);
	}

	@Test(expected = NullPointerException.class)
	public void mapToByteOnJustIntWithNullMapFunctionThrowsNullPointerException() {
		justInt(0).mapToByte(null);
	}

	@Test
	public void mapToShortOnJustIntInvokesMapFunctionAndReturnsJustShortOfFunctionReturnValue() {
		MaybeIntTest
				.<IntToShortFunction<RuntimeException>, Short, MaybeShort> functionInvocationOnJustIntInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
						MaybeInt::mapToShort, IntToShortFunction.class, IntToShortFunction::apply, (short) 42,
						(value, functionReturnValue) -> MaybeShort.just(functionReturnValue));
	}

	@Test
	public void mapToShortOnJustIntWithThrowingMapFunctionPropagatesThrownException() {
		MaybeIntTest
				.<IntToShortFunction<RuntimeException>> functionInvocationOnJustIntWithThrowingFunctionPropagatesThrownException(
						MaybeInt::mapToShort, IntToShortFunction.class, IntToShortFunction::apply);
	}

	@Test(expected = NullPointerException.class)
	public void mapToShortOnJustIntWithNullMapFunctionThrowsNullPointerException() {
		justInt(0).mapToShort(null);
	}

	@Test
	public void mapToIntOnJustIntInvokesMapFunctionAndReturnsJustIntOfFunctionReturnValue() {
		MaybeIntTest
				.<IntUnaryOperator<RuntimeException>, Integer, MaybeInt> functionInvocationOnJustIntInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
						MaybeInt::mapToInt, IntUnaryOperator.class, IntUnaryOperator::apply, 42,
						(value, functionReturnValue) -> justInt(functionReturnValue));
	}

	@Test
	public void mapToIntOnJustIntWithThrowingMapFunctionPropagatesThrownException() {
		MaybeIntTest
				.<IntUnaryOperator<RuntimeException>> functionInvocationOnJustIntWithThrowingFunctionPropagatesThrownException(
						MaybeInt::mapToInt, IntUnaryOperator.class, IntUnaryOperator::apply);
	}

	@Test(expected = NullPointerException.class)
	public void mapToIntOnJustIntWithNullMapFunctionThrowsNullPointerException() {
		justInt(0).mapToInt(null);
	}

	@Test
	public void mapToLongOnJustIntInvokesMapFunctionAndReturnsJustLongOfFunctionReturnValue() {
		MaybeIntTest
				.<IntToLongFunction<RuntimeException>, Long, MaybeLong> functionInvocationOnJustIntInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
						MaybeInt::mapToLong, IntToLongFunction.class, IntToLongFunction::apply, 42L,
						(value, functionReturnValue) -> MaybeLong.just(functionReturnValue));
	}

	@Test
	public void mapToLongOnJustIntWithThrowingMapFunctionPropagatesThrownException() {
		MaybeIntTest
				.<IntToLongFunction<RuntimeException>> functionInvocationOnJustIntWithThrowingFunctionPropagatesThrownException(
						MaybeInt::mapToLong, IntToLongFunction.class, IntToLongFunction::apply);
	}

	@Test(expected = NullPointerException.class)
	public void mapToLongOnJustIntWithNullMapFunctionThrowsNullPointerException() {
		justInt(0).mapToLong(null);
	}

	@Test
	public void mapToFloatOnJustIntInvokesMapFunctionAndReturnsJustFloatOfFunctionReturnValue() {
		MaybeIntTest
				.<IntToFloatFunction<RuntimeException>, Float, MaybeFloat> functionInvocationOnJustIntInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
						MaybeInt::mapToFloat, IntToFloatFunction.class, IntToFloatFunction::apply, 42.0F,
						(value, functionReturnValue) -> MaybeFloat.just(functionReturnValue));
	}

	@Test
	public void mapToFloatOnJustIntWithThrowingMapFunctionPropagatesThrownException() {
		MaybeIntTest
				.<IntToFloatFunction<RuntimeException>> functionInvocationOnJustIntWithThrowingFunctionPropagatesThrownException(
						MaybeInt::mapToFloat, IntToFloatFunction.class, IntToFloatFunction::apply);
	}

	@Test(expected = NullPointerException.class)
	public void mapToFloatOnJustIntWithNullMapFunctionThrowsNullPointerException() {
		justInt(0).mapToFloat(null);
	}

	@Test
	public void mapToDoubleOnJustIntInvokesMapFunctionAndReturnsJustDoubleOfFunctionReturnValue() {
		MaybeIntTest
				.<IntToDoubleFunction<RuntimeException>, Double, MaybeDouble> functionInvocationOnJustIntInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
						MaybeInt::mapToDouble, IntToDoubleFunction.class, IntToDoubleFunction::apply, 42.0,
						(value, functionReturnValue) -> MaybeDouble.just(functionReturnValue));
	}

	@Test
	public void mapToDoubleOnJustIntWithThrowingMapFunctionPropagatesThrownException() {
		MaybeIntTest
				.<IntToDoubleFunction<RuntimeException>> functionInvocationOnJustIntWithThrowingFunctionPropagatesThrownException(
						MaybeInt::mapToDouble, IntToDoubleFunction.class, IntToDoubleFunction::apply);
	}

	@Test(expected = NullPointerException.class)
	public void mapToDoubleOnJustIntWithNullMapFunctionThrowsNullPointerException() {
		justInt(0).mapToInt(null);
	}

	@Test
	public void mapOnJustIntInvokesMapFunctionAndReturnsJustOfFunctionReturnValue() {
		MaybeIntTest
				.<IntFunction<Object, RuntimeException>, Object, Maybe<Object>> functionInvocationOnJustIntInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
						MaybeInt::map, IntFunction.class, IntFunction::apply, new Object(),
						(value, functionReturnValue) -> Maybe.just(functionReturnValue));
	}

	@Test
	public void mapOnJustIntWithThrowingMapFunctionPropagatesThrownException() {
		MaybeIntTest
				.<IntFunction<Object, RuntimeException>> functionInvocationOnJustIntWithThrowingFunctionPropagatesThrownException(
						MaybeInt::map, IntFunction.class, IntFunction::apply);
	}

	@Test(expected = NullPointerException.class)
	public void mapOnJustIntWithNullMapFunctionThrowsNullPointerException() {
		justInt(0).map(null);
	}

	@Test
	public void justIntHasSize1() {
		assertThat(justInt(0), hasSize(1));
	}

	@Test
	public void justIntIsNotEmpty() {
		assertThat(justInt(0), not(empty()));
	}

	@Test
	public void justIntContainsOwnValue() {
		int value = 0;
		assertTrue(justInt(value).contains(value));
	}

	@Test
	public void justIntDoesNotContainOtherValue() {
		assertFalse(justInt(0).contains(new Object()));
	}

	@Test
	public void justIntContainsAllOfCollectionContainingOnlyValue() {
		int value = 42;
		assertTrue(justInt(value).containsAll(asList(value, value)));
	}

	@Test
	public void justIntDoesNotContainAllOfCollectionContainingOtherValue() {
		int value = 42;
		assertFalse(justInt(value).containsAll(asList(value, new Object())));
	}

	@Test(expected = NullPointerException.class)
	public void justIntContainsAllOfNullCollectionThrowsNullPointerException() {
		justInt(0).containsAll(null);
	}

	@Test
	public void addOnJustIntThrowsUnsupportedOperationExceptionAndDoesNotMutateTarget() {

	}

	private static void consumerInvocationOnJustIntWithThrowingConsumerPropagatesThrownException(
			BinaryConsumer<MaybeInt, IntConsumer<RuntimeException>, RuntimeException> consumerInvocation) {
		int value = 0;
		RuntimeException exception = new RuntimeException();

		IntConsumer<RuntimeException> consumerMock = mock(IntConsumer.class);
		doThrow(exception).when(consumerMock).consume(value);

		try {
			consumerInvocation.consume(justInt(0), consumerMock);
			fail();
		} catch (RuntimeException e) {
			assertThat(e, is(sameInstance(exception)));
		}
	}

	private static <F, T, M> void functionInvocationOnJustIntInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
			BinaryFunction<MaybeInt, F, M, RuntimeException> maybeApplication, Class<?> functionClass,
			BinaryFunction<F, Integer, T, RuntimeException> functionApplication, T functionReturnValue,
			BinaryFunction<Integer, T, M, RuntimeException> targetReturnValueFunction) {
		int value = 0;
		MaybeInt maybe = justInt(value);

		F functionMock = (F) mock(functionClass);
		when(functionApplication.apply(functionMock, value)).thenReturn(functionReturnValue);

		assertThat(maybeApplication.apply(maybe, functionMock),
				is(targetReturnValueFunction.apply(value, functionReturnValue)));

		functionApplication.apply(verify(functionMock), value);
		verifyNoMoreInteractions(functionMock);
	}

	private static <F> void functionInvocationOnJustIntWithThrowingFunctionPropagatesThrownException(
			BinaryConsumer<MaybeInt, F, RuntimeException> maybeInvocation, Class<?> functionClass,
			BinaryFunction<F, Integer, ?, RuntimeException> functionApplication) {
		int value = 0;
		MaybeInt maybe = justInt(value);

		RuntimeException exception = new RuntimeException();

		F functionMock = (F) mock(functionClass);
		when(functionApplication.apply(functionMock, value)).thenThrow(exception);

		try {
			maybeInvocation.consume(maybe, functionMock);
			fail();
		} catch (RuntimeException e) {
			assertThat(e, is(sameInstance(exception)));
		}
	}

}
