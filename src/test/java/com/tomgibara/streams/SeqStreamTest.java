package com.tomgibara.streams;

import java.util.Arrays;
import java.util.Random;

import junit.framework.TestCase;

public class SeqStreamTest /*extends FuzzStreamTest*/ extends TestCase {

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

}
