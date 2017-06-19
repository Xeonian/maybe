package net.xeona.maybe;

import static org.apache.commons.lang3.RandomUtils.nextBytes;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;

public class RandomNumberUtility {

	public static byte aRandomByte() {
		return nextBytes(Byte.BYTES)[0];
	}

	public static short aRandomShort() {
		return Shorts.fromByteArray(nextBytes(Short.BYTES));
	}

	public static int aRandomInt() {
		return Ints.fromByteArray(nextBytes(Integer.BYTES));
	}

	public static long aRandomLong() {
		return Longs.fromByteArray(nextBytes(Long.BYTES));
	}

	public static float aRandomFloat() {
		return Float.intBitsToFloat(aRandomInt());
	}

	public static double aRandomDouble() {
		return Double.longBitsToDouble(aRandomLong());
	}

}
