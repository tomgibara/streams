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

public interface PositionalStream {

	/**
	 * An optional method that reports the position within the stream. The value
	 * returned is dependent on the nature of the stream, but when supported, it
	 * is generally equal to the number of bytes read-from/written-to the
	 * stream. Not all streams support this method. If the method is not
	 * supported, a negative value is returned. The position is not guaranteed to be available
	 * after a stream has been closed.
	 *
	 * @return an integer indicating the position within the stream, or a
	 *         negative value
	 * @throws StreamException
	 *             if an error occurs on the stream as a consequence of
	 *             computing the position
	 */

	default long position() throws StreamException {
		return -1L;
	}

}
