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

final class BufferReadStream implements ReadStream {

	private final ByteBuffer buffer;

	BufferReadStream(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	@Override
	public byte readByte() {
		if (!buffer.hasRemaining()) EndOfStreamException.raise();
		return buffer.get();
	}

	@Override
	public short readShort() {
		if (buffer.remaining() < 2) EndOfStreamException.raise();
		return buffer.getShort();
	}

	@Override
	public int readInt() {
		if (buffer.remaining() < 4) EndOfStreamException.raise();
		return buffer.getInt();
	}

	@Override
	public long readLong() {
		if (buffer.remaining() < 8) EndOfStreamException.raise();
		return buffer.getLong();
	}

	@Override
	public float readFloat() {
		if (buffer.remaining() < 4) EndOfStreamException.raise();
		return buffer.getFloat();
	}

	@Override
	public double readDouble() {
		if (buffer.remaining() < 8) EndOfStreamException.raise();
		return buffer.getDouble();
	}

	@Override
	public char readChar() {
		if (buffer.remaining() < 2) EndOfStreamException.raise();
		return buffer.getChar();
	}

	@Override
	public void readBytes(byte[] bs) {
		readBytes(bs, 0, bs.length);
	}

	@Override
	public void readBytes(byte[] bs, int off, int len) {
		if (bs == null) throw new IllegalArgumentException("null bs");
		if (buffer.remaining() < len) EndOfStreamException.raise();
		buffer.get(bs, off, len);
	}

	@Override
	public void fillBuffer(ByteBuffer buffer) {
		if (buffer == null) throw new IllegalArgumentException("null buffer");
		final int srcR = this.buffer.remaining();
		final int dstR = buffer.remaining();
		if (dstR >= srcR) {
			buffer.put(this.buffer);
			return;
		}
		int oldLimit = this.buffer.limit();
		this.buffer.limit(oldLimit + dstR - srcR);
		buffer.put(this.buffer);
		this.buffer.limit(oldLimit);
	}

	@Override
	public StreamBuffering getBuffering() {
		return buffer.isDirect() ? StreamBuffering.PREFER_DIRECT : StreamBuffering.PREFER_INDIRECT;
	}

}