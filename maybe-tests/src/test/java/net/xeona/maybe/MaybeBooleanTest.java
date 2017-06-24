package net.xeona.maybe;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static net.xeona.maybe.MaybeBoolean.justBoolean;
import static net.xeona.maybe.MaybeBoolean.noBoolean;
import static net.xeona.maybe.NumberMatchers.doubleBinaryEqualTo;
import static net.xeona.maybe.NumberMatchers.floatBinaryEqualTo;
import static net.xeona.maybe.RandomNumberUtility.aRandomByte;
import static net.xeona.maybe.RandomNumberUtility.aRandomDouble;
import static net.xeona.maybe.RandomNumberUtility.aRandomFloat;
import static net.xeona.maybe.RandomNumberUtility.aRandomLong;
import static net.xeona.maybe.RandomNumberUtility.aRandomShort;
import static net.xeona.maybe.matcher.MaybeBooleanMatcher.isJustBoolean;
import static net.xeona.maybe.matcher.MaybeBooleanMatcher.isNoBoolean;
import static net.xeona.maybe.matcher.MaybeByteMatcher.isJustByte;
import static net.xeona.maybe.matcher.MaybeDoubleMatcher.isJustDouble;
import static net.xeona.maybe.matcher.MaybeFloatMatcher.isJustFloat;
import static net.xeona.maybe.matcher.MaybeIntMatcher.isJustInt;
import static net.xeona.maybe.matcher.MaybeLongMatcher.isJustLong;
import static net.xeona.maybe.matcher.MaybeMatcher.isJust;
import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
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

import org.apache.commons.lang3.RandomUtils;
import org.hamcrest.Matcher;
import org.junit.Test;

