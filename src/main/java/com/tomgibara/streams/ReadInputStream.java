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
import java.io.InputStream;
import java.nio.ByteBuffer;

class ReadInputStream extends InputStream {

	private final ReadStream stream;

	ReadInputStream(ReadStream stream) {
		this.stream = stream;
	}

	@Override
	public int read() throws IOException {
		try {
			return stream.readByte() & 0xff;
		} catch (EndOfStreamException e) {
			return -1;
		} catch (StreamException e) {
			throw new IOException(e);
		}
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(ByteBuffer.wrap(b));
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return read(ByteBuffer.wrap(b, off, len));
	}

	@Override
	public void close() throws IOException {
		try {
			stream.close();
		} catch (StreamException e) {
			throw new IOException(e);
		}
	}

	private int read(ByteBuffer buffer) throws IOException {
		try {
			stream.fillBuffer(buffer);
		} catch (StreamException e) {
			throw new IOException(e);
		}
		return buffer.position();
	}
}
