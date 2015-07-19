/*
 * Copyright 2015 Tom Gibara
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.tomgibara.streams;

import java.nio.CharBuffer;
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
			switch (r.nextInt(11)) {
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
			case 7: {
				char[] cs = chars(r);
				s.writeChars(cs);
				break;
			}
			case 8: {
				char[] cs = chars(r);
				int len = r.nextInt(cs.length + 1);
				int off = r.nextInt(cs.length - len + 1);
				s.writeChars(cs, off, len);
				break;
			}
			case 9: {
				s.writeChars(new String(chars(r)));
				break;
			}
			case 10: {
				s.writeChars(CharBuffer.wrap(chars(r)));
				break;
			}
			}
		}
		r = new Random(seed);
		ReadStream b = newReader(s);
		for (int i = 0; i < count; i++) {
			switch(r.nextInt(11)) {
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
			case 7: {
				char[] cs = chars(r);
				char[] cs2 = new char[cs.length];
				b.readChars(cs2);
				Assert.assertArrayEquals(cs, cs2);
				break;
			}
			case 8: {
				char[] cs = chars(r);
				int len = r.nextInt(cs.length + 1);
				int off = r.nextInt(cs.length - len + 1);
				char[] cs2 = new char[ cs.length ];
				b.readChars(cs2, off, len);
				Assert.assertArrayEquals(Arrays.copyOfRange(cs, off, off + len), Arrays.copyOfRange(cs2, off, off + len));
				break;
			}
			case 9: {
				assertEquals(new String(chars(r)), b.readChars());
				break;
			}
			case 10: {
				assertEquals(CharBuffer.wrap(chars(r)), CharBuffer.wrap(b.readChars().toCharArray()));
				break;
			}
			}
		}
		try {
			b.readByte();
			fail();
		} catch (EndOfStreamException e) {
			/* expected */
		}
	}
	
	private static char[] chars(Random r) {
		int len = r.nextInt(50);
		char[] cs = new char[len];
		for (int i = 0; i < len; i++) {
			cs[i] = (char) r.nextInt();
		}
		return cs;
	}

}
