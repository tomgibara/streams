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

final class BufferWriteStream implements WriteStream {

	private final ByteBuffer buffer;

	BufferWriteStream(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	@Override
	public void writeByte(byte v) {
		if (!buffer.hasRemaining()) throw EndOfStreamException.instance();
		buffer.put(v);
	}

	@Override
	public void writeShort(short v) {
		if (buffer.remaining() < 2) throw EndOfStreamException.instance();
		buffer.putShort(v);
	}

	@Override
	public void writeInt(int v) {
		if (buffer.remaining() < 4) throw EndOfStreamException.instance();
		buffer.putInt(v);
	}

	@Override
	public void writeLong(long v) {
		if (buffer.remaining() < 8) throw EndOfStreamException.instance();
		buffer.putLong(v);
	}

	@Override
	public void writeFloat(float v) throws StreamException {
		if (buffer.remaining() < 4) throw EndOfStreamException.instance();
		buffer.putFloat(v);
	}

	@Override
	public void writeDouble(double v) throws StreamException {
		if (buffer.remaining() < 8) throw EndOfStreamException.instance();
		buffer.putDouble(v);
	}

	@Override
	public void writeChar(char v) throws StreamException {
		if (buffer.remaining() < 2) throw EndOfStreamException.instance();
		buffer.putChar(v);
	}

	@Override
	public void writeBytes(byte[] bs) {
		writeBytes(bs, 0, bs.length);
	}

	@Override
	public void writeBytes(byte[] bs, int off, int len) {
		if (bs == null) throw new IllegalArgumentException("null bs");
		if (buffer.remaining() < len) throw EndOfStreamException.instance();
		buffer.put(bs, off, len);
	}

	@Override
	public void drainBuffer(ByteBuffer buffer) throws StreamException {
		if (buffer == null) throw new IllegalArgumentException("null buffer");
		if (buffer.remaining() > this.buffer.remaining()) throw EndOfStreamException.instance();
		this.buffer.put(buffer);
	}

	@Override
	public StreamBuffering getBuffering() {
		return buffer.isDirect() ? StreamBuffering.PREFER_DIRECT : StreamBuffering.PREFER_INDIRECT;
	}

}
