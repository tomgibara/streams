package com.tomgibara.streams;

import java.util.Arrays;
import java.util.Random;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

abstract class FuzzStreamTest extends TestCase {

	abstract WriteStream newWriter();
	
	abstract ReadStream newReader(WriteStream writer);
	
	@Test
	public void testFuzz() {
		Random r = new Random(0L);
		for (int i = 0; i < 1000; i++) {
			fuzz(r.nextLong());
		}
	}
	
	void fuzz(long seed) {
		Random r;
		r = new Random(seed);
		WriteStream s = newWriter();
		int count = 100;
		for (int i = 0; i < count; i++) {
			switch (r.nextInt(7)) {
			case 0:
				s.writeBoolean(r.nextBoolean());
				break;
			case 1:
				s.writeByte((byte) r.nextInt());
				break;
			case 2:
				s.writeShort((short) r.nextInt());
				break;
			case 3:
				s.writeInt(r.nextInt());
				break;
			case 4:
				s.writeLong(r.nextLong());
				break;
			case 5: {
				byte[] bs = new byte[r.nextInt(50)];
				r.nextBytes(bs);
				s.writeBytes(bs);
				break;
			}
			case 6: {
				byte[] bs = new byte[r.nextInt(50)];
				r.nextBytes(bs);
				int len = r.nextInt(bs.length + 1);
				int off = r.nextInt(bs.length - len + 1);
				s.writeBytes(bs, off, len);
				break;
			}
			}
		}
		r = new Random(seed);
		ReadStream b = newReader(s);
		for (int i = 0; i < count; i++) {
			switch(r.nextInt(7)) {
			case 0:
				assertEquals(r.nextBoolean(), b.readBoolean());
				break;
			case 1:
				assertEquals((byte) r.nextInt(), b.readByte());
				break;
			case 2:
				assertEquals((short) r.nextInt(), b.readShort());
				break;
			case 3:
				assertEquals(r.nextInt(), b.readInt());
				break;
			case 4:
				assertEquals(r.nextLong(), b.readLong());
				break;
			case 5: {
					byte[] bs = new byte[r.nextInt(50)];
					byte[] bs2 = new byte[ bs.length ];
					r.nextBytes(bs);
					b.readBytes(bs2);
					Assert.assertArrayEquals(bs, bs2);
					break;
			}
			case 6: {
				byte[] bs = new byte[r.nextInt(50)];
				r.nextBytes(bs);
				int len = r.nextInt(bs.length + 1);
				int off = r.nextInt(bs.length - len + 1);
				byte[] bs2 = new byte[ bs.length ];
				b.readBytes(bs2, off, len);
				Assert.assertArrayEquals(Arrays.copyOfRange(bs, off, off + len), Arrays.copyOfRange(bs2, off, off + len));
				break;
			}
			}
		}
		try {
			b.readByte();
			fail();
		} catch (StreamException e) {
			/* expected */
		}
	}

}
