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

final class SingleReadStream implements ReadStream {

	private final byte value;
	private boolean read = false;

	SingleReadStream(byte value) {
		this.value = value;
	}

	@Override
	public byte readByte() {
		if (read) throw EndOfStreamException.instance();
		read = true;
		return value;
	}

	@Override
	public void readBytes(byte[] bs) {
		if (bs == null) throw new IllegalArgumentException("null bs");
		if (bs.length == 0) return;
		if (read || bs.length != 1) throw EndOfStreamException.instance();
		bs[0] = value;
		read = true;
	}

	@Override
	public void readBytes(byte[] bs, int off, int len) {
		if (bs == null) throw new IllegalArgumentException("null bs");
		if (off < 0) throw new IllegalArgumentException("negative off");
		if (len < 0) throw new IllegalArgumentException("negative len");
		if (off + len > bs.length) throw new IllegalArgumentException("off + len exceeds length");
		if (len == 0) return;
		if (read || len != 1) throw EndOfStreamException.instance();
		bs[off] = value;
		read = true;
	}

	@Override
	public void fillBuffer(ByteBuffer buffer) {
		if (buffer == null) throw new IllegalArgumentException("null buffer");
		if (read || !buffer.hasRemaining()) return;
		buffer.put(value);
		read = false;
	}

	@Override
	public void skip(long length) throws StreamException {
		if (length < 0L) throw new IllegalArgumentException("negative length");
		if (length > 0L && !read) {
			read = true;
			length --;
		}
		if (length > 0L) throw EndOfStreamException.instance();
	}

	@Override
	public ReadStream bounded(long length) {
		if (length < 0L) throw new IllegalArgumentException("negative length");
		return read || length == 0L ? EmptyReadStream.INSTANCE : this;
	}

	@Override
	public StreamBuffering getBuffering() {
		return StreamBuffering.UNSUPPORTED;
	}

}
