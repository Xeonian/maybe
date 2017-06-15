package net.xeona.maybe;

import static java.util.Objects.requireNonNull;
import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class MaybeMatcher<T> extends BaseMatcher<Maybe<T>> {

	private final Maybe<? extends Matcher<? super T>> maybeValueMatcher;

	private MaybeMatcher(Maybe<? extends Matcher<? super T>> maybeValueMatcher) {
		this.maybeValueMatcher = requireNonNull(maybeValueMatcher);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean matches(Object item) {
		return item instanceof Maybe && matches((Maybe<T>) item);
	}

	public boolean matches(Maybe<T> maybe) {
		return maybeValueMatcher
				.mapToBoolean(valueMatcher -> maybe.mapToBoolean(value -> valueMatcher.matches(value)).orElse(false))
				.orElseGet(() -> !maybe.isPresent());
	}

	@Override
	public void describeTo(Description description) {
		maybeValueMatcher.byPresence(valueMatcher -> {
			description.appendText("Just [");
			valueMatcher.describeTo(description);
			description.appendText("]");
		}, () -> description.appendText("Nothing []"));
	}

	public static <T> MaybeMatcher<T> isJust(T value) {
		return isJust(equalTo(value));
	}

	public static <T> MaybeMatcher<T> isJust(Matcher<? super T> valueMatcher) {
		return new MaybeMatcher<>(Maybe.just(valueMatcher));
	}

	public static <T> MaybeMatcher<T> isNothing() {
		return new MaybeMatcher<>(Maybe.nothing());
	}

}
