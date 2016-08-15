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

/**
 * Common auto-closable interface for {@link ReadStream} and {@link WriteStream}
 * .
 *
 * @author Tom Gibara
 *
 */

public interface CloseableStream extends AutoCloseable {

	/**
	 * Indicates the preferred buffering strategy for this stream
	 * implementation.
	 *
	 * @return the preferred buffering strategy
	 */

	default StreamBuffering getBuffering() {
		return StreamBuffering.UNSUPPORTED;
	}

	/**
	 * Closes a {@link ReadStream} or {@link WriteStream}. The effect of calling
	 * this method will vary between implementations, though the method may
	 * generally be assumed to be idempotent.
	 *
	 * @throws StreamException
	 *             if an error occurs closing the stream
	 */

	@Override
	public void close() throws StreamException;

}
