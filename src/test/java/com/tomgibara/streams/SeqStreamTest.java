package com.tomgibara.streams;

import java.util.Random;

import junit.framework.TestCase;

public class SeqStreamTest /*extends FuzzStreamTest*/ extends TestCase {

	/*
	@Override
	WriteStream newWriter() {
		return null;
	}

	@Override
	ReadStream newReader(WriteStream writer) {
		return null;
	}
	*/

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
}
