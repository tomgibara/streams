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

import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.Random;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

abstract class FuzzStreamTest extends TestCase {

	static final int MAX_WRITES = 1000;
	static final int MAX_LEN = 50;

	abstract WriteStream newWriter();

	abstract ReadStream newReader(WriteStream writer);

	boolean closeHonored() {return true; }

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

	@Test
	public void testAsInputStream() throws IOException {
		Random r = new Random(0L);
		for (int i = 0; i < 100; i++) {
			testAsInputStream(r.nextInt(256), r.nextBoolean(), r.nextBoolean());
		}
	}

	private void testAsInputStream(int offset, boolean skipWithArray, boolean readWithArray) throws IOException {
		WriteStream w = newWriter();
		for (int i = 0; i < 256; i++) {
			w.writeByte((byte) i);
		}
		ReadStream r = newReader(w);
		if (skipWithArray) {
			r.readBytes(new byte[offset]);
		} else {
			int count = 0;
			while (count < offset) {
				if (offset - count > 8) {
					r.readLong();
					count += 8;
				} else if (offset - count > 4) {
					r.readInt();
					count += 4;
				} else if (offset - count > 2) {
					r.readShort();
					count += 2;
				} else {
					r.readByte();
					count += 1;
				}
			}
		}
		InputStream in = r.asInputStream();
		if (readWithArray) {
			offset += in.read(new byte[256]);
			assertEquals(256, offset);
		} else {
			while (offset < 256) {
				int n = in.read();
				assertEquals(offset, n);
				offset ++;
			}
		}
		assertEquals(-1, in.read());
	}

	@Test
	public void testAsOutputStream() throws IOException {
		Random r = new Random(0L);
		for (int i = 0; i < 25; i++) {
			testAsOutputStream(r.nextInt(256));
		}
	}

	private void testAsOutputStream(int offset) throws IOException {
		WriteStream w = newWriter();

		int count = 0;
		while (count < offset) {
			if (offset - count > 8) {
				w.writeLong(0L);
				count += 8;
			} else if (offset - count > 4) {
				w.writeInt(0);
				count += 4;
			} else if (offset - count > 2) {
				w.writeShort((short) 0);
				count += 2;
			} else {
				w.writeByte((byte) 0);
				count += 1;
			}
		}

		OutputStream out = w.asOutputStream();
		for (; count < 256; count++) {
			out.write(count);
		}

		ReadStream r = newReader(w);
		byte[] bs = new byte[offset];
		r.readBytes(bs);
		assertArrayEquals(new byte[offset], bs);

		for (int i = offset; i < 256; i++) {
			assertEquals(i, r.readByte() & 0xff);
		}

		try {
			r.readByte();
			fail();
		} catch (EndOfStreamException e) {
			/* expected */
		}
	}

	public void testAsChannel() throws IOException {
		WriteStream w = newWriter();
		WritableByteChannel out = w.asChannel();
		byte[] moo = {'m', 'o', 'o'};
		out.write(ByteBuffer.wrap(moo));
		ReadStream r = newReader(w);
		ReadableByteChannel in = r.asChannel();
		ByteBuffer b = ByteBuffer.allocate(1);
		in.read(b);
		assertEquals(b.get(0), 'm');
		b.clear();
		in.read(b);
		assertEquals(b.get(0), 'o');
		b.clear();
		in.read(b);
		assertEquals(b.get(0), 'o');
		b.clear();
		in.read(b);
		assertTrue(b.hasRemaining());
	}

	public void testClosedReportClosed() {
		WriteStream w = newWriter();
		w.writeBytes(new byte[] {0,1,2});
		ReadStream r = newReader(w);
		ReadStream s = r.closedWith(StreamCloser.reportClosed());
		if (closeHonored()) {
			assertEquals((byte) 0, s.readByte());
			s.close();
			try {
				s.readByte();
				fail();
			} catch (StreamException e) {
				/* expected */
			}
			assertEquals((byte) 1, r.readByte());
			r.close();
			try {
				r.readByte();
				fail();
			} catch (StreamException e) {
				/* expected */
			}
		} else {
			assertEquals((byte) 0, s.readByte());
			s.close();
			try {
				s.readByte();
				fail();
			} catch (StreamException e) {
				/* expected */
			}
			assertEquals((byte) 1, r.readByte());
			r.close();
			assertEquals((byte) 2, r.readByte());
		}
	}

	public void testClosedDoNothing() {
		WriteStream w = newWriter();
		w.writeBytes(new byte[] {0,1,2});
		ReadStream r = newReader(w);
		ReadStream s = r.closedWith(StreamCloser.doNothing());
		if (closeHonored()) {
			assertEquals((byte) 0, s.readByte());
			s.close();
			assertEquals((byte) 1, s.readByte());
			r.close();
			try {
				r.readByte();
				fail();
			} catch (StreamException e) {
				/* expected */
			}
		} else {
			assertEquals((byte) 0, s.readByte());
			s.close();
			assertEquals((byte) 1, s.readByte());
			r.close();
			assertEquals((byte) 2, r.readByte());
		}
	}

	public void testCloseStream() {
		WriteStream w = newWriter();
		w.writeBytes(new byte[] {0,1,2});
		ReadStream r = newReader(w);
		ReadStream s = r.closedWith(StreamCloser.closeStream());
		if (closeHonored()) {
			assertEquals((byte) 0, s.readByte());
			s.close();
			try {
				s.readByte();
				fail();
			} catch (StreamException e) {
				/* expected */
			}
			try {
				r.readByte();
				fail();
			} catch (StreamException e) {
				/* expected */
			}
		} else {
			assertEquals((byte) 0, s.readByte());
			s.close();
			try {
				s.readByte();
				fail();
			} catch (StreamException e) {
				/* expected */
			}
			assertEquals((byte) 1, r.readByte());
		}
	}

	public void testIOStreamsClosure() throws IOException {
		WriteStream w = newWriter();
		OutputStream out = w.closedWith(StreamCloser.doNothing()).asOutputStream();
		out.write(0);
		out.close();
		out.write(1);
		ReadStream r = newReader(w);
		InputStream in = r.closedWith(StreamCloser.doNothing()).asInputStream();
		assertEquals(0, in.read());
		in.close();
		assertEquals(1, in.read());
		assertEquals(-1, in.read());
	}

	public void testBuffering() {
		// ensure more than one buffer is used, and also that a partial buffer is used
		int size = Streams.BUFFER_SIZE * 2 + Streams.BUFFER_SIZE / 2;
		WriteStream w = newWriter();
		try {
			try (BufferOnlyReadStream source = new BufferOnlyReadStream(size)) {
				source.to(w).transferFully();
			}

			ReadStream r = newReader(w);
			try (BufferOnlyWriteStream sink = new BufferOnlyWriteStream(size)) {
				sink.from(r).transferFully();
			}
		} catch (IllegalStateException e) {
			if (w.getBuffering() != StreamBuffering.UNSUPPORTED) {
				throw e; // rethrow exception
			}
		}
	}

	public void testPosition() {
		WriteStream w = newWriter();
		if (w.position() < 0L) return; // position not supported
		assertEquals(0L, w.position());
		w.writeByte((byte) 1);
		assertEquals(1L, w.position());
		w.writeBytes(new byte[100]);
		assertEquals(101L, w.position());
		ReadStream r = newReader(w);
		assertEquals(0L, r.position());
		r.readByte();
		assertEquals(1L, r.position());
		r.readBytes(new byte[100]);
		assertEquals(101L, r.position());

	}
}
