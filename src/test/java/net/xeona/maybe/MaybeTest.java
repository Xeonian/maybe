package net.xeona.maybe;

import static net.xeona.maybe.Maybe.just;
import static net.xeona.maybe.Maybe.maybe;
import static net.xeona.maybe.Maybe.nothing;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.Test;
import org.mockito.Mockito;

import net.xeona.function.BinaryConsumer;
import net.xeona.function.BinaryFunction;
import net.xeona.function.Consumer;
import net.xeona.function.Function;
import net.xeona.function.Provider;
import net.xeona.function.ToBooleanFunction;
import net.xeona.function.ToCharFunction;
import net.xeona.function.ToDoubleFunction;
import net.xeona.function.ToFloatFunction;
import net.xeona.function.ToIntFunction;
import net.xeona.function.ToLongFunction;
import net.xeona.function.ToShortFunction;
import net.xeona.function.VoidFunction;
import net.xeona.maybe.Maybe;
import net.xeona.maybe.MaybeBoolean;
import net.xeona.maybe.MaybeChar;
import net.xeona.maybe.MaybeDouble;
import net.xeona.maybe.MaybeFloat;
import net.xeona.maybe.MaybeInt;
import net.xeona.maybe.MaybeLong;
import net.xeona.maybe.MaybeShort;

public class MaybeTest {

	@Test
	public void verifyMaybeOfValueIsJustValue() {
		Object value = new Object();
		assertThat(maybe(value), is(just(value)));
	}

	@Test
	public void verifyMaybeOfNullIsNothing() {
		assertThat(maybe(null), is(nothing()));
	}

	@Test(expected = NullPointerException.class)
	public void verifyThrowsNullPointerExceptionOnJustNull() {
		just(null);
	}

	@Test
	public void verifyJustIsPresent() {
		assertThat(just(new Object()).isPresent(), is(true));
	}

	@Test
	public void verifyGetOnJustReturnsValue() {
		Object value = new Object();
		assertThat(just(value).get(), is(value));
	}

	@Test
	public void verifyValueReturnedWhenOrElseInvokedOnJust() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		Object otherValue = new Object();

		assertThat(maybe.orElse(otherValue), is(value));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyValueReturnedWhenOrElseGetInvokedOnJust() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		Provider<Object, RuntimeException> providerMock = mock(Provider.class);

		assertThat(maybe.orElseGet(providerMock), is(value));

