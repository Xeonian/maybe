package net.xeona.maybe;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static net.xeona.maybe.MaybeInt.justInt;
import static net.xeona.maybe.MaybeInt.noInt;
import static net.xeona.maybe.matcher.MaybeBooleanMatcher.isNoBoolean;
import static net.xeona.maybe.matcher.MaybeCharMatcher.isNoChar;
import static net.xeona.maybe.matcher.MaybeIntMatcher.isJustInt;
import static net.xeona.maybe.matcher.MaybeIntMatcher.isNoInt;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.hamcrest.Matcher;
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

	@Test(expected = UnsupportedOperationException.class)
	public void addOnJustIntThrowsUnsupportedOperationExceptionAndDoesNotMutateTarget() {
		int value = 0;
		MaybeInt maybeInt = justInt(value);
		try {
			maybeInt.add(1);
			fail();
		} finally {
			assertThat(maybeInt, isJustInt(value));
		}
	}

	@Test
	public void addAllOfEmptyCollectionOnJustIntDoesNotMutateTarget() {
		int value = 0;
		MaybeInt maybeInt = justInt(value);
		assertFalse(maybeInt.addAll(emptySet()));
		assertThat(maybeInt, isJustInt(value));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void addAllOfPopulatedCollectionOnJustIntThrowsUnsupportedOperationExceptionAndDoesNotMutateTarget() {
		int value = 0;
		MaybeInt maybeInt = justInt(value);
		try {
			maybeInt.addAll(singleton(1));
			fail();
		} finally {
			assertThat(maybeInt, isJustInt(value));
		}
	}

	@Test(expected = UnsupportedOperationException.class)
	public void removeValueOnJustIntThrowsUnsupportedOperationExceptionAndDoesNotMutateTarget() {
		int value = 0;
		MaybeInt maybeInt = justInt(value);
		try {
			maybeInt.remove(value);
			fail();
		} finally {
			assertThat(maybeInt, isJustInt(value));
		}
	}

	@Test
	public void removeOtherValueOnJustIntDoesNotMutateTarget() {
		int value = 0;
		MaybeInt maybeInt = justInt(value);
		int otherValue = 42;
		assertThat(value, is(not(equalTo(otherValue))));
		assertFalse(maybeInt.remove(otherValue));
		assertThat(maybeInt, isJustInt(value));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void removeAllOfCollectionContainingValueOnJustIntThrowsUnsupportedOperationExceptionAndDoesNotMutateTarget() {
		int value = 0;
		MaybeInt maybeInt = justInt(value);
		try {
			maybeInt.removeAll(singleton(value));
			fail();
		} finally {
			assertThat(maybeInt, isJustInt(value));
		}
	}

	@Test
	public void removeAllOfCollectionNotContainingValueOnJustIntDoesNotMutateTarget() {
		int value = 0;
		MaybeInt maybeInt = justInt(value);
		Object otherValue = new Object();
		assertThat(value, is(not(equalTo(otherValue))));
		assertFalse(maybeInt.removeAll(singleton(otherValue)));
		assertThat(maybeInt, isJustInt(value));
	}

	@Test
	public void retainAllOfCollectionContainingValueOnJustIntDoesNotMutateTarget() {
		int value = 0;
		MaybeInt maybeInt = justInt(value);
		assertFalse(maybeInt.retainAll(singleton(value)));
		assertThat(maybeInt, isJustInt(value));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void retainAllOfCollectionNotContainingValueOnJustIntThrowsUnsupportedOperationExceptionAndDoesNotMutateTarget() {
		int value = 0;
		MaybeInt maybeInt = justInt(value);
		try {
			maybeInt.retainAll(emptySet());
			fail();
		} finally {
			assertThat(maybeInt, isJustInt(value));
		}
	}

	@Test(expected = UnsupportedOperationException.class)
	public void clearOnJustIntThrowsUnsupportedOperationExceptionAndDoesNotMutateTarget() {
		int value = 0;
		MaybeInt maybeInt = justInt(value);
		try {
			maybeInt.clear();
			fail();
		} finally {
			assertThat(maybeInt, isJustInt(value));
		}
	}

	@Test
	public void justIntToArrayReturnsSingleElementArrayContainingValue() {
		int value = 0;
		assertThat(justInt(value).toArray(), is(arrayContaining(value)));
	}

	@Test
	public void justIntIteratorIteratesOverValue() {
		int value = 0;
		Iterator<Integer> iterator = justInt(value).iterator();

		assertTrue(iterator.hasNext());
		assertThat(iterator.next(), is(value));
		assertFalse(iterator.hasNext());
		try {
			iterator.next();
			fail();
		} catch (NoSuchElementException e) {}
	}

	@Test
	public void hashCodeOfJustIntIsHashCodeOfValue() {
		int value = 42;
		assertThat(justInt(42).hashCode(), is(Integer.hashCode(value)));
	}

	@Test
	public void justIntEqualsJustIntOfSameValue() {
		int value = 42;
		assertThat(justInt(value), is(justInt(value)));
	}

	@Test
	public void justIntDoesNotEqualJustIntOfDifferentValue() {
		assertThat(justInt(42), not(justInt(343)));
	}

	@Test
	public void justIntDoesNotEqualArbitraryObject() {
		assertThat(justInt(42), not(new Object()));
	}

	@Test
	public void justIntToStringDescribesValue() {
		int value = 42;
		assertThat(justInt(value).toString(), is("JustInt [" + value + "]"));
	}

	@Test
	public void justIntIsSerializable() {
		int value = RandomUtils.nextInt();
		assertThat(SerializationUtils.roundtrip(justInt(value)), isJustInt(value));
	}

	@Test
	public void noIntIsNotPresent() {
		assertFalse(noInt().isPresent());
	}

	@Test(expected = NoSuchElementException.class)
	public void getOnNoIntThrowsNoSuchElementException() {
		noInt().get();
	}

	@Test
	public void orElseOnNoIntReturnsAlternative() {
		int alternative = 42;
		assertThat(noInt().orElse(alternative), is(alternative));
	}

	@Test
	public void orElseGetOnNoIntInvokesProvider() {
		IntProvider<RuntimeException> providerMock = mock(IntProvider.class);
		noInt().orElseGet(providerMock);
		verify(providerMock).get();
		verifyNoMoreInteractions(providerMock);
	}

	@Test
	public void orElseGetOnNoIntReturnsProvidedValue() {
		IntProvider<RuntimeException> providerMock = mock(IntProvider.class);
		int providedValue = 42;
		when(providerMock.get()).thenReturn(providedValue);
		assertThat(noInt().orElseGet(providerMock), is(providedValue));
	}

	@Test
	public void orElseGetOnNoIntWithThrowingProviderPropagatesThrownException() {
		IntProvider<RuntimeException> providerMock = mock(IntProvider.class);
		RuntimeException runtimeException = new RuntimeException();
		when(providerMock.get()).thenThrow(runtimeException);
		try {
			noInt().orElseGet(providerMock);
			fail();
		} catch (RuntimeException e) {
			assertThat(e, is(sameInstance(runtimeException)));
		}
	}

	@Test(expected = NullPointerException.class)
	public void orElseGetOnNoIntWithNullProviderThrowsNullPointerException() {
		noInt().orElseGet(null);
	}

	@Test
	public void orElseThrowOnNoIntThrowsProvidedException() {
		Provider<RuntimeException, RuntimeException> providerMock = mock(Provider.class);
		RuntimeException exception = new RuntimeException();
		when(providerMock.get()).thenReturn(exception);
		try {
			noInt().orElseThrow(providerMock);
			fail();
		} catch (RuntimeException e) {
			assertThat(e, is(sameInstance(exception)));
		}
	}

	@Test
	public void orElseThrowOnNoIntWithThrowingProviderPropagatesThrownException() {
		Provider<RuntimeException, RuntimeException> providerMock = mock(Provider.class);
		RuntimeException exception = new RuntimeException();
		when(providerMock.get()).thenThrow(exception);
		try {
			noInt().orElseThrow(providerMock);
			fail();
		} catch (RuntimeException e) {
			assertThat(e, is(sameInstance(exception)));
		}
	}

	@Test(expected = NullPointerException.class)
	public void orElseThrowOnNoIntWithNullProviderThrowsNullPointerException() {
		noInt().orElseThrow(null);
	}

	@Test
	public void ifPresentOnNoIntDoesNotInvokeConsumer() {
		IntConsumer<RuntimeException> consumerMock = mock(IntConsumer.class);
		noInt().ifPresent(consumerMock);
		verifyNoMoreInteractions(consumerMock);
	}

	@Test
	public void ifAbsentOnNoIntInvokesFunction() {
		VoidFunction<RuntimeException> functionMock = mock(VoidFunction.class);
		noInt().ifAbsent(functionMock);
		verify(functionMock).apply();
		verifyNoMoreInteractions(functionMock);
	}

	@Test
	public void ifAbsentOnNoIntWithThrowingFunctionPropagatesThrownException() {
		VoidFunction<RuntimeException> functionMock = mock(VoidFunction.class);
		RuntimeException exception = new RuntimeException();
		doThrow(exception).when(functionMock).apply();
		try {
			noInt().ifAbsent(functionMock);
			fail();
		} catch (RuntimeException e) {
			assertThat(e, is(sameInstance(exception)));
		}
	}

	@Test(expected = NullPointerException.class)
	public void ifAbsentOnNoIntWIthNullFunctionThrowsNullPointerException() {
		noInt().ifAbsent(null);
	}

	@Test
	public void byPresenceOnNoIntInvokesIfAbsentAndDoesNotInvokeIfPresent() {
		IntConsumer<RuntimeException> consumerMock = mock(IntConsumer.class);
		VoidFunction<RuntimeException> functionMock = mock(VoidFunction.class);
		noInt().byPresence(consumerMock, functionMock);
		verify(functionMock).apply();
		verifyNoMoreInteractions(consumerMock, functionMock);
	}

	@Test
	public void byPresenceOnNoIntWithThrowingIfAbsentFunctionPropagatesThrownException() {
		VoidFunction<RuntimeException> functionMock = mock(VoidFunction.class);
		RuntimeException exception = new RuntimeException();
		doThrow(exception).when(functionMock).apply();
		try {
			noInt().byPresence(mock(IntConsumer.class), functionMock);
			fail();
		} catch (RuntimeException e) {
			assertThat(e, is(sameInstance(exception)));
		}
	}

	@Test(expected = NullPointerException.class)
	public void byPresenceOnNoIntWithNullIfAbsentFunctionThrowsNullPointerException() {
		noInt().byPresence(mock(IntConsumer.class), null);
	}

	@Test
	public void filterOnNoIntDoesNotInvokePredicate() {
		functionInvocationOnNoIntDoesNotInvokeDelegateFunction(MaybeInt::filter, IntToBooleanFunction.class);
	}

	@Test
	public void filterOnNoIntIsNoInt() {
		functionInvocationOnNoIntReturnsValue(MaybeInt::filter, IntToBooleanFunction.class, isNoInt());
	}

	@Test
	public void mapToBooleanOnNoIntDoesNotInvokeMapFunction() {
		functionInvocationOnNoIntDoesNotInvokeDelegateFunction(MaybeInt::mapToBoolean, IntToBooleanFunction.class);
	}

	@Test
	public void mapToBooleanOnNoIntIsNoBoolean() {
		functionInvocationOnNoIntReturnsValue(MaybeInt::mapToBoolean, IntToBooleanFunction.class, isNoBoolean());
	}

	@Test
	public void mapToCharOnNoIntDoesNotInvokeMapFunction() {
		functionInvocationOnNoIntDoesNotInvokeDelegateFunction(MaybeInt::mapToChar, IntToCharFunction.class);
	}

	@Test
	public void mapToCharOnNoIntIsNoChar() {
		functionInvocationOnNoIntReturnsValue(MaybeInt::mapToChar, IntToCharFunction.class, isNoChar());
	}

	@Test
	public void mapToShortOnNoIntDoesNotInvokeMapFunction() {
		functionInvocationOnNoIntDoesNotInvokeDelegateFunction(MaybeInt::mapToShort, IntToShortFunction.class);
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

	private static <F> void functionInvocationOnNoIntDoesNotInvokeDelegateFunction(
			BinaryFunction<? super MaybeInt, ? super F, ?, ? extends RuntimeException> maybeFunctionApplication,
			Class<? super F> delegateFunctionClass) {
		F functionMock = (F) mock(delegateFunctionClass);
		maybeFunctionApplication.apply(noInt(), functionMock);
		verifyNoMoreInteractions(functionMock);
	}

	public static <F, M> void functionInvocationOnNoIntReturnsValue(
			BinaryFunction<? super MaybeInt, ? super F, ? extends M, ? extends RuntimeException> maybeFunctionApplication,
			Class<? super F> delegateFunctionClass, Matcher<? super M> valueMatcher) {
		assertThat(maybeFunctionApplication.apply(noInt(), (F) mock(delegateFunctionClass)), valueMatcher);
	}

}
