package net.xeona.maybe;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static net.xeona.maybe.Maybe.fromOptional;
import static net.xeona.maybe.Maybe.just;
import static net.xeona.maybe.Maybe.maybe;
import static net.xeona.maybe.Maybe.nothing;
import static net.xeona.maybe.Maybe.toOptional;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
		assertThat(maybe(value), is(just(value)));
	}

	@Test
	public void maybeNullIsNothing() {
		assertThat(maybe(null), is(nothing()));
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
		Maybe<Object> maybe = just(value);

		Object otherValue = new Object();

		assertThat(maybe.orElse(otherValue), is(value));
	}

	@Test
	public void orElseGetOnJustReturnsValueAndDoesNotInvokeProvider() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		Provider<Object, RuntimeException> providerMock = mock(Provider.class);

		assertThat(maybe.orElseGet(providerMock), is(value));

		verifyNoMoreInteractions(providerMock);
	}

	@Test
	public void orElseThrowOnJustReturnsValueAndDoesNotInvokeProvider() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		Provider<RuntimeException, RuntimeException> providerMock = mock(Provider.class);

		assertThat(maybe.orElseThrow(providerMock), is(value));

		verifyNoMoreInteractions(providerMock);
	}

	@Test
	public void ifPresentOnJustInvokesConsumerWithValue() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		Consumer<Object, RuntimeException> consumerMock = mock(Consumer.class);

		maybe.ifPresent(consumerMock);

		verify(consumerMock).consume(value);
		verifyNoMoreInteractions(consumerMock);
	}

	@Test
	public void ifPresentOnJustWithThrowingConsumerPropagatesThrownException() {
		consumerInvocationOnJustWithThrowingConsumerPropagatesThrownException(Maybe::ifPresent);
	}

	@Test
	public void ifAbsentOnJustDoesNotInvokeFunction() {
		Maybe<Object> maybe = just(new Object());

		VoidFunction<RuntimeException> functionMock = mock(VoidFunction.class);

		maybe.ifAbsent(functionMock);

		verifyNoMoreInteractions(functionMock);
	}

	@Test
	public void byPresenceOnJustInvokesIfPresentConsumerWithValueAndDoesNotInvokeIfAbsentFunction() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		Consumer<Object, RuntimeException> consumerMock = mock(Consumer.class);
		VoidFunction<RuntimeException> functionMock = mock(VoidFunction.class);

		maybe.byPresence(consumerMock, functionMock);

		verify(consumerMock).consume(value);
		verifyNoMoreInteractions(consumerMock, functionMock);
	}

	@Test
	public void byPresenceOnJustWithThrowingIfPresentConsumerPropagatesThrownException() {
		consumerInvocationOnJustWithThrowingConsumerPropagatesThrownException(
				(maybe, consumer) -> maybe.byPresence(consumer, mock(VoidFunction.class)));
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

	@Test
	public void mapToBooleanOnJustInvokesMapFunctionAndReturnsJustBooleanOfFunctionReturnValue() {
		MaybeTest
				.<ToBooleanFunction<Object, RuntimeException>, Boolean, MaybeBoolean> functionInvocationOnJustInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
						Maybe::mapToBoolean, ToBooleanFunction.class, ToBooleanFunction::apply, true,
						(value, functionReturnValue) -> MaybeBoolean.just(functionReturnValue));
	}

	@Test
	public void mapToBooleanOnJustWithThrowingMapFunctionPropagatesThrownException() {
		MaybeTest
				.<ToBooleanFunction<Object, RuntimeException>> functionInvocationOnJustWithThrowingFunctionPropagatesThrownException(
						Maybe::mapToBoolean, ToBooleanFunction.class, ToBooleanFunction::apply);
	}

	@Test
	public void mapToCharOnJustInvokesMapFunctionAndReturnsJustCharOfFunctionReturnValue() {
		MaybeTest
				.<ToCharFunction<Object, RuntimeException>, Character, MaybeChar> functionInvocationOnJustInvokesFunctionAndReturnsMaybeOfFunctionReturnValue(
						Maybe::mapToChar, ToCharFunction.class, ToCharFunction::apply, 'a',
						(value, functionReturnValue) -> MaybeChar.just(functionReturnValue));
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
						(value, functionReturnValue) -> MaybeByte.just(functionReturnValue));
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
						(value, functionReturnValue) -> MaybeShort.just(functionReturnValue));
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
						(value, functionReturnValue) -> MaybeInt.just(functionReturnValue));
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
						(value, functionReturnValue) -> MaybeLong.just(functionReturnValue));
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
						(value, functionReturnValue) -> MaybeFloat.just(functionReturnValue));
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
						(value, functionReturnValue) -> MaybeDouble.just(functionReturnValue));
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
		Maybe<Object> maybe = just(value);

		List<Object> collection = Arrays.asList(value, value);

		assertTrue(maybe.containsAll(collection));
	}

	@Test
	public void justDoesNotContainAllOfCollectionContainingOtherValue() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		List<Object> collection = Arrays.asList(value, new Object());

		assertFalse(maybe.containsAll(collection));
	}

	@Test
	public void justToArrayReturnsSingleElementArrayContainingValue() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		assertThat(maybe.toArray(), is(arrayContaining(value)));
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
	public void removeOnJustWIthOtherValueDOesNotMutateTarget() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		assertThat(maybe.remove(new Object()), is(false));
		assertThat(maybe, is(just(value)));
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
	public void nothingIsNotPresent() {
		assertFalse(nothing().isPresent());
	}

	@Test(expected = NoSuchElementException.class)
	public void getOnNothingThrowsNoSuchElementException() {
		nothing().get();
	}

	@Test
	public void ifPresentOnNothingDoesNotInvokeConsumer() {
		Maybe<Object> maybe = nothing();

		Consumer<Object, RuntimeException> consumerMock = mock(Consumer.class);

		maybe.ifPresent(consumerMock);

		verifyNoMoreInteractions(consumerMock);
	}

	@Test
	public void filterOnNothingReturnsNothingAndDoesNotInvokePredicate() {
		Maybe<Object> maybe = nothing();

		ToBooleanFunction<Object, RuntimeException> predicateMock = mock(ToBooleanFunction.class);

		assertThat(maybe.filter(predicateMock), is(nothing()));

		verifyNoMoreInteractions(predicateMock);
	}

	@Test
	public void mapOnNothingReturnsNothingAndDoesNotInvokeMapFunction() {
		Maybe<Object> maybe = nothing();

		Function<Object, Object, RuntimeException> functionMock = mock(Function.class);

		assertThat(maybe.map(functionMock), is(nothing()));

		verifyNoMoreInteractions(functionMock);
	}

	@Test
	public void orElseOnNothingReturnsElse() {
		Maybe<Object> maybe = nothing();

		Object value = new Object();

		assertThat(maybe.orElse(value), is(value));
	}

	@Test
	public void orElseGetOnNothingInvokesProviderAndReturnsProvidedValue() {
		Maybe<Object> maybe = nothing();

		Object value = new Object();
		Provider<Object, RuntimeException> providerMock = mock(Provider.class);
		when(providerMock.get()).thenReturn(value);

		assertThat(maybe.orElseGet(providerMock), is(value));

		verify(providerMock).get();
		verifyNoMoreInteractions(providerMock);
	}

	public void orElseGetWithThrowingProviderPropagatesThrownExcetpion() {
		Maybe<Object> maybe = nothing();

		RuntimeException runtimeException = new RuntimeException();

		Provider<Object, RuntimeException> providerMock = mock(Provider.class);
		when(providerMock.get()).thenThrow(runtimeException);

		try {
			maybe.orElseGet(providerMock);
			fail();
		} catch (RuntimeException e) {
			assertThat(e, is(sameInstance(runtimeException)));
		}
	}

	@Test
	public void nothingIsEmpty() {
		assertThat(nothing(), empty());
	}

	@Test
	public void nothingHasSize0() {
		assertThat(nothing(), hasSize(0));
	}

	@Test
	public void maybeFromPopualtedOptionalIsJustValue() {
		Object value = new Object();
		assertThat(fromOptional(Optional.of(value)), is(just(value)));
	}

	@Test
	public void maybeFromEmptyOptionalIsNothing() {
		assertThat(fromOptional(Optional.empty()), is(nothing()));
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

}
