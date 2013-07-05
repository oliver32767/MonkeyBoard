package net.brtly.monkeyboard.util;

public class Util {

	/**
	 * Java implementation of Python's range() function, without steps
	 * 
	 * @param min
	 *            lower bound
	 * @param max
	 *            upper bound
	 * @return an array of integers between min (inclusive) and max (exclusive).
	 *         Returns an empty array if min > max
	 */
	public static int[] range(int min, int max) {
		if (min > max) {
			return new int[0];
		}
		int[] rv = new int[max - min];

		for (int i = 0; i < (max - min); i++) {
			rv[i] = min + i;
		}
		return rv;
	}

}
