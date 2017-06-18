package net.xeona.maybe.matcher;

import static java.util.Objects.requireNonNull;
import static net.xeona.maybe.Maybe.just;
import static net.xeona.maybe.Maybe.nothing;
import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import net.xeona.maybe.Maybe;
import net.xeona.maybe.MaybeShort;

public class MaybeShortMatcher extends BaseMatcher<MaybeShort> {

	private final Maybe<? extends Matcher<? super Short>> maybeValueMatcher;

	private MaybeShortMatcher(Maybe<? extends Matcher<? super Short>> maybeValueMatcher) {
		this.maybeValueMatcher = requireNonNull(maybeValueMatcher);
	}

	@Override
	public boolean matches(Object item) {
		boolean matches;
		if (item instanceof MaybeShort) {
			MaybeShort maybeShort = (MaybeShort) item;
			matches = maybeValueMatcher
					.mapToBoolean(
							valueMatcher -> maybeShort.mapToBoolean(value -> valueMatcher.matches(value)).orElse(false))
					.orElseGet(() -> !maybeShort.isPresent());
		} else {
			matches = false;
		}
		return matches;
	}

	@Override
	public void describeTo(Description description) {
		maybeValueMatcher.byPresence(valueMatcher -> {
			description.appendText("JustShort [");
			valueMatcher.describeTo(description);
			description.appendText("]");
		}, () -> description.appendText("NoShort []"));
	}

	public static MaybeShortMatcher isJustShort(short value) {
		return isJustShort(equalTo(value));
	}

	public static MaybeShortMatcher isJustShort(Matcher<? super Short> valueMatcher) {
		return new MaybeShortMatcher(just(valueMatcher));
	}

	public static MaybeShortMatcher isNoShort() {
		return new MaybeShortMatcher(nothing());
	}

}
