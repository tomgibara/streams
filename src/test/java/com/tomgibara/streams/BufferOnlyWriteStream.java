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

public class BufferOnlyWriteStream implements WriteStream {

	private long remaining;

	public BufferOnlyWriteStream(long capacity) {
		if (capacity < 0L) throw new IllegalArgumentException("negative capacity");
		remaining = capacity;
	}

	@Override
	public void writeByte(byte v) throws StreamException {
		throw new IllegalStateException("buffer only");
	}

	@Override
	public void writeBytes(byte[] bs, int off, int len) throws StreamException {
		if (len > remaining) throw EndOfStreamException.instance();
		remaining -= len;
	}

	@Override
	public void drainBuffer(ByteBuffer buffer) throws StreamException {
		if (buffer.hasArray()) {
			writeBytes(buffer.array(), buffer.arrayOffset(), (int) Math.min(remaining, buffer.remaining()));
		} else {
			while (remaining > 0L && buffer.hasRemaining()) {
				buffer.get();
				remaining --;
			}
		}
	}

}
