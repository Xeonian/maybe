package net.xeona.maybe.matcher;

import static java.util.Arrays.stream;
import static net.xeona.maybe.matcher.MaybeIntMatcher.isJustInt;
import static net.xeona.maybe.matcher.MaybeIntMatcher.isNoInt;
import static net.xeona.maybe.matcher.MaybeIntMatcher.justInt;
import static net.xeona.maybe.matcher.MaybeIntMatcher.noInt;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.apache.commons.text.RandomStringGenerator;
import org.hamcrest.Description;
import org.hamcrest.Description.NullDescription;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

import net.xeona.function.IntToBooleanFunction;
import net.xeona.maybe.MaybeInt;

@SuppressWarnings("unchecked")
public class MaybeIntMatcherTest {

	@Test
	public void justIntMatcherMatchesJustInt() {
		assertTrue(justInt().matches(MaybeInt.justInt(randomInt())));
	}

	@Test
	public void justIntMatcherDoesNotMatchNoInt() {
		assertFalse(justInt().matches(MaybeInt.noInt()));
	}

	@Test
	public void justIntMatcherDoesNotMatchArbitraryObject() {
		assertFalse(justInt().matches(new Object()));
	}

	@Test
	public void justIntMatcherDoesNotMatchNull() {
		assertFalse(justInt().matches(null));
	}

	@Test
	public void justIntMatcherOfValueMatchesJustIntOfValue() {
		int value = randomInt();
		assertTrue(justInt(value).matches(MaybeInt.justInt(value)));
	}

	@Test
	public void justIntMatcherOfValueDoesNotMatchJustIntOfOtherValue() {
		int firstValue = randomInt();
		int secondValue = randomInt(value -> value != firstValue);
		assertThat(firstValue, is(not(secondValue)));
		assertFalse(justInt(firstValue).matches(MaybeInt.justInt(secondValue)));
	}

	@Test
	public void justIntMatcherOfValueDoesNotMatchNoInt() {
		assertFalse(justInt(randomInt()).matches(MaybeInt.noInt()));
	}

	@Test
	public void justIntMatcherOfValueDoesNotMatchArbitraryObject() {
		assertFalse(justInt(randomInt()).matches(new Object()));
	}

	@Test
	public void justIntMatcherOfValueDoesNotMatchNull() {
		assertFalse(justInt(randomInt()).matches(null));
	}

	@Test
	public void justIntMatcherOfValueMatcherInvokesValueMatcherOnJustInt() {
		Matcher<Object> matcherMock = mock(Matcher.class);
		int value = randomInt();
		justInt(matcherMock).matches(MaybeInt.justInt(value));
		verify(matcherMock).matches(value);
		verifyNoMoreInteractions(matcherMock);
	}

	@Test
	public void justIntMatcherOfValueMatcherMatchesJustIntWhenValueMatcherMatchesValue() {
		Matcher<Object> matcherMock = mock(Matcher.class);
		int value = randomInt();
		when(matcherMock.matches(value)).thenReturn(true);
		assertTrue(justInt(matcherMock).matches(MaybeInt.justInt(value)));
	}

	@Test
	public void justIntMatcherOfValueMatcherDoesNotMatchJustIntWhenValueMatcherDoesNotMatchValue() {
		Matcher<Object> matcherMock = mock(Matcher.class);
		int value = randomInt();
		when(matcherMock.matches(value)).thenReturn(false);
		assertFalse(justInt(matcherMock).matches(MaybeInt.justInt(value)));
	}

	@Test
	public void justIntMatcherOfValueMatcherDoesNotMatchNothing() {
		assertFalse(justInt(mock(Matcher.class)).matches(MaybeInt.noInt()));
	}

	@Test
	public void justIntMatcherOfValueMatcherDoesNotMatchArbitraryObject() {
		assertFalse(justInt(mock(Matcher.class)).matches(new Object()));
	}

	@Test
	public void justIntMatcherOfValueMatcherDoesNotMatchNull() {
		assertFalse(justInt(mock(Matcher.class)).matches(null));
	}

	@Test
	public void justIntMatcherInvokesDescribeToOfValueMatcherOnDescribeTo() {
		Matcher<Object> matcherMock = mock(Matcher.class);
		Description description = new NullDescription();
		justInt(matcherMock).describeTo(description);
		verify(matcherMock).describeTo(description);
		verifyNoMoreInteractions(matcherMock);
	}

	@Test
	public void justMatcherDescribesSelfAndValueMatcher() {
		Matcher<Object> matcherMock = mock(Matcher.class);
		Description description = new StringDescription();
		String matcherDescription = new RandomStringGenerator.Builder().withinRange('a', 'z').build()
				.generate(nextInt(5, 20));
		doAnswer(invocation -> ((Description) invocation.getArgument(0)).appendText(matcherDescription))
				.when(matcherMock).describeTo(description);
		justInt(matcherMock).describeTo(description);
		assertThat(description.toString(), is("JustInt [" + matcherDescription + "]"));
	}

	@Test
	public void isJustIntMatcherMatchesJustInt() {
		assertTrue(isJustInt().matches(MaybeInt.justInt(randomInt())));
	}

