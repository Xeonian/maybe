package net.xeona.maybe.test;

import static java.util.Objects.requireNonNull;
import static net.xeona.maybe.Maybe.just;
import static net.xeona.maybe.Maybe.nothing;
import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import net.xeona.maybe.Maybe;
import net.xeona.maybe.MaybeInt;

public class MaybeIntMatcher extends BaseMatcher<MaybeInt> {

	private final Maybe<? extends Matcher<? super Integer>> maybeValueMatcher;

	private MaybeIntMatcher(Maybe<? extends Matcher<? super Integer>> maybeValueMatcher) {
		this.maybeValueMatcher = requireNonNull(maybeValueMatcher, "Maybe value matcher must not be null");
	}

	@Override
	public boolean matches(Object item) {
		boolean matches;
		if (item instanceof MaybeInt) {
			MaybeInt maybeInt = (MaybeInt) item;
			matches = maybeValueMatcher
					.mapToBoolean(
							valueMatcher -> maybeInt.mapToBoolean(value -> valueMatcher.matches(value)).orElse(false))
					.orElseGet(() -> !maybeInt.isPresent());
		} else {
			matches = false;
		}
		return matches;
	}

	@Override
	public void describeTo(Description description) {
		maybeValueMatcher.byPresence(valueMatcher -> {
			description.appendText("JustInt [");
			valueMatcher.describeTo(description);
			description.appendText("]");
		}, () -> description.appendText("NoInt []"));
	}

	public static MaybeIntMatcher isJustInt(int value) {
		return isJustInt(equalTo(value));
	}

	public static MaybeIntMatcher isJustInt(Matcher<? super Integer> valueMatcher) {
		return new MaybeIntMatcher(just(requireNonNull(valueMatcher, "Value matcher must not be null")));
	}

	public static MaybeIntMatcher isNoInt() {
		return new MaybeIntMatcher(nothing());
	}

}
