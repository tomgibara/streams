package com.tomgibara.streams;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.tomgibara.fundament.Producer;

public class StreamsTest {

	private final static int MAX_LEN = 3000;
	
	@Test
	public void testTransfer() {
		Random r = new Random(0L);
		for (int i = 0; i < 100; i++) {
			byte[] srcBytes = new byte[r.nextInt(MAX_LEN)];
			int length = r.nextBoolean() ? r.nextInt(MAX_LEN) : -1;
			ByteBuffer buffer;
			if (r.nextBoolean()) {
				int size = r.nextInt(MAX_LEN / 5);
				buffer = r.nextBoolean() ? ByteBuffer.allocateDirect(size) : ByteBuffer.allocate(size);
			} else {
				buffer = null;
			}
				
			ByteReadStream br = new ByteReadStream(srcBytes);
			ByteWriteStream bw = new ByteWriteStream();
			test(br, () -> srcBytes, bw, () -> bw.getBytes(true), length, buffer);
			
			InputReadStream sr = new InputReadStream(new ByteArrayInputStream(srcBytes));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			OutputWriteStream sw = new OutputWriteStream(baos);
			test(sr, () -> srcBytes, sw, () -> baos.toByteArray(), length, buffer);
			
			ChannelReadStream cr = new ChannelReadStream(new ByteArrayChannel(srcBytes));
			ByteArrayChannel bac = new ByteArrayChannel(length < 0 ? srcBytes.length : Math.min(length, srcBytes.length));
			ChannelWriteStream cw = new ChannelWriteStream(bac);
			test(cr, () -> srcBytes, cw, () -> bac.getBytes(), length, buffer);
		}
	}

	
	private void test(ReadStream in, Producer<byte[]> inToBytes, WriteStream out, Producer<byte[]> outToBytes, long length, ByteBuffer buffer) {
		// do the transfer
		if (length < 0) {
			if (buffer == null) {
				Streams.transfer(in, out);
			} else {
				Streams.transfer(in, out, buffer);
			}
		} else {
			if (buffer == null) {
				Streams.transfer(in, out, length);
			} else {
				Streams.transfer(in, out, length, buffer);
			}
		}
		// check the result
		byte[] inBytes = inToBytes.produce();
		byte[] outBytes = outToBytes.produce();
		if (length < 0) {
			assertArrayEquals(inBytes, outBytes);
		} else {
			int expLen = (int) Math.min(inBytes.length, length);
			assertEquals(expLen, outBytes.length);
			assertArrayEquals(Arrays.copyOf(inBytes, expLen), outBytes);
		}
	}
	
}