import net.xeona.function.BinaryConsumer;
import net.xeona.function.BinaryFunction;
import net.xeona.function.BooleanConsumer;
import net.xeona.function.BooleanFunction;
import net.xeona.function.BooleanProvider;
import net.xeona.function.BooleanToByteFunction;
import net.xeona.function.BooleanToCharFunction;
import net.xeona.function.BooleanToDoubleFunction;
import net.xeona.function.BooleanToFloatFunction;
import net.xeona.function.BooleanToIntFunction;
import net.xeona.function.BooleanToLongFunction;
import net.xeona.function.BooleanToShortFunction;
import net.xeona.function.BooleanUnaryOperator;
import net.xeona.function.Provider;
import net.xeona.function.ToBooleanFunction;
import net.xeona.function.VoidFunction;
import net.xeona.maybe.matcher.MaybeCharMatcher;
import net.xeona.maybe.matcher.MaybeShortMatcher;

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

	@Test
	public void mapToShortOnJustBooleanInvokesMapFunction() {
		MaybeBooleanTest
				.<BooleanToShortFunction<RuntimeException>> functionWithDelegateFunctionOnJustBooleanInvokesDelegateFunctionWithValue(
						MaybeBoolean::mapToShort, BooleanToShortFunction.class, BooleanToShortFunction::apply);
	}

	@Test
	public void mapToShortOnJustBooleanReturnsJustShortOfMapFunctionReturnValue() {
		MaybeBooleanTest
				.<BooleanToShortFunction<RuntimeException>, Short, MaybeShort> functionWithDelegateFunctionOnJustBooleanReturnsValue(
						MaybeBoolean::mapToShort, BooleanToShortFunction.class, BooleanToShortFunction::apply,
						aRandomShort(), (value, mappedValue) -> MaybeShortMatcher.isJustShort(mappedValue));
	}

	@Test
	public void mapToShortOnJustBooleanWithThrowingMapFunctionPropagatesThrownException() {
		MaybeBooleanTest
				.<BooleanToShortFunction<RuntimeException>> functionWithThrowingDelegateFunctionPropagatesThrownException(
						MaybeBoolean::mapToShort, BooleanToShortFunction.class, BooleanToShortFunction::apply);
	}

	@Test
	public void mapToShortOnJustBooleanWithNullMapFunctionThrowsNullPointerException() {
		functionWithNullArgumentThrowsNullPointerException(
				(BinaryConsumer<MaybeBoolean, BooleanToShortFunction<RuntimeException>, RuntimeException>) MaybeBoolean::mapToShort);
	}

	@Test
	public void mapToIntOnJustBooleanInvokesMapFunction() {
		MaybeBooleanTest
				.<BooleanToIntFunction<RuntimeException>> functionWithDelegateFunctionOnJustBooleanInvokesDelegateFunctionWithValue(
						MaybeBoolean::mapToInt, BooleanToIntFunction.class, BooleanToIntFunction::apply);
	}

	@Test
	public void mapToIntOnJustBooleanReturnsJustIntOfMapFunctionReturnValue() {
		MaybeBooleanTest
				.<BooleanToIntFunction<RuntimeException>, Integer, MaybeInt> functionWithDelegateFunctionOnJustBooleanReturnsValue(
						MaybeBoolean::mapToInt, BooleanToIntFunction.class, BooleanToIntFunction::apply, nextInt(),
						(value, mappedValue) -> isJustInt(mappedValue));
	}

	@Test
	public void mapToIntOnJustBooleanWithThrowingFunctionPropagatesThrownException() {
		MaybeBooleanTest
				.<BooleanToIntFunction<RuntimeException>> functionWithThrowingDelegateFunctionPropagatesThrownException(
						MaybeBoolean::mapToInt, BooleanToIntFunction.class, BooleanToIntFunction::apply);
	}

	@Test
	public void mapToIntOnJustBooleanWithNullMapFunctionThrowsNullPointerException() {
		functionWithNullArgumentThrowsNullPointerException(
				(BinaryConsumer<MaybeBoolean, BooleanToIntFunction<RuntimeException>, RuntimeException>) MaybeBoolean::mapToInt);
	}

	@Test
	public void mapToLongOnJustBooleanInvokesMapFunction() {
		MaybeBooleanTest
				.<BooleanToLongFunction<RuntimeException>> functionWithDelegateFunctionOnJustBooleanInvokesDelegateFunctionWithValue(
						MaybeBoolean::mapToLong, BooleanToLongFunction.class, BooleanToLongFunction::apply);
	}

	@Test
	public void mapToLongOnJustBooleanReturnsJustLongOfMapFunctionReturnValue() {
		MaybeBooleanTest
				.<BooleanToLongFunction<RuntimeException>, Long, MaybeLong> functionWithDelegateFunctionOnJustBooleanReturnsValue(
						MaybeBoolean::mapToLong, BooleanToLongFunction.class, BooleanToLongFunction::apply,
						aRandomLong(), (value, mappedValue) -> isJustLong(mappedValue));
	}

	@Test
	public void mapToLongOnJustBooleanWithThrowingFunctionPropagatesThrownException() {
		MaybeBooleanTest
				.<BooleanToLongFunction<RuntimeException>> functionWithThrowingDelegateFunctionPropagatesThrownException(
						MaybeBoolean::mapToLong, BooleanToLongFunction.class, BooleanToLongFunction::apply);
	}

	@Test
	public void mapToLongOnJustBooleanWithNullFunctionThrowsNullPointerException() {
		functionWithNullArgumentThrowsNullPointerException(
				(BinaryConsumer<MaybeBoolean, BooleanToLongFunction<RuntimeException>, RuntimeException>) MaybeBoolean::mapToLong);
	}

	@Test
	public void mapToFloatOnJustBooleanInvokesMapFunction() {
		MaybeBooleanTest
				.<BooleanToFloatFunction<RuntimeException>> functionWithDelegateFunctionOnJustBooleanInvokesDelegateFunctionWithValue(
						MaybeBoolean::mapToFloat, BooleanToFloatFunction.class, BooleanToFloatFunction::apply);
	}

	@Test
	public void mapToFloatOnJustBooleanReturnsJustFloatOfMapFunctionReturnValue() {
		MaybeBooleanTest
				.<BooleanToFloatFunction<RuntimeException>, Float, MaybeFloat> functionWithDelegateFunctionOnJustBooleanReturnsValue(
						MaybeBoolean::mapToFloat, BooleanToFloatFunction.class, BooleanToFloatFunction::apply,
						aRandomFloat(), (value, mappedValue) -> isJustFloat(floatBinaryEqualTo(mappedValue)));
	}

	@Test
	public void mapToFloatOnJustBooleanWithThrowingFunctionPropagatesThrownException() {
		MaybeBooleanTest
				.<BooleanToFloatFunction<RuntimeException>> functionWithThrowingDelegateFunctionPropagatesThrownException(
						MaybeBoolean::mapToFloat, BooleanToFloatFunction.class, BooleanToFloatFunction::apply);
	}

	@Test
	public void mapToFloatOnJustBooleanWithNullFunctionThrowsNullPointerException() {
		functionWithNullArgumentThrowsNullPointerException(
				(BinaryConsumer<MaybeBoolean, BooleanToFloatFunction<RuntimeException>, RuntimeException>) MaybeBoolean::mapToFloat);
	}

	@Test
	public void mapToDoubleOnJustBooleanInvokesMapFunction() {
		MaybeBooleanTest
				.<BooleanToDoubleFunction<RuntimeException>> functionWithDelegateFunctionOnJustBooleanInvokesDelegateFunctionWithValue(
						MaybeBoolean::mapToDouble, BooleanToDoubleFunction.class, BooleanToDoubleFunction::apply);
	}

	@Test
	public void mapToDoubleOnJustBooleanReturnsJustDoubleOfMapFunctionReturnValue() {
		MaybeBooleanTest
				.<BooleanToDoubleFunction<RuntimeException>, Double, MaybeDouble> functionWithDelegateFunctionOnJustBooleanReturnsValue(
						MaybeBoolean::mapToDouble, BooleanToDoubleFunction.class, BooleanToDoubleFunction::apply,
						aRandomDouble(), (value, mappedValue) -> isJustDouble(doubleBinaryEqualTo(mappedValue)));
	}

	@Test
	public void mapToDoubleOnJustBooleanWithThrowingMapFunctionPropagatesThrownException() {
		MaybeBooleanTest
				.<BooleanToDoubleFunction<RuntimeException>> functionWithThrowingDelegateFunctionPropagatesThrownException(
						MaybeBoolean::mapToDouble, BooleanToDoubleFunction.class, BooleanToDoubleFunction::apply);
	}

	@Test
	public void mapToDoubleOnJustBooleanWithNullMapFunctionThrowsNullPointerException() {
		functionWithNullArgumentThrowsNullPointerException(
				(BinaryConsumer<MaybeBoolean, BooleanToDoubleFunction<RuntimeException>, RuntimeException>) MaybeBoolean::mapToDouble);
	}

	@Test
	public void mapOnJustBooleanInvokesMapFunction() {
		MaybeBooleanTest
				.<BooleanFunction<Object, RuntimeException>> functionWithDelegateFunctionOnJustBooleanInvokesDelegateFunctionWithValue(
						MaybeBoolean::map, BooleanFunction.class, BooleanFunction::apply);
	}

	@Test
	public void mapOnJustBooleanReturnsJustOfMapFunctionReturnValue() {
		MaybeBooleanTest
				.<BooleanFunction<Object, RuntimeException>, Object, Maybe<Object>> functionWithDelegateFunctionOnJustBooleanReturnsValue(
						MaybeBoolean::map, BooleanFunction.class, BooleanFunction::apply, new Object(),
						(value, mappedValue) -> isJust(mappedValue));
	}

	@Test
	public void mapOnJustBooleanWithThrowingMapFunctionPropagatesThrownException() {
		MaybeBooleanTest
				.<BooleanFunction<Object, RuntimeException>> functionWithThrowingDelegateFunctionPropagatesThrownException(
						MaybeBoolean::map, BooleanFunction.class, BooleanFunction::apply);
	}

	@Test
	public void mapOnJustBooleanWithNullMapFunctionThrowsNullPointerException() {
		functionWithNullArgumentThrowsNullPointerException(
				(BinaryConsumer<MaybeBoolean, BooleanFunction<Object, RuntimeException>, RuntimeException>) MaybeBoolean::map);
	}

	@Test
	public void justBooleanIsNotEmpty() {
		assertThat(justBoolean(nextBoolean()), is(not(empty())));
	}

	@Test
	public void justBooleanHasSize1() {
		assertThat(justBoolean(nextBoolean()), hasSize(1));
	}

	@Test
	public void justBooleanContainsOwnValue() {
		boolean value = nextBoolean();
		assertTrue(justBoolean(value).contains(value));
	}

	@Test
	public void justBooleanDoesNotContainOtherValue() {
		boolean value = nextBoolean();
		assertFalse(justBoolean(value).contains(!value));
	}

	@Test
	public void justBooleanDoesNotContainArbitraryObject() {
		assertFalse(justBoolean(nextBoolean()).contains(new Object()));
	}

	@Test
	public void justBooleanDoesNotContainNull() {
		assertFalse(justBoolean(nextBoolean()).contains(null));
	}

	@Test
	public void justBooleanContainsAllOfCollectionOfValue() {
		boolean value = nextBoolean();
		assertTrue(justBoolean(value).containsAll(singleton(value)));
	}

	@Test
	public void justBooleanDoesNotContainAllOfCollectionContainingOtherValues() {
		boolean value = nextBoolean();
		assertFalse(justBoolean(value).containsAll(asList(value, !value)));
	}

	@Test(expected = NullPointerException.class)
	public void justBooleanContainsAllOfNullCollectionThrowsNullPointerException() {
		justBoolean(nextBoolean()).containsAll(null);
	}

	// TODO: Other collection methods on justboolean

	@Test
	public void hashCodeOfJustBooleanIsHashCodeOfValue() {
		boolean value = nextBoolean();
		assertThat(justBoolean(value).hashCode(), is(Boolean.hashCode(value)));
	}

	@Test
	public void justBooleanEqualsJustBooleanOfSameValue() {
		boolean value = nextBoolean();
		assertThat(justBoolean(value), is(equalTo(justBoolean(value))));
	}

	@Test
	public void justBooleanDoesNotEqualJustBooleanOfDifferentValue() {
		boolean value = nextBoolean();
		assertThat(justBoolean(value), is(not(equalTo(justBoolean(!value)))));
	}

	@Test
	public void justBooleanDoesNotEqualNoBoolean() {
		assertThat(justBoolean(nextBoolean()), is(not(equalTo(noBoolean()))));
	}

	@Test
	public void justBooleanDoesNotEqualArbitraryObject() {
		assertThat(justBoolean(nextBoolean()), is(not(equalTo(new Object()))));
	}

	@Test
	public void justBooleanDoesNotEqualNull() {
		assertThat(justBoolean(nextBoolean()), is(not(equalTo(null))));
	}

	@Test
	public void justToStringDescribesSelf() {
		boolean value = nextBoolean();
		assertThat(justBoolean(value).toString(), is("JustBoolean [" + value + "]"));
	}
	
	@Test
	public void noBooleanIsNotPresent() {
		assertFalse(noBoolean().isPresent());
	}
	
	@Test(expected = NullPointerException.class)
	public void getOnNoBooleanThrowsNullPointerException() {
		noBoolean().get();
	}
	
	@Test
	public void orElseGetOnNoBooleanReturnsAlternativeValue() {
		boolean value = nextBoolean();
	}

	private static void extractValueFunctionOnJustBooleanReturnsValue(
			ToBooleanFunction<? super MaybeBoolean, ? extends RuntimeException> extractFunction) {
		MaybeTestUtility.extractValueFunctionOnJustReturnsValue(RandomUtils::nextBoolean, MaybeBoolean::justBoolean,
				extractFunction);
	}

	private static <P> void extractValueFunctionWithAlternativeProviderOnJustBooleanDoesNotInvokeProvider(
			BinaryFunction<? super MaybeBoolean, ? super P, ?, RuntimeException> extractFunction,
			Class<? super P> providerClass) {
		MaybeTestUtility.extractValueFunctionWithAlternativeProviderOnJustDoesNotInvokeProvider(
				RandomUtils::nextBoolean, MaybeBoolean::justBoolean, extractFunction, providerClass);
	}

	private static void functionWithIfPresentConsumerOnJustBooleanInvokesConsumerWithValue(
			BinaryConsumer<? super MaybeBoolean, ? super BooleanConsumer<RuntimeException>, ? extends RuntimeException> functionInvocation) {
		MaybeTestUtility
				.<Boolean, MaybeBoolean, BooleanConsumer<RuntimeException>> justFunctionInvokesDelegateFunctionWithValue(
						RandomUtils::nextBoolean, MaybeBoolean::justBoolean, functionInvocation, BooleanConsumer.class,
						BooleanConsumer::consume);
	}

	private static void functionWithIfPresentConsumerOnJustBooleanWithThrowingConsumerPropagatesThrownException(
			BinaryConsumer<? super MaybeBoolean, ? super BooleanConsumer<RuntimeException>, ? extends RuntimeException> functionInvocation) {
		MaybeTestUtility
				.<Boolean, MaybeBoolean, BooleanConsumer<RuntimeException>> justFunctionWithThrowingDelegateFunctionPropagatesThrownException(
						RandomUtils::nextBoolean, MaybeBoolean::justBoolean, functionInvocation, BooleanConsumer.class,
						BooleanConsumer::consume);
	}

	private static void functionWithIfAbsentFunctionOnJustBooleanDoesNotInvokeIfAbsentFunction(
			BinaryConsumer<? super MaybeBoolean, ? super VoidFunction<RuntimeException>, ? extends RuntimeException> functionInvocation) {
		MaybeTestUtility
				.<Boolean, MaybeBoolean, VoidFunction<RuntimeException>> justFunctionDoesNotInvokeDelegateFunction(
						RandomUtils::nextBoolean, MaybeBoolean::justBoolean, functionInvocation, VoidFunction.class);
	}

	private static <F> void functionWithDelegateFunctionOnJustBooleanInvokesDelegateFunctionWithValue(
			BinaryConsumer<? super MaybeBoolean, ? super F, ? extends RuntimeException> functionInvocation,
			Class<? super F> delegateFunctionClass,
			BinaryConsumer<? super F, ? super Boolean, ? extends RuntimeException> delegateFunctionInvocation) {
		MaybeTestUtility.justFunctionInvokesDelegateFunctionWithValue(RandomUtils::nextBoolean,
				MaybeBoolean::justBoolean, functionInvocation, delegateFunctionClass, delegateFunctionInvocation);
	}

	private static <F, T, M> void functionWithDelegateFunctionOnJustBooleanReturnsValue(
			BinaryFunction<? super MaybeBoolean, ? super F, ? extends M, ? extends RuntimeException> functionInvocation,
			Class<? super F> delegateFunctionClass,
			BinaryFunction<? super F, ? super Boolean, T, ? extends RuntimeException> delegateFunctionInvocation,
			T delegateFunctionReturnValue,
			BinaryFunction<? super Boolean, ? super T, ? extends Matcher<? super M>, ? extends RuntimeException> expectedReturnValueMatcherFunction) {
		MaybeTestUtility.justFunctionWithDelegateFunctionReturnsValue(RandomUtils::nextBoolean,
				MaybeBoolean::justBoolean, functionInvocation, delegateFunctionClass, delegateFunctionInvocation,
				delegateFunctionReturnValue, expectedReturnValueMatcherFunction);
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

	private static void functionWithNullArgumentThrowsNullPointerException(
			BinaryConsumer<? super MaybeBoolean, ?, ? extends RuntimeException> functionInvocation) {
		MaybeTestUtility.justFunctionWithNullArgumentThrowsNullPointerException(RandomUtils::nextBoolean,
				MaybeBoolean::justBoolean, functionInvocation);
	}

	private static char nextChar() {
		return (char) nextInt(Character.MIN_VALUE, Character.MAX_VALUE);
	}

}
