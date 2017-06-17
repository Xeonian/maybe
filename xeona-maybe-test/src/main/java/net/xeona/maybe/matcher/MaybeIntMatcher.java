package net.xeona.maybe.matcher;

import static java.util.Objects.requireNonNull;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import net.xeona.maybe.MaybeInt;

public abstract class MaybeIntMatcher extends BaseMatcher<MaybeInt> {

	private MaybeIntMatcher() {}

	@Override
	public boolean matches(Object item) {
		boolean matches;
		if (item instanceof MaybeInt) {
			MaybeInt maybeInt = (MaybeInt) item;
			matches = maybeIntMatches(maybeInt);
		} else {
			matches = false;
		}
		return matches;
	}

	protected abstract boolean maybeIntMatches(MaybeInt maybeInt);

	public static Matcher<MaybeInt> justInt() {
		return justInt(anything());
	}

	public static Matcher<MaybeInt> justInt(int value) {
		return justInt(equalTo(value));
	}

	public static Matcher<MaybeInt> justInt(Matcher<? super Integer> valueMatcher) {
		return new JustIntMatcher(valueMatcher);
	}

	public static Matcher<MaybeInt> isJustInt() {
		return is(justInt());
	}

	public static Matcher<MaybeInt> isJustInt(int value) {
		return is(justInt(value));
	}

	public static Matcher<MaybeInt> isJustInt(Matcher<? super Integer> valueMatcher) {
		return is(justInt(valueMatcher));
	}

	public static Matcher<MaybeInt> noInt() {
		return NoIntMatcher.instance();
	}

	public static Matcher<MaybeInt> isNoInt() {
		return is(noInt());
	}

	private static class JustIntMatcher extends MaybeIntMatcher {

		private final Matcher<? super Integer> valueMatcher;

		private JustIntMatcher(Matcher<? super Integer> valueMatcher) {
			this.valueMatcher = requireNonNull(valueMatcher, "Value matcher must not be null");
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("JustInt [");
			valueMatcher.describeTo(description);
			description.appendText("]");

		}

		@Override
		protected boolean maybeIntMatches(MaybeInt maybeInt) {
			return maybeInt.mapToBoolean(value -> valueMatcher.matches(value)).orElse(false);
		}

	}

	private static class NoIntMatcher extends MaybeIntMatcher {

		private static final NoIntMatcher INSTANCE = new NoIntMatcher();

		private NoIntMatcher() {}

		@Override
		public void describeTo(Description description) {
			description.appendText("NoInt []");
		}

		@Override
		protected boolean maybeIntMatches(MaybeInt maybeInt) {
			return maybeInt.isEmpty();
		}

		public static NoIntMatcher instance() {
			return INSTANCE;
		}

	}

}
