package net.xeona.maybe.matcher;

import static java.util.Objects.requireNonNull;
import static net.xeona.maybe.Maybe.just;
import static net.xeona.maybe.Maybe.nothing;
import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import net.xeona.maybe.Maybe;
import net.xeona.maybe.MaybeChar;

public class MaybeCharMatcher extends BaseMatcher<MaybeChar> {

	private final Maybe<? extends Matcher<? super Character>> maybeValueMatcher;

	private MaybeCharMatcher(Maybe<? extends Matcher<? super Character>> maybeValueMatcher) {
		this.maybeValueMatcher = requireNonNull(maybeValueMatcher);
	}

	@Override
	public boolean matches(Object item) {
		boolean matches;
		if (item instanceof MaybeChar) {
			MaybeChar maybeChar = (MaybeChar) item;
			matches = maybeValueMatcher
					.mapToBoolean(
							valueMatcher -> maybeChar.mapToBoolean(value -> valueMatcher.matches(value)).orElse(false))
					.orElseGet(() -> !maybeChar.isPresent());
		} else {
			matches = false;
		}
		return matches;
	}

	@Override
	public void describeTo(Description description) {
		maybeValueMatcher.byPresence(valueMatcher -> {
			description.appendText("JustChar [");
			valueMatcher.describeTo(description);
			description.appendText("]");
		}, () -> description.appendText("NoChar []"));
	}

	public static MaybeCharMatcher isJustChar(char value) {
		return isJustChar(equalTo(value));
	}

	public static MaybeCharMatcher isJustChar(Matcher<? super Character> valueMatcher) {
		return new MaybeCharMatcher(just(valueMatcher));
	}

	public static MaybeCharMatcher isNoChar() {
		return new MaybeCharMatcher(nothing());
	}

}
