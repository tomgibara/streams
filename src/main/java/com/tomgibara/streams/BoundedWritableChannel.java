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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

class BoundedWritableChannel implements WritableByteChannel {

	private final WritableByteChannel channel;
	private long length;

	BoundedWritableChannel(WritableByteChannel channel, long length) {
		if (channel == null) throw new IllegalArgumentException("null channel");
		if (length < 0L) throw new IllegalArgumentException("negative length");
		this.channel = channel;
		this.length = length;
	}

	@Override
	public boolean isOpen() {
		return channel.isOpen();
	}

	@Override
	public void close() throws IOException {
		channel.close();
	}

	@Override
	public int write(ByteBuffer src) throws IOException {
		if (length == 0) return -1;
		int remaining = src.remaining();
		// happy case - we can read as much as we want
		if (remaining < length) {
			int count = channel.write(src);
			if (count != -1) length -= count;
			return count;
		}
		// other case - we need to artificially limit the buffer
		int oldLimit = src.limit();
		src.limit(src.position() + (int) length);
		try {
			int count = channel.write(src);
			if (count != -1) length -= count;
			return count;
		} finally {
			src.limit(oldLimit);
		}
	}

}
