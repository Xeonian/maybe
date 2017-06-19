package net.xeona.maybe;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.hamcrest.Matcher;

import net.xeona.function.BinaryConsumer;
import net.xeona.function.BinaryFunction;
import net.xeona.function.Function;
import net.xeona.function.Provider;
import net.xeona.function.ToBooleanFunction;

@SuppressWarnings("unchecked")
public class MaybeTestUtility {

	public static <T, M> void extractValueFunctionOnJustReturnsValue(
			Provider<? extends T, ? extends RuntimeException> valueProvider,
			Function<? super T, ? extends M, ? extends RuntimeException> justFunction,
			ToBooleanFunction<? super M, ? extends RuntimeException> extractFunction) {
		T value = valueProvider.get();
		assertEquals(extractFunction.apply(justFunction.apply(value)), value);
	}

	public static <T, M, P> void extractValueFunctionWithAlternativeProviderOnJustDoesNotInvokeProvider(
			Provider<? extends T, ? extends RuntimeException> valueProvider,
			Function<? super T, ? extends M, ? extends RuntimeException> justFunction,
			BinaryFunction<? super M, ? super P, ?, RuntimeException> extractFunction, Class<? super P> providerClass) {
		P providerMock = (P) mock(providerClass);
		extractFunction.apply(justFunction.apply((valueProvider.get())), providerMock);
		verifyNoMoreInteractions(providerMock);
	}

	public static <T, M, F> void justFunctionInvokesDelegateFunctionWithValue(
			Provider<? extends T, ? extends RuntimeException> valueProvider,
			Function<? super T, ? extends M, ? extends RuntimeException> justFunction,
			BinaryConsumer<? super M, ? super F, ? extends RuntimeException> functionInvocation,
			Class<? super F> delegateFunctionClass,
			BinaryConsumer<? super F, ? super T, ? extends RuntimeException> delegateFunctionInvocation) {
		T value = valueProvider.get();
		F consumerMock = (F) mock(delegateFunctionClass);
		functionInvocation.consume(justFunction.apply(value), consumerMock);
		delegateFunctionInvocation.consume(verify(consumerMock), value);
		verifyNoMoreInteractions(consumerMock);
	}

	public static <T, M, F> void justFunctionWithThrowingDelegateFunctionPropagatesThrownException(
			Provider<? extends T, ? extends RuntimeException> valueProvider,
			Function<? super T, ? extends M, ? extends RuntimeException> justFunction,
			BinaryConsumer<? super M, ? super F, ? extends RuntimeException> functionInvocation,
			Class<? super F> delegateFunctionClass,
			BinaryConsumer<? super F, ? super T, ? extends RuntimeException> delegateFunctionInvocation) {
		T value = valueProvider.get();
		F consumerMock = (F) mock(delegateFunctionClass);
		RuntimeException exception = new RuntimeException();
		delegateFunctionInvocation.consume(doThrow(exception).when(consumerMock), value);
		try {
			functionInvocation.consume(justFunction.apply(value), consumerMock);
			fail();
		} catch (RuntimeException e) {
			assertThat(e, is(sameInstance(exception)));
		}
	}

	public static <T, M, F> void justFunctionDoesNotInvokeDelegateFunction(
			Provider<? extends T, ? extends RuntimeException> valueProvider,
			Function<? super T, ? extends M, ? extends RuntimeException> justFunction,
			BinaryConsumer<? super M, ? super F, ? extends RuntimeException> functionInvocation,
			Class<? super F> functionClass) {
		F functionMock = (F) mock(functionClass);
		functionInvocation.consume(justFunction.apply(valueProvider.get()), functionMock);
		verifyNoMoreInteractions(functionMock);
	}

	public static <T, M, F, U, N> void justFunctionWithDelegateFunctionReturnsValue(
			Provider<? extends T, ? extends RuntimeException> valueProvider,
			Function<? super T, ? extends M, ? extends RuntimeException> justFunction,
			BinaryFunction<? super M, ? super F, ? extends N, ? extends RuntimeException> functionInvocation,
			Class<? super F> delegateFunctionClass,
			BinaryFunction<? super F, ? super T, U, ? extends RuntimeException> delegateFunctionInvocation,
			U delegateFunctionReturnValue,
			BinaryFunction<? super T, ? super U, ? extends Matcher<? super N>, ? extends RuntimeException> expectedReturnValueMatcherFunction) {
		T value = valueProvider.get();
		F delegateFunctionMock = (F) mock(delegateFunctionClass);
		when(delegateFunctionInvocation.apply(delegateFunctionMock, value)).thenReturn(delegateFunctionReturnValue);
		assertThat(functionInvocation.apply(justFunction.apply(value), delegateFunctionMock),
				expectedReturnValueMatcherFunction.apply(value, delegateFunctionReturnValue));
	}

	public static <T, M> void justFunctionWithNullArgumentThrowsNullPointerException(
			Provider<? extends T, ? extends RuntimeException> valueProvider,
			Function<? super T, ? extends M, ? extends RuntimeException> justFunction,
			BinaryConsumer<? super M, ?, ? extends RuntimeException> functionInvocation) {
		try {
			functionInvocation.consume(justFunction.apply(valueProvider.get()), null);
			fail();
		} catch (NullPointerException e) {}
	}

}
