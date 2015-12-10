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
import java.io.OutputStream;

class BoundedOutputStream extends OutputStream {

	private final OutputStream stream;
	private long remaining;

	BoundedOutputStream(OutputStream stream, long length) {
		if (stream == null) throw new IllegalArgumentException("null stream");
		if (length < 0L) throw new IllegalArgumentException("negative length");
		this.stream = stream;
		this.remaining = length;
	}

	@Override
	public void write(int b) throws IOException {
		if (remaining <= 0L) throw new IOException("EOS");
		stream.write(b);
		remaining --;
	}

	@Override
	public void write(byte[] b) throws IOException {
		if (b.length <= remaining) {
			stream.write(b);
			remaining -= b.length;
		} else {
			stream.write(b, 0, (int) remaining);
			remaining = 0L;
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (len <= remaining) {
			stream.write(b);
			remaining -= len;
		} else {
			stream.write(b, 0, (int) remaining);
			remaining = 0L;
		}
	}

	@Override
	public void flush() throws IOException {
		stream.flush();
	}

	@Override
	public void close() throws IOException {
		stream.close();
	}

}
