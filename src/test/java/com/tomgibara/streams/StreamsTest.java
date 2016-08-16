/*
 * Copyright 2016 Tom Gibara
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

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;

public class StreamsTest {

	@Test
	public void testNoReadOnlyBuffer() {
		ByteBuffer b = ByteBuffer.allocate(0).asReadOnlyBuffer();
		try {
			Streams.streamBuffer(b);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			/* expected */
		}
	}

	@Test
	public void testBasicBuffer() {
		StreamBuffer buffer = Streams.streamBuffer(ByteBuffer.allocate(100));
		String str = "This is a test!";
		buffer.writeStream().writeChars(str);
		buffer.buffer().flip();
		Assert.assertEquals(str, buffer.readStream().readChars());
	}

	@Test
	public void testStreamFromEmpty() {
		ReadStream s = Streams.streamFromEmpty();
		try {
			s.readByte();
			Assert.fail();
		} catch (EndOfStreamException e) {
			/* expected */
		}
	}

	@Test
	public void testStreamToEmpty() {
		WriteStream s = Streams.streamToEmpty();
		try {
			s.writeByte((byte) 0);
			Assert.fail();
		} catch (EndOfStreamException e) {
			/* expected */
		}
	}
}
