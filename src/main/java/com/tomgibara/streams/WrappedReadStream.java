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

/**
 * Delegates all method calls to a wrapped {@link ReadStream}.
 *
 * @author Tom Gibara
 *
 * @param <S>
 *            the type of {@link ReadStream} being wrapped
 */

public class WrappedReadStream<S extends ReadStream> implements ReadStream {

	/**
	 * The stream that is being wrapped.
	 */

	protected final S wrapped;

	/**
	 * Creates a new stream that wraps an existing {@link ReadStream}.
	 *
	 * @param wrapped
	 *            the {@link ReadStream} to which all calls should be delegated.
	 */

	public WrappedReadStream(S wrapped) {
		if (wrapped == null) throw new IllegalArgumentException("null wrapped");
		this.wrapped = wrapped;
	}

	@Override
	public void close() throws StreamException {
		wrapped.close();
	}

	@Override
	public byte readByte() throws StreamException {
		return wrapped.readByte();
	}

	@Override
	public void readBytes(byte[] bs) throws StreamException {
		wrapped.readBytes(bs);
	}

	@Override
	public void readBytes(byte[] bs, int off, int len) throws StreamException {
		wrapped.readBytes(bs, off, len);
	}

	@Override
	public int readInt() throws StreamException {
		return wrapped.readInt();
	}

	@Override
	public boolean readBoolean() throws StreamException {
		return wrapped.readBoolean();
	}

	@Override
	public short readShort() throws StreamException {
		return wrapped.readShort();
	}

	@Override
	public long readLong() throws StreamException {
		return wrapped.readLong();
	}

	@Override
	public float readFloat() throws StreamException {
		return wrapped.readFloat();
	}

	@Override
	public double readDouble() throws StreamException {
		return wrapped.readDouble();
	}

	@Override
	public char readChar() throws StreamException {
		return wrapped.readChar();
	}

	@Override
	public void readChars(char[] cs) throws StreamException {
		wrapped.readChars(cs);
	}

	@Override
	public void readChars(char[] cs, int off, int len) throws StreamException {
		wrapped.readChars(cs, off, len);
	}

	@Override
	public String readChars() throws StreamException {
		return wrapped.readChars();
	}

	@Override
	public StreamBuffering getBuffering() {
		return wrapped.getBuffering();
	}
	
	@Override
	public void fillBuffer(ByteBuffer buffer) throws StreamException {
		wrapped.fillBuffer(buffer);
	}
}
