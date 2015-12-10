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
import java.nio.channels.ReadableByteChannel;

class BoundedReadableChannel implements ReadableByteChannel {

	private final ReadableByteChannel channel;
	private long length;

	BoundedReadableChannel(ReadableByteChannel channel, long length) {
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
	public int read(ByteBuffer dst) throws IOException {
		if (length == 0) return -1;
		int remaining = dst.remaining();
		// happy case - we can read as much as we want
		if (remaining < length) {
			int count = channel.read(dst);
			if (count != -1) length -= count;
			return count;
		}
		// other case - we need to artificially limit the buffer
		int oldLimit = dst.limit();
		dst.limit(dst.position() + (int) length);
		try {
			int count = channel.read(dst);
			if (count != -1) length -= count;
			return count;
		} finally {
			dst.limit(oldLimit);
		}
	}

}
