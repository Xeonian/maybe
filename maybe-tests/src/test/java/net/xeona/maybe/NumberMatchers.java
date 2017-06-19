package net.xeona.maybe;

import static java.lang.Double.doubleToRawLongBits;
import static java.lang.Float.floatToRawIntBits;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class NumberMatchers {

	public static Matcher<Float> floatBinaryEqualTo(float value) {
		return new BaseMatcher<Float>() {
			@Override
			public boolean matches(Object item) {
				return item instanceof Float
						&& floatToRawIntBits(((Float) item).floatValue()) == floatToRawIntBits(value);
			}

			@Override
			public void describeTo(Description description) {
				description.appendText(toString());
			}

			@Override
			public String toString() {
				return Integer.toHexString(floatToRawIntBits(value));
			}
		};
	}

	public static Matcher<Double> doubleBinaryEqualTo(double value) {
		return new BaseMatcher<Double>() {
			@Override
			public boolean matches(Object item) {
				return item instanceof Double
						&& doubleToRawLongBits(((Double) item).doubleValue()) == doubleToRawLongBits(value);
			}

			@Override
			public void describeTo(Description description) {
				description.appendText(toString());
			}

			@Override
			public String toString() {
				return Long.toHexString(doubleToRawLongBits(value));
			}
		};
	}

}
