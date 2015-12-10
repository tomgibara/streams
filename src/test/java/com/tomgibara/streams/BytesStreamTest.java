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

import java.util.Arrays;

import org.junit.Assert;


public class BytesStreamTest extends FuzzStreamTest {

	@Override
	WriteStream newWriter() { return new BytesWriteStream(new byte[32], Integer.MAX_VALUE); }

	@Override
	ReadStream newReader(WriteStream writer) {
		return new BytesReadStream(((BytesWriteStream) writer).getBytes(false));
	}
	
	public void testGetUncopiedBytes() {
		try (BytesWriteStream writer = new BytesWriteStream(new byte[8], Integer.MAX_VALUE)) {
			writer.writeLong(-1L);
			byte[] bytes = writer.getBytes(true);
			byte[] expected = new byte[8];
			Arrays.fill(expected, (byte) -1);
			Assert.assertArrayEquals(expected, bytes);
			try {
				writer.writeByte((byte) 0);
				fail();
			} catch (StreamException e) {
				/* expected */
			}
		}
	}

	public void testMaximumCapacity() {
		StreamBytes bytes = Streams.bytes(0, 5);
		WriteStream writer = bytes.writeStream();
		writer.writeByte((byte) 0);
		writer.writeByte((byte) 0);
		writer.writeByte((byte) 0);
		writer.writeByte((byte) 0);
		writer.writeByte((byte) 0);
		try {
			writer.writeByte((byte) 0);
			fail();
		} catch (EndOfStreamException e) {
			/* expected */
		}
	}

	public void testBytesReading() {
		try (ReadStream r = Streams.bytes(new byte[] {'a', 'b', 'c', 'd', 'e'}, 4).readStream()) {
			assertEquals('a', r.readByte());
			assertEquals('b', r.readByte());
			assertEquals('c', r.readByte());
			assertEquals('d', r.readByte());
			try {
				r.readByte();
				fail();
			} catch (EndOfStreamException e) {
				/* expected */
			}
		}
	}

	public void testBytesFlipping() {
		StreamBytes bytes = Streams.bytes(0, 100);
		for (int i = 0; i < 10; i++) {
			WriteStream w = bytes.writeStream();
			byte[] check = new byte[i * 10];
			for (int j = 0; j < check.length; j++) {
				byte v = (byte)(i + j);
				check[j] = v;
				w.writeByte(v);
			}
			Assert.assertArrayEquals(check, bytes.bytes());
			ReadStream r = bytes.readStream();
			for (int j = 0; j < check.length; j++) {
				assertEquals(check[j], r.readByte());
			}
			try {
				r.readByte();
				fail();
			} catch (EndOfStreamException e) {
				/* expected */
			}
		}
		byte[] full = new byte[100];
		WriteStream w = bytes.writeStream();
		w.writeBytes(full);
		try {
			w.writeByte((byte) 0);
		} catch (EndOfStreamException e) {
			/* expected */
		}
	}
}
