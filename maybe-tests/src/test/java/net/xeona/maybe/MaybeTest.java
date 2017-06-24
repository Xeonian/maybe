package net.xeona.maybe;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static net.xeona.maybe.Maybe.fromOptional;
import static net.xeona.maybe.Maybe.just;
import static net.xeona.maybe.Maybe.maybe;
import static net.xeona.maybe.Maybe.nothing;
import static net.xeona.maybe.Maybe.reduce;
import static net.xeona.maybe.Maybe.toOptional;
import static net.xeona.maybe.matcher.MaybeMatcher.isJust;
import static net.xeona.maybe.matcher.MaybeMatcher.isNothing;
import static org.apache.commons.lang3.SerializationUtils.roundtrip;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.text.RandomStringGenerator;
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

@SuppressWarnings("unchecked")
public class MaybeTest {

	@Test
	public void maybeValueIsJustValue() {
		Object value = new Object();
		assertThat(maybe(value), isJust(value));
	}

	@Test
	public void maybeNullIsNothing() {
		assertThat(maybe(null), isNothing());
	}

	@Test(expected = NullPointerException.class)
	public void justNullThrowsNullPointerException() {
		just(null);
	}

	@Test
	public void justIsPresent() {
		assertTrue(just(new Object()).isPresent());
	}

	@Test
	public void getOnJustReturnsValue() {
		Object value = new Object();
		assertThat(just(value).get(), is(value));
	}

	@Test
	public void orElseOnJustReturnsValue() {
		Object value = new Object();
		assertThat(just(value).orElse(new Object()), is(value));
	}

	@Test
	public void orElseGetOnJustReturnsValueAndDoesNotInvokeProvider() {
		Object value = new Object();
		Provider<Object, RuntimeException> providerMock = mock(Provider.class);

		assertThat(just(value).orElseGet(providerMock), is(value));

		verifyNoMoreInteractions(providerMock);
	}

	@Test
	public void orElseThrowOnJustReturnsValueAndDoesNotInvokeProvider() {
		Object value = new Object();
		Provider<RuntimeException, RuntimeException> providerMock = mock(Provider.class);

		assertThat(just(value).orElseThrow(providerMock), is(value));
		verifyNoMoreInteractions(providerMock);
	}

	@Test
	public void ifPresentOnJustInvokesConsumerWithValue() {
		Object value = new Object();
		Consumer<Object, RuntimeException> consumerMock = mock(Consumer.class);

		just(value).ifPresent(consumerMock);

		verify(consumerMock).consume(value);
		verifyNoMoreInteractions(consumerMock);
	}

	@Test
	public void ifPresentOnJustWithThrowingConsumerPropagatesThrownException() {
		consumerInvocationOnJustWithThrowingConsumerPropagatesThrownException(Maybe::ifPresent);
	}

	@Test(expected = NullPointerException.class)
	public void ifPresentOnJustWithNullConsumerThrowsNullPointerException() {
		just(new Object()).ifPresent(null);
	}

	@Test
	public void ifAbsentOnJustDoesNotInvokeFunction() {
		VoidFunction<RuntimeException> functionMock = mock(VoidFunction.class);

		just(new Object()).ifAbsent(functionMock);

		verifyNoMoreInteractions(functionMock);
	}

	@Test
	public void byPresenceOnJustInvokesIfPresentConsumerWithValueAndDoesNotInvokeIfAbsentFunction() {
		Object value = new Object();
		Consumer<Object, RuntimeException> consumerMock = mock(Consumer.class);
		VoidFunction<RuntimeException> functionMock = mock(VoidFunction.class);

		just(value).byPresence(consumerMock, functionMock);

		verify(consumerMock).consume(value);
		verifyNoMoreInteractions(consumerMock, functionMock);
	}

	@Test
	public void byPresenceOnJustWithThrowingIfPresentConsumerPropagatesThrownException() {
		consumerInvocationOnJustWithThrowingConsumerPropagatesThrownException(
				(maybe, consumer) -> maybe.byPresence(consumer, mock(VoidFunction.class)));
	}

	@Test(expected = NullPointerException.class)
	public void byPresenceOnJustWithNullIfPresentConsumerThrowsNullPointerException() {
		just(new Object()).byPresence(null, mock(VoidFunction.class));
	}

