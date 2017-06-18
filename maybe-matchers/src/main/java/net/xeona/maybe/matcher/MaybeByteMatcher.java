package net.xeona.maybe.matcher;

import static java.util.Objects.requireNonNull;
import static net.xeona.maybe.Maybe.just;
import static net.xeona.maybe.Maybe.nothing;
import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import net.xeona.maybe.Maybe;
import net.xeona.maybe.MaybeByte;

public class MaybeByteMatcher extends BaseMatcher<MaybeByte> {

	private final Maybe<? extends Matcher<? super Byte>> maybeValueMatcher;

	private MaybeByteMatcher(Maybe<? extends Matcher<? super Byte>> maybeValueMatcher) {
		this.maybeValueMatcher = requireNonNull(maybeValueMatcher);
	}

	@Override
	public boolean matches(Object item) {
		boolean matches;
		if (item instanceof MaybeByte) {
			MaybeByte maybeByte = (MaybeByte) item;
			matches = maybeValueMatcher
					.mapToBoolean(
							valueMatcher -> maybeByte.mapToBoolean(value -> valueMatcher.matches(value)).orElse(false))
					.orElseGet(() -> !maybeByte.isPresent());
		} else {
			matches = false;
		}
		return matches;
	}

	@Override
	public void describeTo(Description description) {
		maybeValueMatcher.byPresence(valueMatcher -> {
			description.appendText("JustByte [");
			valueMatcher.describeTo(description);
			description.appendText("]");
		}, () -> description.appendText("NoByte []"));
	}

	public static MaybeByteMatcher isJustByte(byte value) {
		return isJustByte(equalTo(value));
	}

	public static MaybeByteMatcher isJustByte(Matcher<? super Byte> valueMatcher) {
		return new MaybeByteMatcher(just(valueMatcher));
	}

	public static MaybeByteMatcher isNoByte() {
		return new MaybeByteMatcher(nothing());
	}

}
