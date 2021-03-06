/*
 * Copyright Tom Gibara
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
import java.util.Random;

import junit.framework.TestCase;

public class SeqStreamTest /*extends FuzzStreamTest*/ extends TestCase {

	public void testVoid() {
		ReadStream r = Streams.concatReadStreams();
		try {
			r.readByte();
			fail();
		} catch (EndOfStreamException e) {
			/* expected */
		}
		WriteStream w = Streams.concatWriteStreams();
		try {
			w.writeByte((byte) 0);
			fail();
		} catch (EndOfStreamException e) {
			/* expected */
		}
	}

	public void testSplitRead() {
		Random r = new Random(0);
		for (int i = 0; i < 1000; i++) {
			testSplitRead(r);
		}
	}

	private void testSplitRead(Random r) {
		int a = r.nextInt(50);
		int b = r.nextInt(50);
		byte[] as = new byte[a];
		byte[] bs = new byte[b];
		r.nextBytes(as);
		r.nextBytes(bs);
		StreamBytes bytes1;
		StreamBytes bytes2;

		bytes1 = Streams.bytes(as);
		bytes2 = Streams.bytes(bs);
		byte[] data = new byte[a + b];
		try (ReadStream s = bytes1.readStream().andThen(StreamCloser.closeStream(), bytes2.readStream())) {
			s.readBytes(data);
		}
		for (int i = 0; i < data.length; i++) {
			if (i < a) {
				assertEquals("index " + i, as[i], data[i]);
			} else {
				assertEquals("index " + i, bs[i - a], data[i]);
			}
		}

		bytes1 = Streams.bytes(as);
		bytes2 = Streams.bytes(bs);
		try (ReadStream s = bytes1.readStream().andThen(StreamCloser.closeStream(), bytes2.readStream())) {
			for (int i = 0; i < data.length; i++) {
				assertEquals("index " + i, data[i], s.readByte());
			}
		}
	}

	public void testSplitWrite() {
		Random r = new Random(0);
		for (int i = 0; i < 1000; i++) {
			testSplitWrite(r);
		}
	}

	private void testSplitWrite(Random r) {
		int a = r.nextInt(50);
		int b = r.nextInt(50);
		byte[] as = new byte[a];
		byte[] bs = new byte[b];
		byte[] data = new byte[a + b];
		r.nextBytes(data);

		StreamBytes bytes1;
		StreamBytes bytes2;
		bytes1 = Streams.bytes(as, 0, a);
		bytes2 = Streams.bytes(bs, 0, b);
		try (WriteStream s = bytes1.writeStream().andThen(StreamCloser.closeStream(), bytes2.writeStream())) {
			s.writeBytes(data);
		}

		for (int i = 0; i < data.length; i++) {
			if (i < a) {
				assertEquals("index " + i, data[i], as[i]);
			} else {
				assertEquals("index " + i, data[i], bs[i - a]);
			}
		}

		Arrays.fill(as, (byte) 0);
		Arrays.fill(bs, (byte) 0);
		bytes1 = Streams.bytes(as, 0, a);
		bytes2 = Streams.bytes(bs, 0, b);
		try (WriteStream s = bytes1.writeStream().andThen(StreamCloser.closeStream(), bytes2.writeStream())) {
			for (int i = 0; i < data.length; i++) {
				s.writeByte(data[i]);
			}
		}
		for (int i = 0; i < data.length; i++) {
			if (i < a) {
				assertEquals("index " + i, data[i], as[i]);
			} else {
				assertEquals("index " + i, data[i], bs[i - a]);
			}
		}

	}

	public void testPosition() {
		{
			ReadStream[] streams = new ReadStream[20];
			for (int i = 0; i < streams.length; i++) {
				streams[i] = Streams.bytes(new byte[2]).readStream();
			}
			ReadStream s = Streams.concatReadStreams(StreamCloser.closeStream(), streams);
			for (int i = 0; i < 20 * 2; i++) {
				assertEquals(i, s.position());
				s.readByte();
			}
		}
		{
			WriteStream[] streams = new WriteStream[20];
			for (int i = 0; i < streams.length; i++) {
				streams[i] = Streams.bytes(2,2).writeStream();
			}
			WriteStream s = Streams.concatWriteStreams(StreamCloser.closeStream(), streams);
			for (int i = 0; i < 20 * 2; i++) {
				assertEquals(i, s.position());
				s.writeByte((byte) i);
			}
		}
	}
}
