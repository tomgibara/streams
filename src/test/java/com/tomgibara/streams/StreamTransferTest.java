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
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import com.tomgibara.fundament.Producer;

public class StreamTransferTest {

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

			BytesReadStream br = new BytesReadStream(srcBytes);
			BytesWriteStream bw = new BytesWriteStream(new byte[32], Integer.MAX_VALUE);
			test(br, () -> srcBytes, bw, () -> bw.getBytes(false), length, buffer);

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

	@Test
	public void testLimitedWrite() {
		Random r = new Random(0L);
		for (int i = 0; i < 100; i++) {
			byte[] srcBytes = new byte[r.nextInt(MAX_LEN)];
			byte[] dstBytes = new byte[r.nextInt(MAX_LEN)];
			int length = r.nextBoolean() ? r.nextInt(MAX_LEN) : -1;

			for (int j = 0; j < 10; j++) {
				int size = j * MAX_LEN / 10;
				ReadStream src = new BytesReadStream(srcBytes);
				ByteArrayChannel channel = new ByteArrayChannel(dstBytes);
				WriteStream dst = new ChannelWriteStream(channel);
				StreamTransfer transfer = src.to(dst, size);
				long transferred = length < 0 ? transfer.transferFully() : transfer.transfer(length);
				assertEquals(channel.position(), transferred);
				assertArrayEquals(Arrays.copyOf(srcBytes, (int) transferred), Arrays.copyOf(dstBytes, (int) transferred));
			}
		}
	}

	private void test(ReadStream in, Producer<byte[]> inToBytes, WriteStream out, Producer<byte[]> outToBytes, long length, ByteBuffer buffer) {
		// do the transfer
		StreamTransfer transfer = in.to(out, buffer);
		long transferred = length < 0 ? transfer.transferFully() : transfer.transfer(length);
		// check the result
		byte[] inBytes = inToBytes.produce();
		byte[] outBytes = outToBytes.produce();
		assertEquals(outBytes.length, transferred);
		if (length < 0) {
			assertArrayEquals(inBytes, outBytes);
		} else {
			int expLen = (int) Math.min(inBytes.length, length);
			assertEquals(expLen, outBytes.length);
			assertArrayEquals(Arrays.copyOf(inBytes, expLen), outBytes);
		}
	}

}