		verifyNoMoreInteractions(providerMock);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyNoExceptionThrownOnOrElseGetWithThrowingProviderOnJust() {
		Maybe<Object> maybe = just(new Object());

		try {
			Provider<Object, Exception> providerMock = mock(Provider.class);
			when(providerMock.get()).thenThrow(Exception.class);

			maybe.orElseGet(providerMock);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyValueReturnedWhenOrElseThrowInvokedOnJust() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		Provider<RuntimeException, RuntimeException> providerMock = mock(Provider.class);

		assertThat(maybe.orElseThrow(providerMock), is(value));

		verifyNoMoreInteractions(providerMock);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyNoExceptionThrownOnOrElseThrowWithThrowingProviderOnJust() {
		Maybe<Object> maybe = just(new Object());

		try {
			Provider<RuntimeException, Exception> providerMock = mock(Provider.class);
			when(providerMock.get()).thenThrow(Exception.class);

			maybe.orElseThrow(providerMock);
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyIfPresentOnJustIsInvokedWithValue() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		Consumer<Object, RuntimeException> consumerMock = mock(Consumer.class);

		maybe.ifPresent(consumerMock);

		verify(consumerMock).consume(value);
		verifyNoMoreInteractions(consumerMock);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyFunctionNotInvokedOnIfAbsentOnJust() {
		Maybe<Object> maybe = just(new Object());

		VoidFunction<RuntimeException> functionMock = mock(VoidFunction.class);

		maybe.ifAbsent(functionMock);

		verifyNoMoreInteractions(functionMock);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyIfPresentInvokedOnByPresenceOnJust() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		Consumer<Object, RuntimeException> consumerMock = mock(Consumer.class);
		VoidFunction<RuntimeException> functionMock = mock(VoidFunction.class);

		maybe.byPresence(consumerMock, functionMock);

		verify(consumerMock).consume(value);
		verifyNoMoreInteractions(consumerMock, functionMock);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyFilterOnJustWithAcceptingPredicateIsJust() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		ToBooleanFunction<Object, RuntimeException> predicateMock = mock(ToBooleanFunction.class);
		when(predicateMock.apply(value)).thenReturn(true);

		Maybe<Object> filteredMaybe = maybe.filter(predicateMock);

		assertThat(filteredMaybe, is(just(value)));

		verify(predicateMock).apply(value);
		verifyNoMoreInteractions(predicateMock);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyFilterOnJustWithRejectingPredicateIsNothing() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		ToBooleanFunction<Object, RuntimeException> predicateMock = mock(ToBooleanFunction.class);
		when(predicateMock.apply(value)).thenReturn(false);

		Maybe<Object> filteredMaybe = maybe.filter(predicateMock);

		assertThat(filteredMaybe, is(nothing()));

		verify(predicateMock).apply(value);
		verifyNoMoreInteractions(predicateMock);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyExceptionPropogatesOnFilterWithThrowingPredicateOnJust() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		Exception exception = new Exception();

		try {
			ToBooleanFunction<Object, Exception> predicateMock = mock(ToBooleanFunction.class);
			when(predicateMock.apply(value)).thenThrow(exception);

			maybe.filter(predicateMock);
			fail();
		} catch (Exception e) {
			assertThat(e, is(sameInstance(exception)));
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyMapToBooleanOnJustIsJustBoolean() {
		try {
			verifyMapOnJustIsJustValue(ToBooleanFunction.class, true,
					(functionMock, value) -> functionMock.apply(value), Maybe::mapToBoolean, MaybeBoolean::just);
		} catch (Throwable t) {
			fail();
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyExceptionPropogatesOnMapToBooleanWithThrowingFunctionOnJust() {
		try {
			verifyExceptionPropogatesOnInvocationOnJust(ToBooleanFunction.class,
					(functionMock, value) -> functionMock.apply(value), Maybe::mapToBoolean);
		} catch (Throwable t) {
			fail();
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyMapToCharOnJustIsJustChar() {
		try {
			verifyMapOnJustIsJustValue(ToCharFunction.class, 'a', (functionMock, value) -> functionMock.apply(value),
					Maybe::mapToChar, MaybeChar::just);
		} catch (Throwable t) {
			fail();
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyExceptionPropogatesOnMapToCharWithThrowingFunctionOnJust() {
		try {
			verifyExceptionPropogatesOnInvocationOnJust(ToCharFunction.class,
					(functionMock, value) -> functionMock.apply(value), Maybe::mapToChar);
		} catch (Throwable t) {
			fail();
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyMapToShortOnJustIsJustShort() {
		try {
			verifyMapOnJustIsJustValue(ToShortFunction.class, (short) 42,
					(functionMock, value) -> functionMock.apply(value), Maybe::mapToShort, MaybeShort::just);
		} catch (Throwable t) {
			fail();
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyExceptionPropogatesOnMapToShortWithThrowingFunctionOnJust() {
		try {
			verifyExceptionPropogatesOnInvocationOnJust(ToShortFunction.class,
					(functionMock, value) -> functionMock.apply(value), Maybe::mapToShort);
		} catch (Throwable t) {
			fail();
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyMapToIntOnJustIsJustInt() {
		try {
			verifyMapOnJustIsJustValue(ToIntFunction.class, 42, (functionMock, value) -> functionMock.apply(value),
					Maybe::mapToInt, MaybeInt::just);
		} catch (Throwable t) {
			fail();
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyExceptionPropogatesOnMapToIntWithThrowingFunctionOnJust() {
		try {
			verifyExceptionPropogatesOnInvocationOnJust(ToIntFunction.class,
					(functionMock, value) -> functionMock.apply(value), Maybe::mapToInt);
		} catch (Throwable t) {
			fail();
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyMapToLongOnJustIsJustLong() {
		try {
			verifyMapOnJustIsJustValue(ToLongFunction.class, 42L, (functionMock, value) -> functionMock.apply(value),
					Maybe::mapToLong, MaybeLong::just);
		} catch (Throwable t) {
			fail();
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyExceptionPropogatesOnMapToLongWithThrowingFunctionOnJust() {
		try {
			verifyExceptionPropogatesOnInvocationOnJust(ToLongFunction.class,
					(functionMock, value) -> functionMock.apply(value),
					(maybe, functionMock) -> maybe.mapToLong(functionMock));
		} catch (Throwable t) {
			fail();
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyMapToFloatOnJustIsJustFloat() {
		try {
			verifyMapOnJustIsJustValue(ToFloatFunction.class, 42F, (functionMock, value) -> functionMock.apply(value),
					Maybe::mapToFloat, MaybeFloat::just);
		} catch (Throwable t) {
			fail();
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyExceptionPropogatesOnMapToFloatWithThrowingFunctionOnJust() {
		try {
			verifyExceptionPropogatesOnInvocationOnJust(ToFloatFunction.class,
					(functionMock, value) -> functionMock.apply(value), Maybe::mapToFloat);
		} catch (Throwable t) {
			fail();
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyMapToDoubleOnJustIsJustDouble() {
		try {
			verifyMapOnJustIsJustValue(ToDoubleFunction.class, 42.0, (functionMock, value) -> functionMock.apply(value),
					Maybe::mapToDouble, MaybeDouble::just);
		} catch (Throwable t) {
			fail();
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyExceptionPropogatesOnMapToDoubleWithThrowingFunctionOnJust() {
		try {
			verifyExceptionPropogatesOnInvocationOnJust(ToDoubleFunction.class,
					(functionMock, value) -> functionMock.apply(value), Maybe::mapToDouble);
		} catch (Throwable t) {
			fail();
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyFunctionAppliedWhenMapInvokedOnJust() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		Function<Object, Object, RuntimeException> functionMock = Mockito.mock(Function.class);
		Object mappedValue = new Object();
		when(functionMock.apply(value)).thenReturn(mappedValue);

		Maybe<Object> mappedMaybe = maybe.map(functionMock);

		assertThat(mappedMaybe, is(just(mappedValue)));

		verify(functionMock).apply(value);
		verifyNoMoreInteractions(functionMock);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyExceptionPropogatesOnMapWithThrowingFunctionOnJust() {
		try {
			verifyExceptionPropogatesOnInvocationOnJust(Function.class, (function, value) -> function.apply(value),
					Maybe::map);
		} catch (Throwable t) {
			fail();
		}
	}

	@Test
	public void verifyJustNotEmpty() {
		assertThat(just(new Object()), not(empty()));
	}

	@Test
	public void verifyJustHasSize1() {
		assertThat(just(new Object()), hasSize(1));
	}

	@Test
	public void verifyJustContainsItsValue() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		assertThat(maybe.contains(value), is(true));
	}

	@Test
	public void verifyJustDoesNotContainOtherValue() {
		Maybe<Object> maybe = just(new Object());
		assertThat(maybe.contains(new Object()), is(false));
	}

	@Test
	public void verifyJustContainsAllOfEmptyCollection() {
		assertThat(just(new Object()).containsAll(Collections.emptySet()), is(true));
	}

	@Test
	public void verifyJustContainsAllOfCollectionContainingOnlyValue() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		List<Object> collection = Arrays.asList(value, value);

		assertThat(maybe.containsAll(collection), is(true));
	}

	@Test
	public void verifyJustDoesNotContainAllOfCollectionContainingOtherValues() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		List<Object> collection = Arrays.asList(value, new Object());

		assertThat(maybe.containsAll(collection), is(false));
	}

	@Test
	public void verifyJustToArrayReturnsSingleElementArray() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		assertThat(maybe.toArray(), is(arrayContaining(value)));
	}

	@Test(expected = NoSuchElementException.class)
	public void verifyJustIteratorContainsSingleElement() {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		Iterator<Object> iterator = maybe.iterator();

		assertThat(iterator.hasNext(), is(true));
		assertThat(iterator.next(), is(value));
		assertThat(iterator.hasNext(), is(false));
		iterator.next();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void verifyUnsupportedOperationExceptionThrownOnAdd() {
		Maybe<Object> maybe = just(new Object());
		maybe.add(new Object());
	}
	
	@Test
	public void verifyUnsupportedOperationExceptionThrownOnAddAll() {
		
	}

	@Test
	public void verifyNothingIsNotPresent() {
		assertThat(nothing().isPresent(), is(false));
	}

	@Test(expected = NoSuchElementException.class)
	public void verifyThrowNoSuchElementExceptionWhenGetInvokedOnNothing() {
		nothing().get();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyConsumerNotInvokedWhenIfPresentInvokedOnNothing() {
		Maybe<Object> maybe = nothing();

		Consumer<Object, RuntimeException> consumerMock = mock(Consumer.class);

		maybe.ifPresent(consumerMock);

		verifyNoMoreInteractions(consumerMock);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyNothingReturnedWhenFilterInvokedOnNothing() {
		Maybe<Object> maybe = nothing();

		ToBooleanFunction<Object, RuntimeException> predicateMock = mock(ToBooleanFunction.class);

		assertThat(maybe.filter(predicateMock), is(nothing()));

		verifyNoMoreInteractions(predicateMock);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyNothingReturnedWhenMapInvokedOnNothing() {
		Maybe<Object> maybe = nothing();

		Function<Object, Object, RuntimeException> functionMock = mock(Function.class);

		assertThat(maybe.map(functionMock), is(nothing()));

		verifyNoMoreInteractions(functionMock);
	}

	@Test
	public void verifyOtherReturnedWhenOrElseInvokedOnNothing() {
		Maybe<Object> maybe = nothing();

		Object value = new Object();

		assertThat(maybe.orElse(value), is(value));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void verifyProviderInvokedWhenOrElseGetInvokedOnNothing() {
		Maybe<Object> maybe = nothing();

		Object value = new Object();
		Provider<Object, RuntimeException> providerMock = mock(Provider.class);
		when(providerMock.get()).thenReturn(value);

		assertThat(maybe.orElseGet(providerMock), is(value));

		verify(providerMock).get();
		verifyNoMoreInteractions(providerMock);
	}

	@Test(expected = RuntimeException.class)
	@SuppressWarnings("unchecked")
	public void verifyThrowsExceptionWhenProviderThrowsExceptionOnOrElseGetInvokedOnNothing() {
		Maybe<Object> maybe = nothing();

		Provider<Object, RuntimeException> providerMock = mock(Provider.class);
		when(providerMock.get()).thenThrow(RuntimeException.class);

		maybe.orElseGet(providerMock);
	}

	@Test
	public void verifyNothingIsEmpty() {
		assertThat(nothing(), empty());
	}

	@Test
	public void verifyNothingHasSize0() {
		assertThat(nothing(), hasSize(0));
	}

	@Test
	public void verifyConvertPopulatedOptionalToJust() {
		Object value = new Object();
		assertThat(Maybe.fromOptional(Optional.of(value)), is(just(value)));
	}

	@Test
	public void verifyConvertEmptyOptionalToNothing() {
		assertThat(Maybe.fromOptional(Optional.empty()), is(nothing()));
	}

	@Test
	public void verifyConvertJustToPopulatedOptional() {
		Object value = new Object();
		assertThat(Maybe.toOptional(just(value)), is(Optional.of(value)));
	}

	@Test
	public void verifyConvertNothingToEmptyOptional() {
		assertThat(Maybe.toOptional(nothing()), is(Optional.empty()));
	}

	@SuppressWarnings("unchecked")
	private static <F, T, M, X extends Throwable> void verifyMapOnJustIsJustValue(Class<? super F> functionClass,
			T functionReturnValue, BinaryFunction<? super F, Object, T, ? extends X> functionApplication,
			BinaryFunction<? super Maybe<Object>, ? super F, ? extends M, RuntimeException> mapApplication,
			Function<? super T, ? extends M, RuntimeException> justFunction) throws X {
		Object value = new Object();
		Maybe<Object> maybe = just(value);

		F functionMock = (F) mock(functionClass);
		when(functionApplication.apply(functionMock, value)).thenReturn(functionReturnValue);

		assertThat(mapApplication.apply(maybe, functionMock), is(justFunction.apply(functionReturnValue)));

		functionApplication.apply(verify(functionMock), value);
		verifyNoMoreInteractions(functionMock);
	}

	@SuppressWarnings("unchecked")
	private static <F, X extends Throwable> void verifyExceptionPropogatesOnInvocationOnJust(
			Class<? super F> functionClass, BinaryFunction<? super F, Object, ?, ? extends X> functionInvocation,
			BinaryConsumer<Maybe<Object>, ? super F, RuntimeException> maybeInvocation) throws X {
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
