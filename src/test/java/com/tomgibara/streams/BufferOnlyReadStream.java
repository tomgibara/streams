/*
 * Copyright 2018 Tom Gibara
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
import java.util.Arrays;

public class BufferOnlyReadStream implements ReadStream {

	private long remaining;

	public BufferOnlyReadStream(long size) {
		if (size < 0L) throw new IllegalArgumentException("negative size");
		remaining = size;
	}

	@Override
	public byte readByte() throws StreamException {
		throw new IllegalStateException("buffer only");
	}

	@Override
	public void readBytes(byte[] bs, int off, int len) {
		if (len > remaining) EndOfStreamException.raise();
		Arrays.fill(bs, off, off + len, (byte) 0);
		remaining -= len;
	}

	@Override
	public void fillBuffer(ByteBuffer buffer) throws StreamException {
		if (buffer.hasArray()) {
			int len = (int) Math.min(remaining, buffer.remaining());
			readBytes(buffer.array(), buffer.arrayOffset(), len);
			buffer.position(buffer.position() + len);
		} else {
			while (remaining > 0L && buffer.hasRemaining()) {
				buffer.put((byte) 0);
				remaining --;
			}
		}
	}

}
