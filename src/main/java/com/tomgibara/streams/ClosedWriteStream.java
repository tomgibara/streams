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

import java.nio.ByteBuffer;

class ClosedWriteStream implements WriteStream {

	private final WriteStream wrapped;
	private final WriteStream stream; // optimized by unwrapping for faster data calls
	private final StreamCloser closer;
	private boolean closed = false;

	ClosedWriteStream(WriteStream wrapped, StreamCloser closer) {
		this.wrapped = wrapped;
		this.stream = (wrapped instanceof ClosedWriteStream) ? ((ClosedWriteStream) wrapped).wrapped : wrapped;
		this.closer = closer;
	}

	@Override
	public void close() throws StreamException {
		if (!closed) closed = closer.close(wrapped);
	}

	@Override
	public void writeByte(byte v) throws StreamException {
		checkClosed();
		stream.writeByte(v);
	}

	@Override
	public void writeBytes(byte[] bs) throws StreamException {
		checkClosed();
		stream.writeBytes(bs);
	}

	@Override
	public void writeBytes(byte[] bs, int off, int len) throws StreamException {
		checkClosed();
		stream.writeBytes(bs, off, len);
	}

	@Override
	public void writeInt(int v) throws StreamException {
		checkClosed();
		stream.writeInt(v);
	}

	@Override
	public void writeBoolean(boolean v) throws StreamException {
		checkClosed();
		stream.writeBoolean(v);
	}

	@Override
	public void writeShort(short v) throws StreamException {
		checkClosed();
		stream.writeShort(v);
	}

	@Override
	public void writeLong(long v) throws StreamException {
		checkClosed();
		stream.writeLong(v);
	}

	@Override
	public void writeFloat(float v) throws StreamException {
		checkClosed();
		stream.writeFloat(v);
	}

	@Override
	public void writeDouble(double v) throws StreamException {
		checkClosed();
		stream.writeDouble(v);
	}

	@Override
	public void writeChar(char v) throws StreamException {
		checkClosed();
		stream.writeChar(v);
	}

	@Override
	public void writeChars(char[] cs) throws StreamException {
		checkClosed();
		stream.writeChars(cs);
	}

	@Override
	public void writeChars(char[] cs, int off, int len) throws StreamException {
		checkClosed();
		stream.writeChars(cs, off, len);
	}

	@Override
	public void writeChars(CharSequence cs) throws StreamException {
		checkClosed();
		stream.writeChars(cs);
	}

	@Override
	public StreamBuffering getBuffering() {
		return stream.getBuffering();
	}

	@Override
	public void drainBuffer(ByteBuffer buffer) throws StreamException {
		if (!closed) stream.drainBuffer(buffer);
	}

	private void checkClosed() {
		//TODO should throw a regular exception?
		if (closed) EndOfStreamException.raise();
	}
}