	@Test
	public void isJustIntMatcherDoesNotMatchNoInt() {
		assertFalse(isJustInt().matches(MaybeInt.noInt()));
	}

	@Test
	public void isJustIntMatcherDoesNotMatchArbitraryObject() {
		assertFalse(isJustInt().matches(new Object()));
	}

	@Test
	public void isJustIntMatcherDoesNotMatchNull() {
		assertFalse(isJustInt().matches(null));
	}

	@Test
	public void isJustIntMatcherOfValueMatchesJustIntOfValue() {
		int value = randomInt();
		assertTrue(isJustInt(value).matches(MaybeInt.justInt(value)));
	}

	@Test
	public void isJustIntMatcherOfValueDoesNotMatchJustIntOfOtherValue() {
		int firstValue = randomInt();
		int secondValue = randomInt(value -> value != firstValue);
		assertThat(firstValue, is(not(secondValue)));
		assertFalse(isJustInt(firstValue).matches(MaybeInt.justInt(secondValue)));
	}

	@Test
	public void isJustIntMatcherOfValueDoesNotMatchNoInt() {
		assertFalse(isJustInt(randomInt()).matches(MaybeInt.noInt()));
	}

	@Test
	public void isJustIntMatcherOfValueDoesNotMatchArbitraryObject() {
		assertFalse(isJustInt(randomInt()).matches(new Object()));
	}

	@Test
	public void isJustIntMatcherOfValueDoesNotMatchNull() {
		assertFalse(isJustInt(randomInt()).matches(null));
	}

	@Test
	public void isJustIntMatcherOfValueMatcherInvokesValueMatcherOnJustInt() {
		Matcher<Object> matcherMock = mock(Matcher.class);
		int value = randomInt();
		isJustInt(matcherMock).matches(MaybeInt.justInt(value));
		verify(matcherMock).matches(value);
		verifyNoMoreInteractions(matcherMock);
	}

	@Test
	public void isJustIntMatcherOfValueMatcherMatchesJustIntWhenValueMatcherMatchesValue() {
		Matcher<Object> matcherMock = mock(Matcher.class);
		int value = randomInt();
		when(matcherMock.matches(value)).thenReturn(true);
		assertTrue(isJustInt(matcherMock).matches(MaybeInt.justInt(value)));
	}

	@Test
	public void isJustIntMatcherOfValueMatcherDoesNotMatchJustIntWhenValueMatcherDoesNotMatchValue() {
		Matcher<Object> matcherMock = mock(Matcher.class);
		int value = randomInt();
		when(matcherMock.matches(value)).thenReturn(false);
		assertFalse(isJustInt(matcherMock).matches(MaybeInt.justInt(value)));
	}

	@Test
	public void isJustIntMatcherOfValueMatcherDoesNotMatchNothing() {
		assertFalse(isJustInt(mock(Matcher.class)).matches(MaybeInt.noInt()));
	}

	@Test
	public void isJustIntMatcherOfValueMatcherDoesNotMatchArbitraryObject() {
		assertFalse(isJustInt(mock(Matcher.class)).matches(new Object()));
	}

	@Test
	public void isJustIntMatcherOfValueMatcherDoesNotMatchNull() {
		assertFalse(isJustInt(mock(Matcher.class)).matches(null));
	}

	@Test
	public void noIntMatcherMatchesNoInt() {
		assertTrue(noInt().matches(MaybeInt.noInt()));
	}

	@Test
	public void noIntMatcherDoesNotMatchJustInt() {
		assertFalse(noInt().matches(MaybeInt.justInt(randomInt())));
	}

	@Test
	public void noIntMatcherDoesNotMatchArbitraryObject() {
		assertFalse(noInt().matches(new Object()));
	}

	@Test
	public void noIntMatcherDoesNotMatchNull() {
		assertFalse(noInt().matches(null));
	}

	@Test
	public void noIntMatcherDescribesSelf() {
		Description description = new StringDescription();
		noInt().describeTo(description);
		assertThat(description.toString(), is("NoInt []"));
	}

	@Test
	public void isNoIntMatcherMatchesNoInt() {
		assertTrue(isNoInt().matches(MaybeInt.noInt()));
	}

	@Test
	public void isNoIntMatcherDoesNotMatchJustInt() {
		assertFalse(isNoInt().matches(MaybeInt.justInt(randomInt())));
	}

	@Test
	public void isNoIntMatcherDoesNotMatchArbitraryObject() {
		assertFalse(isNoInt().matches(new Object()));
	}

	@Test
	public void isNoIntMatcherDoesNotMatchNull() {
		assertFalse(isNoInt().matches(null));
	}

	@SafeVarargs
	private static int randomInt(IntToBooleanFunction<? extends RuntimeException>... rules) {
		int value;
		do {
			value = nextInt();
		} while (!valuePassesRules(value, rules));
		return value;
	}

	@SafeVarargs
	private static boolean valuePassesRules(int value, IntToBooleanFunction<? extends RuntimeException>... rules) {
		return stream(rules).allMatch(rule -> rule.apply(value));
	}

}
