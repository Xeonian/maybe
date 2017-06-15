package net.xeona.maybe.test;

import static java.util.Objects.requireNonNull;
import static net.xeona.maybe.Maybe.just;
import static net.xeona.maybe.Maybe.nothing;
import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import net.xeona.maybe.Maybe;

/**
 * An implementation of {@link Matcher} which matches instances of {@link Maybe}
 * 
 * @author Wesley Marsh
 *
 * @param <T>
 *            The type of value contained by <code>Just</code> instances to match against
 */
public class MaybeMatcher<T> extends BaseMatcher<Maybe<T>> {

	private final Maybe<? extends Matcher<? super T>> maybeValueMatcher;

	private MaybeMatcher(Maybe<? extends Matcher<? super T>> maybeValueMatcher) {
		this.maybeValueMatcher = requireNonNull(maybeValueMatcher, "Maybe value matcher must not be null");
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean matches(Object item) {
		boolean matches;
		if (item instanceof Maybe) {
			Maybe<T> maybe = (Maybe<T>) item;
			matches = maybeValueMatcher
					.mapToBoolean(
							valueMatcher -> maybe.mapToBoolean(value -> valueMatcher.matches(value)).orElse(false))
					.orElseGet(() -> !maybe.isPresent());
		} else {
			matches = false;
		}
		return matches;
	}

	@Override
	public void describeTo(Description description) {
		maybeValueMatcher.byPresence(valueMatcher -> {
			description.appendText("Just [");
			valueMatcher.describeTo(description);
			description.appendText("]");
		}, () -> description.appendText("Nothing []"));
	}

	/**
	 * Construct a matcher that will match instances of {@link Maybe} with a present value equal to <code>value</code>.
	 * 
	 * @param <T>
	 *            The type of <code>value</code> expected to be contained in matching instances of <code>Maybe</code>
	 * @param value
	 *            The value expected to be contained by matching instances of <code>Maybe</code>
	 * @return A <code>MaybeMatcher</code> which accepts instances of <code>Maybe</code> containing <code>value</code>
	 * @throws NullPointerException
	 *             If <code>value</code> is <code>null</code>, as populated instances of <code>Maybe</code> cannot
	 *             contain <code>null</code>. Consider using {@link #isNothing()} instead.
	 */
	public static <T> MaybeMatcher<T> isJust(T value) {
		return isJust(equalTo(requireNonNull(value, "Value must not be null")));
	}

	/**
	 * Construct a matcher that will match instances of {@link Maybe} with a present value that matches the given
	 * matcher.
	 * 
	 * @param <T>
	 *            The type of <code>value</code> expected to be contained in matching instances of <code>Maybe</code>
	 * @param valueMatcher
	 *            The {@link Matcher} to be tested against the values of populated instances of <code>Maybe</code>
	 * @return A <code>MaybeMatcher</code> which accepts populated instances of <code>Maybe</code> containing values
	 *         which match <code>valueMatcher</code>
	 * @throws NullPointerException
	 *             If <code>valueMatcher</code> is <code>null</code>
	 */
	public static <T> MaybeMatcher<T> isJust(Matcher<? super T> valueMatcher) {
		return new MaybeMatcher<>(just(requireNonNull(valueMatcher, "Value matcher must not be null")));
	}

	/**
	 * Construct a matcher that will matches instances of {@link Maybe} with no present value.
	 * 
	 * @param <T>
	 *            The type of <code>value</code> expected to be contained in matching instances of <code>Maybe</code>
	 * @return A <code>MaybeMatcher</code> which accepts instances of <code>Maybe</code> containing no value
	 */
	public static <T> MaybeMatcher<T> isNothing() {
		return new MaybeMatcher<>(nothing());
	}

}
