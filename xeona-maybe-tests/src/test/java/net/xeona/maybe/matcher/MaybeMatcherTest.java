package net.xeona.maybe.matcher;

import static net.xeona.maybe.Maybe.just;
import static net.xeona.maybe.Maybe.nothing;
import static net.xeona.maybe.matcher.MaybeMatcher.isJust;
import static net.xeona.maybe.matcher.MaybeMatcher.isNothing;
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

import org.hamcrest.Description;
import org.hamcrest.Description.NullDescription;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class MaybeMatcherTest {

	@Test
	public void justMatcherOfValueMatchesJustContainingValue() {
		Object value = new Object();
		assertTrue(isJust(value).matches(just(value)));
	}

	@Test
	public void justMatcherOfValueDoesNotMatchJustOtherValue() {
		Object firstValue = new Object();
		Object secondValue = new Object();
		assertThat(firstValue, not(secondValue));
		assertFalse(isJust(firstValue).matches(just(secondValue)));
	}

	@Test
	public void justMatcherOfValueDoesNotMatchNothing() {
		assertFalse(isJust(new Object()).matches(nothing()));
	}

	@Test
	public void justMatcherOfValueDoesNotMatchArbitraryObject() {
		assertFalse(isJust(new Object()).matches(new Object()));
	}

	@Test
	public void justMatcherOfValueDoesNotMatchNull() {
		assertFalse(isJust(new Object()).matches(null));
	}

	@Test
	public void justMatcherOfValueMatcherInvokesValueMatcherOnJust() {
		Matcher<Object> matcherMock = mock(Matcher.class);
		Object value = new Object();
		isJust(matcherMock).matches(just(value));
		verify(matcherMock).matches(value);
		verifyNoMoreInteractions(matcherMock);
	}

	@Test
	public void justMatcherOfValueMatcherMatchesJustIfValueMatcherMatchesValue() {
		Matcher<Object> matcherMock = mock(Matcher.class);
		Object value = new Object();
		when(matcherMock.matches(value)).thenReturn(true);
		assertTrue(isJust(matcherMock).matches(just(value)));
	}

	@Test
	public void justMatcherOfValueMatcherDoesNotMatchJustIfValueMatcherDoesNotMatchValue() {
		Matcher<Object> matcherMock = mock(Matcher.class);
		Object value = new Object();
		when(matcherMock.matches(value)).thenReturn(false);
		assertFalse(isJust(matcherMock).matches(just(value)));
	}

	@Test
	public void justMatcherOfValueMatcherDoesNotMatchNothing() {
		assertFalse(isJust(mock(Matcher.class)).matches(nothing()));
	}

	@Test
	public void justMatcherOfValueMatcherDoesNotMatchArbitraryObject() {
		assertFalse(isJust(mock(Matcher.class)).matches(new Object()));
	}

	@Test
	public void justMatcherOfValueMatcherDoesNotMatchNull() {
		assertFalse(isJust(mock(Matcher.class)).matches(null));
	}

	@Test
	public void justMatcherInvokesDescribeToOfValueMatcherOnDescribeTo() {
		Matcher<Object> matcherMock = mock(Matcher.class);
		Description description = new NullDescription();
		isJust(matcherMock).describeTo(description);
		verify(matcherMock).describeTo(description);
		verifyNoMoreInteractions(matcherMock);
	}

	@Test
	public void justMatcherDescribesSelfAndValueMatcher() {
		Description description = new StringDescription();
		Matcher<Object> matcherMock = mock(Matcher.class);
		String matcherDescription = "Matcher";
		doAnswer(invocation -> ((Description) invocation.getArgument(0)).appendText(matcherDescription))
				.when(matcherMock).describeTo(description);
		isJust(matcherMock).describeTo(description);
		assertThat(description.toString(), is("Just [" + matcherDescription + "]"));
	}

	@Test
	public void nothingMatcherMatchesNothing() {
		assertTrue(isNothing().matches(nothing()));
	}

	@Test
	public void nothingMatcherDoesNotMatchJust() {
		assertFalse(isNothing().matches(just(new Object())));
	}

	@Test
	public void nothingMatcherDoesNotMatchArbitraryObject() {
		assertFalse(isNothing().matches(new Object()));
	}

	@Test
	public void nothingMatcherDoesNotMatchNull() {
		assertFalse(isNothing().matches(null));
	}

	@Test
	public void nothingMatcherDescribesSelf() {
		Description description = new StringDescription();
		isNothing().describeTo(description);
		assertThat(description.toString(), is("Nothing []"));
	}

}