	@Test
	public void filterOnJustWithAcceptingPredicateInvokesPredicateWithValueAndReturnsJustValue() {
		MaybeTest
				.<ToBooleanFunction<Object, RuntimeException>, Boolean, Maybe<Object>> functionInvocationOnJustInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
						Maybe::filter, ToBooleanFunction.class, ToBooleanFunction::apply, true,
						(value, functionReturnValue) -> functionReturnValue ? Maybe.just(value) : Maybe.nothing());
	}

	@Test
	public void filterOnJustWithRejectingPredicateInvokesPredicateWithValueAndReturnsNothing() {
		MaybeTest
				.<ToBooleanFunction<Object, RuntimeException>, Boolean, Maybe<Object>> functionInvocationOnJustInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
						Maybe::filter, ToBooleanFunction.class, ToBooleanFunction::apply, false,
						(value, functionReturnValue) -> functionReturnValue ? Maybe.just(value) : Maybe.nothing());
	}

	@Test
	public void filterOnJustWithThrowingPredicatePropagatesException() {
		MaybeTest
				.<ToBooleanFunction<Object, RuntimeException>> functionInvocationOnJustWithThrowingFunctionPropagatesThrownException(
						Maybe::filter, ToBooleanFunction.class, ToBooleanFunction::apply);
	}

	@Test(expected = NullPointerException.class)
	public void filterOnJustWithNullPredicateThrowsNullPointerException() {
		just(new Object()).filter(null);
	}

	@Test
	public void mapToBooleanOnJustInvokesMapFunctionAndReturnsJustBooleanOfFunctionReturnValue() {
		MaybeTest
				.<ToBooleanFunction<Object, RuntimeException>, Boolean, MaybeBoolean> functionInvocationOnJustInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
						Maybe::mapToBoolean, ToBooleanFunction.class, ToBooleanFunction::apply, true,
						(value, functionReturnValue) -> MaybeBoolean.justBoolean(functionReturnValue));
	}

	@Test
	public void mapToBooleanOnJustWithThrowingMapFunctionPropagatesThrownException() {
		MaybeTest
				.<ToBooleanFunction<Object, RuntimeException>> functionInvocationOnJustWithThrowingFunctionPropagatesThrownException(
						Maybe::mapToBoolean, ToBooleanFunction.class, ToBooleanFunction::apply);
	}

	@Test(expected = NullPointerException.class)
	public void mapToBooleanOnJustWithNullMapFunctionThrowsNullPointerException() {
		just(new Object()).mapToBoolean(null);
	}

	@Test
	public void mapToCharOnJustInvokesMapFunctionAndReturnsJustCharOfFunctionReturnValue() {
		MaybeTest
				.<ToCharFunction<Object, RuntimeException>, Character, MaybeChar> functionInvocationOnJustInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
						Maybe::mapToChar, ToCharFunction.class, ToCharFunction::apply, 'a',
						(value, functionReturnValue) -> MaybeChar.justChar(functionReturnValue));
	}

	@Test
	public void mapToCharOnJustWithThrowingMapFunctionPropagatesThrownException() {
		MaybeTest
				.<ToCharFunction<Object, RuntimeException>> functionInvocationOnJustWithThrowingFunctionPropagatesThrownException(
						Maybe::mapToChar, ToCharFunction.class, ToCharFunction::apply);
	}

	@Test
	public void mapToByteOnJustInvokesMapFunctionAndReturnsJustByteOfFunctionReturnValue() {
		MaybeTest
				.<ToByteFunction<Object, RuntimeException>, Byte, MaybeByte> functionInvocationOnJustInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
						Maybe::mapToByte, ToByteFunction.class, ToByteFunction::apply, (byte) 42,
						(value, functionReturnValue) -> MaybeByte.justByte(functionReturnValue));
	}

	@Test
	public void mapToByteOnJustWithThrowingFunctionPropagatesThrownException() {
		MaybeTest
				.<ToByteFunction<Object, RuntimeException>> functionInvocationOnJustWithThrowingFunctionPropagatesThrownException(
						Maybe::mapToByte, ToByteFunction.class, ToByteFunction::apply);
	}

	@Test
	public void mapToShortOnJustInvokesMapFunctionAndReturnsJustShortOfFunctionReturnValue() {
		MaybeTest
				.<ToShortFunction<Object, RuntimeException>, Short, MaybeShort> functionInvocationOnJustInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
						Maybe::mapToShort, ToShortFunction.class, ToShortFunction::apply, (short) 42,
						(value, functionReturnValue) -> MaybeShort.justShort(functionReturnValue));
	}

	@Test
	public void mapToShortOnJustWithThrowingFunctionPropagatesThrownException() {
		MaybeTest
				.<ToShortFunction<Object, RuntimeException>> functionInvocationOnJustWithThrowingFunctionPropagatesThrownException(
						Maybe::mapToShort, ToShortFunction.class, ToShortFunction::apply);
	}

	@Test
	public void mapToIntOnJustInvokesMapFunctionAndReturnsJustIntOfFunctionReturnValue() {
		MaybeTest
				.<ToIntFunction<Object, RuntimeException>, Integer, MaybeInt> functionInvocationOnJustInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
						Maybe::mapToInt, ToIntFunction.class, ToIntFunction::apply, 42,
						(value, functionReturnValue) -> MaybeInt.justInt(functionReturnValue));
	}

	@Test
	public void mapToIntOnJustWithThrowingFunctionPropagatesThrownException() {
		MaybeTest
				.<ToIntFunction<Object, RuntimeException>> functionInvocationOnJustWithThrowingFunctionPropagatesThrownException(
						Maybe::mapToInt, ToIntFunction.class, ToIntFunction::apply);
	}

	@Test
	public void mapToLongOnJustInvokesMapFunctionAndReturnsJustLongOfFunctionReturnValue() {
		MaybeTest
				.<ToLongFunction<Object, RuntimeException>, Long, MaybeLong> functionInvocationOnJustInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
						Maybe::mapToLong, ToLongFunction.class, ToLongFunction::apply, 42L,
						(value, functionReturnValue) -> MaybeLong.justLong(functionReturnValue));
	}

	@Test
	public void mapToLongOnJustWithThrowingFunctionPropagatesThrownException() {
		MaybeTest
				.<ToLongFunction<Object, RuntimeException>> functionInvocationOnJustWithThrowingFunctionPropagatesThrownException(
						Maybe::mapToLong, ToLongFunction.class, ToLongFunction::apply);
	}

	@Test
	public void mapToFloatOnJustInvokesMapFunctionAndReturnsJustFloatOfFunctionReturnValue() {
		MaybeTest
				.<ToFloatFunction<Object, RuntimeException>, Float, MaybeFloat> functionInvocationOnJustInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
						Maybe::mapToFloat, ToFloatFunction.class, ToFloatFunction::apply, 42F,
						(value, functionReturnValue) -> MaybeFloat.justFloat(functionReturnValue));
	}

	@Test
	public void mapToFloatOnJustWithThrowingFunctionPropagatesThrownException() {
		MaybeTest
				.<ToFloatFunction<Object, RuntimeException>> functionInvocationOnJustWithThrowingFunctionPropagatesThrownException(
						Maybe::mapToFloat, ToFloatFunction.class, (functionMock, value) -> functionMock.apply(value));
	}

	@Test
	public void mapToDoubleOnJustInvokesMapFunctionAndReturnsJustDoubleOfFunctionReturnValue() {
		MaybeTest
				.<ToDoubleFunction<Object, RuntimeException>, Double, MaybeDouble> functionInvocationOnJustInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
						Maybe::mapToDouble, ToDoubleFunction.class, ToDoubleFunction::apply, 42.0,
						(value, functionReturnValue) -> MaybeDouble.justDouble(functionReturnValue));
	}

	@Test
	public void mapToDoubleOnJustWithThrowingFunctionPropagatesThrownException() {
		MaybeTest
				.<ToDoubleFunction<Object, RuntimeException>> functionInvocationOnJustWithThrowingFunctionPropagatesThrownException(
						Maybe::mapToDouble, ToDoubleFunction.class, (functionMock, value) -> functionMock.apply(value));
	}

	@Test
	public void mapOnJustInvokesMapFunctionAndReturnsJustOfFunctionReturnValue() {
		MaybeTest
				.<Function<Object, Object, RuntimeException>, Object, Maybe<Object>> functionInvocationOnJustInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
						Maybe::map, Function.class, Function::apply, new Object(),
						(value, functionReturnValue) -> Maybe.just(functionReturnValue));
	}

	@Test
	public void mapOnJustWithThrowingFunctionPropagatesThrownException() {
		MaybeTest
				.<Function<Object, Object, RuntimeException>> functionInvocationOnJustWithThrowingFunctionPropagatesThrownException(
						Maybe::map, Function.class, (function, value) -> function.apply(value));
	}

	@Test
	public void justIsNotEmpty() {
		assertThat(just(new Object()), not(empty()));
	}

	@Test
	public void justHasSize1() {
		assertThat(just(new Object()), hasSize(1));
	}

	@Test
	public void justContainsOwnValue() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		assertTrue(maybe.contains(value));
	}

	@Test
	public void justDoesNotContainOtherValue() {
		Maybe<Object> maybe = just(new Object());
		assertFalse(maybe.contains(new Object()));
	}

	@Test
	public void justContainsAllOfEmptyCollection() {
		assertTrue(just(new Object()).containsAll(emptySet()));
	}

	@Test
	public void justContainsAllOfCollectionContainingOnlyOwnValue() {
		Object value = new Object();
		assertTrue(just(value).containsAll(asList(value, value)));
	}

	@Test
	public void justDoesNotContainAllOfCollectionContainingOtherValue() {
		Object value = new Object();
		assertFalse(just(value).containsAll(asList(value, new Object())));
	}

	@Test(expected = NullPointerException.class)
	public void justContainsAllOfNullCollectionThrowsNullPointerException() {
		just(new Object()).containsAll(null);
	}

	@Test
	public void addOnJustThrowsUnsupportedOperationExceptionAndDoesNotMutateTarget() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);
		try {
			maybe.add(value);
			fail();
		} catch (UnsupportedOperationException e) {
			assertThat(maybe, is(just(value)));
		}
	}

	@Test
	public void addAllOfEmptyCollectionOnJustDoesNotMutateTarget() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		assertFalse(maybe.addAll(emptySet()));
		assertThat(maybe, is(just(value)));
	}

	@Test
	public void addAllOfPopulatedCollectionOnJustThrowsUnsupportedOperationExceptionAndDoesNotMutateTarget() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);
		try {
			maybe.addAll(singleton(value));
			fail();
		} catch (UnsupportedOperationException e) {
			assertThat(maybe, is(just(value)));
		}
	}

	@Test
	public void removeOnJustWithValueOfJustThrowsUnsupportedOperationExceptionAndDoesNotMutateTarget() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		try {
			maybe.remove(value);
			fail();
		} catch (UnsupportedOperationException e) {
			assertThat(maybe, is(just(value)));
		}
	}

	@Test
	public void removeOnJustWithOtherValueDoesNotMutateTarget() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		assertThat(maybe.remove(new Object()), is(false));
		assertThat(maybe, is(just(value)));
	}

	@Test
	public void removeAllOnJustWithCollectionNotContainingValueDoesNotMutateTarget() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		assertFalse(maybe.removeAll(asList(new Object(), new Object())));
		assertThat(maybe, is(just(value)));
	}

	@Test
	public void removeAllOnJustWithCollectionContainingValueThrowsUnsupportedOperationExceptionAndDoesNotMutateTarget() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		try {
			maybe.removeAll(singleton(value));
			fail();
		} catch (UnsupportedOperationException e) {
			assertThat(maybe, is(just(value)));
		}
	}

	@Test
	public void retainAllOnJustWithCollectionContainingValueDoesNotMutateTarget() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		assertFalse(maybe.retainAll(asList(value, new Object())));
		assertThat(maybe, is(just(value)));
	}

	@Test
	public void retainAllOnJustWithCollectionNotContainingValueThrowsUnsupportedOperationExceptionAndDoesNotMutateTarget() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		try {
			maybe.retainAll(emptySet());
			fail();
		} catch (UnsupportedOperationException e) {
			assertThat(maybe, is(just(value)));
		}
	}

	@Test
	public void clearOnJustThrowsUnsupportedOperationExceptionAndDoesNotMutateTarget() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		try {
			maybe.clear();
			fail();
		} catch (UnsupportedOperationException e) {
			assertThat(maybe, is(just(value)));
		}
	}

	@Test
	public void justToArrayReturnsSingleElementArrayContainingValue() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		assertThat(maybe.toArray(), is(arrayContaining(value)));
	}

	@Test
	public void justToArrayWith0LengthArrayReturnsSingleElementArrayContainingValue() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		assertThat(maybe.toArray(new Object[0]), is(arrayContaining(value)));
	}

	@Test
	public void justToArrayWith1LengthArrayReturnsSingleElementArrayContainingValue() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		assertThat(maybe.toArray(new Object[] { new Object() }), is(arrayContaining(value)));
	}

	@Test
	public void justToArrayWithArrayOfLength2ReturnsArrayContainingValueAndNull() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		assertThat(maybe.toArray(new Object[] { new Object(), new Object() }), is(arrayContaining(value, null)));
	}

	@Test
	public void justToArrayWithArrayOfLengthGreaterThan2ReturnsArrayContainingValueThenNullThenRemainingArrayElements() {
		// TODO
	}

	@Test(expected = NullPointerException.class)
	public void justToArrayWithNullArrayThrowsNullPointerException() {
		just(new Object()).toArray(null);
	}

	@Test(expected = NoSuchElementException.class)
	public void justIteratorIteratesOverSingleElement() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		Iterator<Object> iterator = maybe.iterator();

		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is(value));
		assertThat(iterator.hasNext(), is(false));
		iterator.next();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void justIteratorDoesNotSupportRemoval() {
		Maybe<Object> maybe = just(new Object());
		Iterator<Object> iterator = maybe.iterator();
		iterator.remove();
	}

	@Test
	public void justHashCodeIsValueHashCode() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		assertThat(maybe.hashCode(), is(value.hashCode()));
	}

	@Test
	public void justEqualsJustOfSameValue() {
		Object value = new Object();
		assertThat(just(value), is(just(value)));
	}

	@Test
	public void justDoesNotEqualJustOfDifferentValue() {
		assertThat(just(new Object()), not(just(new Object())));
	}

	@Test
	public void justDoesNotEqualArbitraryObject() {
		assertThat(just(new Object()), not(new Object()));
	}

	@Test
	public void justToStringDescribesValue() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		assertThat(maybe.toString(), is("Just [" + value + "]"));
	}

	@Test
	public void justOfSerializableValueIsSerializable() {
		Object value = new RandomStringGenerator.Builder().withinRange('a', 'z').build()
				.generate(RandomUtils.nextInt(5, 20));
		assertThat(value, is(instanceOf(Serializable.class)));
		assertThat(roundtrip(just(value)), isJust(value));
	}

	@Test(expected = SerializationException.class)
	public void serializeJustOfNonSerializableValueThrowsSerializationException() {
		Object value = new Object();
		assertThat(value, is(not(instanceOf(Serializable.class))));
		roundtrip(just(value));
	}

	@Test
	public void nothingIsNotPresent() {
		assertFalse(nothing().isPresent());
	}

	@Test(expected = NoSuchElementException.class)
	public void getOnNothingThrowsNoSuchElementException() {
		nothing().get();
	}

	@Test
	public void orElseOnNothingReturnsElse() {
		Maybe<Object> maybe = nothing();

		Object value = new Object();

		assertThat(maybe.orElse(value), is(value));
	}

	@Test
	public void orElseGetOnNothingInvokesProviderAndReturnsProvidedValue() {
		Object value = new Object();
		Provider<Object, RuntimeException> providerMock = mock(Provider.class);
		when(providerMock.get()).thenReturn(value);

		assertThat(nothing().orElseGet(providerMock), is(value));

		verify(providerMock).get();
		verifyNoMoreInteractions(providerMock);
	}

	@Test
	public void orElseGetOnNothingWithThrowingProviderPropagatesThrownExcetpion() {
		RuntimeException runtimeException = new RuntimeException();

		Provider<Object, RuntimeException> providerMock = mock(Provider.class);
		when(providerMock.get()).thenThrow(runtimeException);

		try {
			nothing().orElseGet(providerMock);
			fail();
		} catch (RuntimeException e) {
			assertThat(e, is(sameInstance(runtimeException)));
		}
	}

	@Test(expected = NullPointerException.class)
	public void orElseGetOnNothingWithNullProviderThrowsNullPointerException() {
		nothing().orElseGet(null);
	}

	@Test
	public void orElseThrowOnNothingThrowsProvidedException() {
		RuntimeException exception = new RuntimeException();

		Provider<RuntimeException, RuntimeException> providerMock = mock(Provider.class);
		when(providerMock.get()).thenReturn(exception);

		try {
			nothing().orElseThrow(providerMock);
			fail();
		} catch (RuntimeException e) {
			assertThat(e, is(sameInstance(exception)));
		}
	}

	@Test
	public void orElseThrowOnNothingWithThrowingProviderPropagatesThrownException() {
		RuntimeException exception = new RuntimeException();

		Provider<RuntimeException, RuntimeException> providerMock = mock(Provider.class);
		when(providerMock.get()).thenThrow(exception);

		try {
			nothing().orElseThrow(providerMock);
			fail();
		} catch (RuntimeException e) {
			assertThat(e, is(sameInstance(exception)));
		}
	}

	@Test(expected = NullPointerException.class)
	public void orElseThrowOnNothingWithNullProviderThrowsNullPointerException() {
		nothing().orElseThrow(null);
	}

	@Test
	public void ifPresentOnNothingDoesNotInvokeConsumer() {
		Consumer<Object, RuntimeException> consumerMock = mock(Consumer.class);

		nothing().ifPresent(consumerMock);

		verifyNoMoreInteractions(consumerMock);
	}

	@Test
	public void ifAbsentOnNothingInvokesFunction() {
		VoidFunction<RuntimeException> functionMock = mock(VoidFunction.class);

		nothing().ifAbsent(functionMock);

		verify(functionMock).apply();
		verifyNoMoreInteractions(functionMock);
	}

	@Test
	public void ifAbsentOnNothingWithThrowingFunctionPropagatesException() {
		RuntimeException exception = new RuntimeException();

		VoidFunction<RuntimeException> functionMock = mock(VoidFunction.class);
		doThrow(exception).when(functionMock).apply();

		try {
			nothing().ifAbsent(functionMock);
			fail();
		} catch (RuntimeException e) {
			assertThat(e, is(sameInstance(exception)));
		}
	}

	@Test(expected = NullPointerException.class)
	public void ifAbsentOnNothingWithNullFunctionThrowsNullPointerException() {
		nothing().ifAbsent(null);
	}

	@Test
	public void byPresenceOnNothingInvokesIfAbsentAndDoesNotInvokeIfPresent() {
		Consumer<Object, RuntimeException> consumerMock = mock(Consumer.class);
		VoidFunction<RuntimeException> functionMock = mock(VoidFunction.class);

		nothing().byPresence(consumerMock, functionMock);

		verify(functionMock).apply();
		verifyNoMoreInteractions(consumerMock, functionMock);
	}

	@Test
	public void byPresenceOnNothingWithThrowingIfAbsentPropagatesThrownException() {
		RuntimeException exception = new RuntimeException();

		VoidFunction<RuntimeException> functionMock = mock(VoidFunction.class);
		doThrow(exception).when(functionMock).apply();

		try {
			nothing().byPresence(mock(Consumer.class), functionMock);
			fail();
		} catch (RuntimeException e) {
			assertThat(e, is(sameInstance(exception)));
		}
	}

	@Test(expected = NullPointerException.class)
	public void byPresenceOnNothingWithNullIfAbsentFunctionThrowsNullPointerException() {
		nothing().byPresence(mock(Consumer.class), null);
	}

	@Test
	public void filterOnNothingReturnsNothingAndDoesNotInvokePredicate() {
		ToBooleanFunction<Object, RuntimeException> predicateMock = mock(ToBooleanFunction.class);

		assertThat(nothing().filter(predicateMock), is(nothing()));

		verifyNoMoreInteractions(predicateMock);
	}

	@Test
	public void mapToBooleanOnNothingReturnsNothingAndDoesNotInvokeMapFunction() {
		MaybeTest
				.<ToBooleanFunction<Object, RuntimeException>, MaybeBoolean> mapOnNothingReturnsNothingAndDoesNotInvokeFunction(
						Maybe::mapToBoolean, ToBooleanFunction.class, MaybeBoolean.noBoolean());
	}

	@Test
	public void mapToCharOnNothingReturnsNothingAndDoesNotInvokeMapFunction() {
		MaybeTest
				.<ToCharFunction<Object, RuntimeException>, MaybeChar> mapOnNothingReturnsNothingAndDoesNotInvokeFunction(
						Maybe::mapToChar, ToCharFunction.class, MaybeChar.nothing());
	}

	@Test
	public void mapToByteOnNothingReturnsNothingAndDoesNotInvokeMapFunction() {
		MaybeTest
				.<ToByteFunction<Object, RuntimeException>, MaybeByte> mapOnNothingReturnsNothingAndDoesNotInvokeFunction(
						Maybe::mapToByte, ToByteFunction.class, MaybeByte.noByte());
	}

	@Test
	public void mapToShortOnNothingReturnsNothingAndDoesNotInvokeMapFunction() {
		MaybeTest
				.<ToShortFunction<Object, RuntimeException>, MaybeShort> mapOnNothingReturnsNothingAndDoesNotInvokeFunction(
						Maybe::mapToShort, ToShortFunction.class, MaybeShort.noShort());
	}

	@Test
	public void mapToIntOnNothingReturnsNothingAndDoesNotInvokeMapFunction() {
		MaybeTest
				.<ToIntFunction<Object, RuntimeException>, MaybeInt> mapOnNothingReturnsNothingAndDoesNotInvokeFunction(
						Maybe::mapToInt, ToIntFunction.class, MaybeInt.noInt());
	}

	@Test
	public void mapToLongOnNothingReturnsNothingAndDoesNotInvokeMapFunction() {
		MaybeTest
				.<ToLongFunction<Object, RuntimeException>, MaybeLong> mapOnNothingReturnsNothingAndDoesNotInvokeFunction(
						Maybe::mapToLong, ToLongFunction.class, MaybeLong.noLong());
	}

	@Test
	public void mapToFloatOnNothingReturnsNothingAndDoesNotInvokeMapFunction() {
		MaybeTest
				.<ToFloatFunction<Object, RuntimeException>, MaybeFloat> mapOnNothingReturnsNothingAndDoesNotInvokeFunction(
						Maybe::mapToFloat, ToFloatFunction.class, MaybeFloat.nothing());
	}

	@Test
	public void mapToDoubleOnNothingReturnsNothingAndDoesNotInvokeMapFunction() {
		MaybeTest
				.<ToDoubleFunction<Object, RuntimeException>, MaybeDouble> mapOnNothingReturnsNothingAndDoesNotInvokeFunction(
						Maybe::mapToDouble, ToDoubleFunction.class, MaybeDouble.nothing());
	}

	@Test
	public void mapOnNothingReturnsNothingAndDoesNotInvokeMapFunction() {
		MaybeTest
				.<Function<Object, Object, RuntimeException>, Maybe<Object>> mapOnNothingReturnsNothingAndDoesNotInvokeFunction(
						Maybe::map, Function.class, nothing());
	}

	@Test
	public void nothingIsEmpty() {
		assertThat(nothing(), is(empty()));
	}

	@Test
	public void nothingHasSize0() {
		assertThat(nothing(), hasSize(0));
	}

	@Test
	public void nothingDoesNotContainValue() {
		assertFalse(nothing().contains(new Object()));
	}

	@Test
	public void nothingContainsAllOfEmptyCollection() {
		assertTrue(nothing().containsAll(emptySet()));
	}

	@Test
	public void nothingDoesNotContainAllOfPopulatedCollection() {
		assertFalse(nothing().containsAll(singleton(new Object())));
	}

	@Test(expected = NullPointerException.class)
	public void nothingContainsAllOfNullCollectionThrowsNullPointerException() {
		nothing().containsAll(null);
	}

	@Test
	public void addOnNothingThrowsUnsupportedOperationExceptionAndDoesNotMutateTarget() {
		Maybe<Object> maybe = nothing();

		try {
			maybe.add(new Object());
			fail();
		} catch (UnsupportedOperationException e) {
			assertThat(maybe, is(nothing()));
		}
	}

	@Test
	public void nothingAddAllOfEmptyCollectionDoesNotMutateTarget() {
		Maybe<Object> maybe = nothing();

		assertFalse(maybe.addAll(emptySet()));

		assertThat(maybe, is(nothing()));
	}

	@Test
	public void nothingAddAllOfPopulatedCollectionThrowsUnsupportedOperationExceptionAndDoesNotMutateTarget() {
		Maybe<Object> maybe = nothing();

		try {
			maybe.addAll(singleton(new Object()));
			fail();
		} catch (UnsupportedOperationException e) {
			assertThat(maybe, is(nothing()));
		}
	}

	@Test
	public void removeOnNothingDoesNotMutateTarget() {
		Maybe<Object> maybe = nothing();

		assertFalse(maybe.remove(new Object()));

		assertThat(maybe, is(nothing()));
	}

	@Test
	public void removeAllOnNothingDoesNotMutateTarget() {
		Maybe<Object> maybe = nothing();

		assertFalse(maybe.removeAll(singleton(new Object())));

		assertThat(maybe, is(nothing()));
	}

	@Test
	public void retainAllOnNothingDoesNotMutateTarget() {
		Maybe<Object> maybe = nothing();

		assertFalse(maybe.retainAll(singleton(new Object())));

		assertThat(maybe, is(nothing()));
	}

	@Test
	public void clearOnNothingDoesNotMutateTarget() {
		Maybe<Object> maybe = nothing();

		maybe.clear();

		assertThat(maybe, is(nothing()));
	}

	@Test
	public void nothingToArrayReturnsArrayOfLength0() {
		assertThat(nothing().toArray(), arrayWithSize(0));
	}

	@Test
	public void nothingToArrayWithArrayOfLength0ReturnsArrayOfLength0() {
		assertThat(nothing().toArray(new Object[0]), arrayWithSize(0));
	}

	@Test
	public void nothingToArrayWithPopulatedArraySetsFirstIndexToNull() {
		Object value0 = new Object();
		Object value1 = new Object();
		Object[] array = new Object[] { value0, value1 };

		assertThat(nothing().toArray(array), arrayContaining(null, value1));
	}

	@Test(expected = NullPointerException.class)
	public void nothingToArrayWithNullArrayThrowsNullPointerException() {
		nothing().toArray(null);
	}

	@Test(expected = NoSuchElementException.class)
	public void nothingIteratorHasNoElements() {
		Iterator<Object> iterator = nothing().iterator();

		assertFalse(iterator.hasNext());
		iterator.next();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void nothingIteratorDoesNotSupportRemove() {
		nothing().iterator().remove();
	}

	@Test
	public void nothingHashCodeIs0() {
		assertThat(nothing().hashCode(), is(0));
	}

	@Test
	public void nothingEqualToNothing() {
		assertThat(nothing(), is(nothing()));
	}

	@Test
	public void nothingNotEqualToJust() {
		assertThat(nothing(), is(not(just(new Object()))));
	}

	@Test
	public void nothingDescribesSelfOnString() {
		assertThat(nothing().toString(), is("Nothing []"));
	}

	@Test
	public void nothingIsSerializable() {
		assertThat(roundtrip(nothing()), isNothing());
	}

	@Test
	public void maybeFromPopulatedOptionalIsJustValue() {
		Object value = new Object();
		assertThat(fromOptional(Optional.of(value)), is(just(value)));
	}

	@Test
	public void maybeFromEmptyOptionalIsNothing() {
		assertThat(fromOptional(Optional.empty()), is(nothing()));
	}

	@Test
	public void reduceUnboxesNestedJust() {
		Object value = new Object();
		assertThat(reduce(just(just(value))), is(just(value)));
	}

	@Test
	public void reduceUnboxesNestedNothing() {
		assertThat(reduce(just(nothing())), is(nothing()));
	}

	@Test
	public void justToOptionalIsOptionalOfValue() {
		Object value = new Object();
		assertThat(toOptional(just(value)), is(Optional.of(value)));
	}

	@Test
	public void nothingToOptionalIsEmptyOptional() {
		assertThat(toOptional(nothing()), is(Optional.empty()));
	}

	private static void consumerInvocationOnJustWithThrowingConsumerPropagatesThrownException(
			BinaryConsumer<Maybe<Object>, Consumer<Object, Exception>, Exception> maybeInvocation) {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		Exception exception = new Exception();

		Consumer<Object, Exception> consumerMock = (Consumer<Object, Exception>) mock(Consumer.class);
		try {
			doThrow(exception).when(consumerMock).consume(value);
		} catch (Exception e) {
			fail();
		}

		try {
			maybeInvocation.consume(maybe, consumerMock);
			fail();
		} catch (Exception e) {
			assertThat(e, is(sameInstance(exception)));
		}
	}

	private static <F, T, M> void functionInvocationOnJustInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
			BinaryFunction<Maybe<Object>, F, M, RuntimeException> maybeFunctionApplication, Class<?> functionClass,
			BinaryFunction<F, Object, T, RuntimeException> functionApplication, T functionReturnValue,
			BinaryFunction<Object, T, M, RuntimeException> targetReturnValueFunction) {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		F functionMock = (F) mock(functionClass);
		when(functionApplication.apply(functionMock, value)).thenReturn(functionReturnValue);

		assertThat(maybeFunctionApplication.apply(maybe, functionMock),
				is(targetReturnValueFunction.apply(value, functionReturnValue)));

		functionApplication.apply(verify(functionMock), value);
		verifyNoMoreInteractions(functionMock);
	}

	private static <F> void functionInvocationOnJustWithThrowingFunctionPropagatesThrownException(
			BinaryConsumer<? super Maybe<Object>, ? super F, ? extends RuntimeException> maybeInvocation,
			Class<?> functionClass,
			BinaryFunction<? super F, Object, ?, ? extends RuntimeException> functionInvocation) {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		Exception exception = new Exception();

		F functionMock = (F) mock(functionClass);
		when(functionInvocation.apply(functionMock, value)).thenThrow(exception);

		try {
			maybeInvocation.consume(maybe, functionMock);
			fail();
		} catch (Exception e) {
			assertThat(e, is(sameInstance(exception)));
		}
	}

	private static <F, M> void mapOnNothingReturnsNothingAndDoesNotInvokeFunction(
			BinaryFunction<? super Maybe<Object>, ? super F, ? extends M, ? extends RuntimeException> mapFunctionApplication,
			Class<?> functionClass, M nothingValue) {
		F functionMock = (F) mock(functionClass);

		assertThat(mapFunctionApplication.apply(nothing(), functionMock), is(nothingValue));

		verifyNoMoreInteractions(functionMock);
	}

}
