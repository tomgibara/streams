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

/**
 * <p>
 * Instances of this class expose a <code>ByteBuffer</code> as a
 * {@link ReadStream} and a {@link WriteStream}. Both streams are subject to the
 * buffers limit and each stream read/writes bytes at the streams current
 * position before advancing it. Either stream is exhausted when the buffer has
 * no remaining elements.
 *
 * <p>
 * Importantly direct operations on the buffer can be interleaved with stream
 * operations so that, for example, a buffer can be filled via the
 * {@link #writeStream()} before being flipped using {@link #buffer()} and then
 * drained with {@link #readStream()}.
 *
 * @author Tom Gibara
 * @see Streams#streamBuffer(ByteBuffer)
 */

public final class StreamBuffer {

	private final ByteBuffer buffer;

	StreamBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	/**
	 * The buffer backing the streams. Any operations on the buffer are
	 * immediately reflected in any of the streams returned by this objec. The
	 * buffer guaranteed not to be read-only.
	 *
	 * @return the backing buffer.
	 */

	public ByteBuffer buffer() {
		return buffer;
	}

	/**
	 * Creates a new read stream over the backing buffer. The stream will return
	 * data at the buffer position up to the buffer limit.
	 *
	 * @return a stream for reading the contents of the buffer
	 * @see #buffer()
	 */

	public ReadStream readStream() {
		return new BufferReadStream(buffer);
	}

	/**
	 * Creates a new write stream over the backing buffer. The stream will
	 * populate data at the buffer position up to the buffer limit.
	 *
	 * @return a stream for writing the contents of the buffer
	 * @see #buffer()
	 */

	public WriteStream writeStream() {
		return new BufferWriteStream(buffer);
	}
}
