package net.xeona.maybe.matcher;

import static java.util.Objects.requireNonNull;
import static net.xeona.maybe.Maybe.just;
import static net.xeona.maybe.Maybe.nothing;
import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import net.xeona.maybe.Maybe;
import net.xeona.maybe.MaybeLong;

public class MaybeLongMatcher extends BaseMatcher<MaybeLong> {

	private final Maybe<? extends Matcher<? super Long>> maybeValueMatcher;

	private MaybeLongMatcher(Maybe<? extends Matcher<? super Long>> maybeValueMatcher) {
		this.maybeValueMatcher = requireNonNull(maybeValueMatcher);
	}

	@Override
	public boolean matches(Object item) {
		boolean matches;
		if (item instanceof MaybeLong) {
			MaybeLong maybeLong = (MaybeLong) item;
			matches = maybeValueMatcher
					.mapToBoolean(
							valueMatcher -> maybeLong.mapToBoolean(value -> valueMatcher.matches(value)).orElse(false))
					.orElseGet(() -> !maybeLong.isPresent());
		} else {
			matches = false;
		}
		return matches;
	}

	@Override
	public void describeTo(Description description) {
		maybeValueMatcher.byPresence(valueMatcher -> {
			description.appendText("JustLong [");
			valueMatcher.describeTo(description);
			description.appendText("]");
		}, () -> description.appendText("NoLong []"));
	}

	public static MaybeLongMatcher isJustLong(long value) {
		return isJustLong(equalTo(value));
	}

	public static MaybeLongMatcher isJustLong(Matcher<? super Long> valueMatcher) {
		return new MaybeLongMatcher(just(valueMatcher));
	}

	public static MaybeLongMatcher isNoLong() {
		return new MaybeLongMatcher(nothing());
	}

}
