package lab15.srey.serialize;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;

public class SerializeTest {

	@Test
	public void test() {
		int  n;
		long l;
		byte[] b;
		
		n = 0;
		b = LittleEndian.serialize(n);
		assertEquals("fail Little", n, LittleEndian.toInt(b));
		n = 15336;
		b = LittleEndian.serialize(n);
		assertEquals("fail Little", n, LittleEndian.toInt(b));
		n = 14236237;
		b = LittleEndian.serialize(n);
		assertEquals("fail Little", n, LittleEndian.toInt(b));
		l = 15L;
		b = LittleEndian.serialize(l);
		assertEquals("fail Little", l, LittleEndian.toLong(b));
		l = Calendar.getInstance().getTimeInMillis();
		b = LittleEndian.serialize(l);
		assertEquals("fail Little", l, LittleEndian.toLong(b));
	}
}
