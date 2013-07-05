package net.brtly.monkeyboard.test;

import junit.framework.Assert;
import net.brtly.monkeyboard.util.Util;

import org.junit.Test;

public class UtilTest {

	@Test
	public void testRange() {
		int[] r = Util.range(0, 3); // == [0, 1, 2]
		Assert.assertEquals(r.length, 3);
		Assert.assertEquals(r[0], 0);
		Assert.assertEquals(r[2], 2);
	}

}
