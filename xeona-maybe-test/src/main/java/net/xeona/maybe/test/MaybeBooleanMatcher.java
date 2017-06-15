package net.xeona.maybe.test;

import static java.util.Objects.requireNonNull;
import static net.xeona.maybe.Maybe.just;
import static net.xeona.maybe.Maybe.nothing;
import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import net.xeona.maybe.Maybe;
import net.xeona.maybe.MaybeBoolean;

public class MaybeBooleanMatcher extends BaseMatcher<MaybeBoolean> {

	private final Maybe<? extends Matcher<? super Boolean>> maybeValueMatcher;

	private MaybeBooleanMatcher(Maybe<? extends Matcher<? super Boolean>> maybeValueMatcher) {
		this.maybeValueMatcher = requireNonNull(maybeValueMatcher, "Maybe value matcher must not be null");
	}

	@Override
	public boolean matches(Object item) {
		boolean matches;
		if (item instanceof MaybeBoolean) {
			MaybeBoolean maybeBoolean = (MaybeBoolean) item;
			matches = maybeValueMatcher.mapToBoolean(
					valueMatcher -> maybeBoolean.mapToBoolean(value -> valueMatcher.matches(value)).orElse(false))
					.orElseGet(() -> !maybeBoolean.isPresent());
		} else {
			matches = false;
		}
		return matches;
	}

	@Override
	public void describeTo(Description description) {
		maybeValueMatcher.byPresence(valueMatcher -> {
			description.appendText("JustBoolean [");
			valueMatcher.describeTo(description);
			description.appendText("]");
		}, () -> description.appendText("NoBoolean []"));
	}

	public static MaybeBooleanMatcher isJustBoolean(boolean value) {
		return isJustBoolean(equalTo(value));
	}

	public static MaybeBooleanMatcher isJustBoolean(Matcher<? super Boolean> valueMatcher) {
		return new MaybeBooleanMatcher(just(requireNonNull(valueMatcher, "Value matcher must not be null")));
	}

	public static MaybeBooleanMatcher isNoBoolean() {
		return new MaybeBooleanMatcher(nothing());
	}

}
