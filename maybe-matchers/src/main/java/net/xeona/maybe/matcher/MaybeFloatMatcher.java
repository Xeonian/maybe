package net.xeona.maybe.matcher;

import static java.util.Objects.requireNonNull;
import static net.xeona.maybe.Maybe.just;
import static net.xeona.maybe.Maybe.nothing;
import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import net.xeona.maybe.Maybe;
import net.xeona.maybe.MaybeFloat;

public class MaybeFloatMatcher extends BaseMatcher<MaybeFloat> {

	private final Maybe<? extends Matcher<? super Float>> maybeValueMatcher;

	private MaybeFloatMatcher(Maybe<? extends Matcher<? super Float>> maybeValueMatcher) {
		this.maybeValueMatcher = requireNonNull(maybeValueMatcher);
	}

	@Override
	public boolean matches(Object item) {
		boolean matches;
		if (item instanceof MaybeFloat) {
			MaybeFloat maybeFloat = (MaybeFloat) item;
			matches = maybeValueMatcher
					.mapToBoolean(
							valueMatcher -> maybeFloat.mapToBoolean(value -> valueMatcher.matches(value)).orElse(false))
					.orElseGet(() -> !maybeFloat.isPresent());
		} else {
			matches = false;
		}
		return matches;
	}

	@Override
	public void describeTo(Description description) {
		maybeValueMatcher.byPresence(valueMatcher -> {
			description.appendText("JustFloat [");
			valueMatcher.describeTo(description);
			description.appendText("]");
		}, () -> description.appendText("NoFloat []"));
	}

	public static MaybeFloatMatcher isJustFloat(float value) {
		return isJustFloat(equalTo(value));
	}

	public static MaybeFloatMatcher isJustFloat(Matcher<? super Float> valueMatcher) {
		return new MaybeFloatMatcher(just(valueMatcher));
	}

	public static MaybeFloatMatcher isNoFloat() {
		return new MaybeFloatMatcher(nothing());
	}

}
