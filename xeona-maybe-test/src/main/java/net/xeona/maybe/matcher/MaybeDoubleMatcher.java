package net.xeona.maybe.matcher;

import static java.util.Objects.requireNonNull;
import static net.xeona.maybe.Maybe.just;
import static net.xeona.maybe.Maybe.nothing;
import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import net.xeona.maybe.Maybe;
import net.xeona.maybe.MaybeDouble;

public class MaybeDoubleMatcher extends BaseMatcher<MaybeDouble> {

	private final Maybe<? extends Matcher<? super Double>> maybeValueMatcher;

	private MaybeDoubleMatcher(Maybe<? extends Matcher<? super Double>> maybeValueMatcher) {
		this.maybeValueMatcher = requireNonNull(maybeValueMatcher);
	}

	@Override
	public boolean matches(Object item) {
		boolean matches;
		if (item instanceof MaybeDouble) {
			MaybeDouble maybeDouble = (MaybeDouble) item;
			matches = maybeValueMatcher.mapToBoolean(
					valueMatcher -> maybeDouble.mapToBoolean(value -> valueMatcher.matches(value)).orElse(false))
					.orElseGet(() -> !maybeDouble.isPresent());
		} else {
			matches = false;
		}
		return matches;
	}

	@Override
	public void describeTo(Description description) {
		maybeValueMatcher.byPresence(valueMatcher -> {
			description.appendText("JustDouble [");
			valueMatcher.describeTo(description);
			description.appendText("]");
		}, () -> description.appendText("NoDouble []"));
	}

	public static MaybeDoubleMatcher isJustDouble(double value) {
		return isJustDouble(equalTo(value));
	}

	public static MaybeDoubleMatcher isJustDouble(Matcher<? super Double> valueMatcher) {
		return new MaybeDoubleMatcher(just(valueMatcher));
	}

	public static MaybeDoubleMatcher isNoDouble() {
		return new MaybeDoubleMatcher(nothing());
	}

}
