package net.brtly.monkeyboard.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import junit.framework.Assert;
import net.brtly.monkeyboard.api.plugin.Bundle;
import net.brtly.monkeyboard.plugin.core.panel.PluginDockableLayout;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class BundleAndLayoutTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testBundleByteArraySerialization() throws Exception {
		Bundle b = new Bundle();

		b.putString("String", "abracadabra").putBoolean("bool", true)
				.putLong("long", 123456789L).putInt("int", 32767)
				.putString("String2", null).putFloat("float", 0.123f);

		byte[] ba = Bundle.toByteArray(b);
		b = Bundle.fromByteArray(ba);

		Assert.assertTrue("abracadabra".equals(b.getString("String")));
		Assert.assertTrue(b.getBoolean("bool"));
		Assert.assertTrue(b.getBoolean("non-existent bool", true));

	}

	@Test
	public void testPluginDockableLayoutSerialization() throws Exception {
		Bundle b = new Bundle();
		b.putString("String", "abracadabra").putBoolean("bool", true)
				.putLong("long", 123456789L).putInt("int", 32767)
				.putInt("int2", 32767).putString("String2", null)
				.putFloat("float", 0.123f);

		String s = this.getClass().getName();

		PluginDockableLayout l = new PluginDockableLayout(s, b);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);

		l.writeStream(dos);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		DataInputStream dis = new DataInputStream(bais);

		l.readStream(dis);

		b = l.getBundle();

		Assert.assertTrue("abracadabra".equals(b.getString("String")));
		Assert.assertTrue(b.getBoolean("bool"));
		Assert.assertTrue(b.getBoolean("non-existent bool", true));

	}

	@Test
	public void testPluginDockableLayoutWithNullBundle() throws Exception {
		Bundle b = null;

		String s = this.getClass().getName();

		PluginDockableLayout l = new PluginDockableLayout(s, b);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);

		l.writeStream(dos);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		DataInputStream dis = new DataInputStream(bais);

		l.readStream(dis);

		b = l.getBundle();

		Assert.assertNull(b);
	}
	
	@Test
	public void testPluginDockableLayoutWithEmptyBundle() throws Exception {
		Bundle b = new Bundle();

		String s = this.getClass().getName();

		PluginDockableLayout l = new PluginDockableLayout(s, b);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);

		l.writeStream(dos);

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		DataInputStream dis = new DataInputStream(bais);

		l.readStream(dis);

		b = l.getBundle();

		Assert.assertEquals(b, new Bundle());
	}

}
