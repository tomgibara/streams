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

class ClosedReadStream implements ReadStream {

	private final ReadStream wrapped;
	private final ReadStream stream; // optimized by unwrapping for faster data calls
	private final StreamCloser closer;
	private boolean closed = false;

	ClosedReadStream(ReadStream wrapped, StreamCloser closer) {
		this.wrapped = wrapped;
		this.stream = (wrapped instanceof ClosedReadStream) ? ((ClosedReadStream) wrapped).wrapped : wrapped;
		this.closer = closer;
	}

	@Override
	public void close() throws StreamException {
		if (!closed) closed = closer.close(wrapped);
	}

	@Override
	public byte readByte() throws StreamException {
		checkClosed();
		return stream.readByte();
	}

	@Override
	public void readBytes(byte[] bs) throws StreamException {
		checkClosed();
		stream.readBytes(bs);
	}

	@Override
	public void readBytes(byte[] bs, int off, int len) throws StreamException {
		checkClosed();
		stream.readBytes(bs, off, len);
	}

	@Override
	public int readInt() throws StreamException {
		checkClosed();
		return stream.readInt();
	}

	@Override
	public boolean readBoolean() throws StreamException {
		checkClosed();
		return stream.readBoolean();
	}

	@Override
	public short readShort() throws StreamException {
		checkClosed();
		return stream.readShort();
	}

	@Override
	public long readLong() throws StreamException {
		checkClosed();
		return stream.readLong();
	}

	@Override
	public float readFloat() throws StreamException {
		checkClosed();
		return stream.readFloat();
	}

	@Override
	public double readDouble() throws StreamException {
		checkClosed();
		return stream.readDouble();
	}

	@Override
	public char readChar() throws StreamException {
		checkClosed();
		return stream.readChar();
	}

	@Override
	public void readChars(char[] cs) throws StreamException {
		checkClosed();
		stream.readChars(cs);
	}

	@Override
	public void readChars(char[] cs, int off, int len) throws StreamException {
		checkClosed();
		stream.readChars(cs, off, len);
	}

	@Override
	public String readChars() throws StreamException {
		checkClosed();
		return stream.readChars();
	}

	@Override
	public StreamBuffering getBuffering() {
		return stream.getBuffering();
	}
	
	@Override
	public void fillBuffer(ByteBuffer buffer) throws StreamException {
		if (!closed) stream.fillBuffer(buffer);
	}

	private void checkClosed() {
		if (closed) StreamException.raiseClosed();
	}
}
