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

class BoundedInputStream extends InputStream {

	private final InputStream stream;
	private long remaining;

	BoundedInputStream(InputStream stream, long length) {
		if (stream == null) throw new IllegalArgumentException("null stream");
		if (length < 0L) throw new IllegalArgumentException("negative length");
		this.stream = stream;
		this.remaining = length;
	}

	@Override
	public int read() throws IOException {
		if (remaining <= 0L) return -1;
		int value = stream.read();
		remaining--;
		return value;
	}

	@Override
	public int read(byte[] b) throws IOException {
		int read;
		if (b.length <= remaining) {
			read = stream.read(b);
		} else {
			read = stream.read(b, 0, (int) remaining);
		}
		remaining -= read;
		return read;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int read;
		if (len <= remaining) {
			read = stream.read(b, off, len);
		} else {
			read = stream.read(b, off, (int) remaining);
		}
		remaining -= read;
		return read;
	}

	@Override
	public long skip(long n) throws IOException {
		return stream.skip(Math.min(n, remaining));
	}

	@Override
	public int available() throws IOException {
		int a = stream.available();
		if (remaining < a) return (int) remaining;
		return a;
	}

	@Override
	public boolean markSupported() {
		return stream.markSupported();
	}

	@Override
	public synchronized void mark(int readlimit) {
		if (remaining <= readlimit) {
			stream.mark(readlimit);
		} else {
			stream.mark((int) remaining);
		}
	}

	@Override
	public synchronized void reset() throws IOException {
		stream.reset();
	}

	@Override
	public void close() throws IOException {
		stream.close();
	}
}
