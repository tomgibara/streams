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
 * Delegates all method calls to a wrapped {@link WriteStream}.
 *
 * @author Tom Gibara
 *
 */

public class WrappedWriteStream implements WriteStream {

	/**
	 * The stream that is being wrapped.
	 */

	protected final WriteStream wrapped;

	/**
	 * Creates a new stream that wraps an existing {@link WriteStream}.
	 *
	 * @param wrapped
	 *            the {@link WriteStream} to which all calls should be delegated.
	 */

	public WrappedWriteStream(WriteStream wrapped) {
		if (wrapped == null) throw new IllegalArgumentException("null wrapped");
		this.wrapped = wrapped;
	}

	@Override
	public void writeByte(byte v) throws StreamException {
		wrapped.writeByte(v);
	}

	@Override
	public void close() throws StreamException {
		wrapped.close();
	}

	@Override
	public void writeBytes(byte[] bs) throws StreamException {
		wrapped.writeBytes(bs);
	}

	@Override
	public void writeBytes(byte[] bs, int off, int len) throws StreamException {
		wrapped.writeBytes(bs, off, len);
	}

	@Override
	public void writeInt(int v) throws StreamException {
		wrapped.writeInt(v);
	}

	@Override
	public void writeBoolean(boolean v) throws StreamException {
		wrapped.writeBoolean(v);
	}

	@Override
	public void writeShort(short v) throws StreamException {
		wrapped.writeShort(v);
	}

	@Override
	public void writeLong(long v) throws StreamException {
		wrapped.writeLong(v);
	}

	@Override
	public void writeFloat(float v) throws StreamException {
		wrapped.writeFloat(v);
	}

	@Override
	public void writeDouble(double v) throws StreamException {
		wrapped.writeDouble(v);
	}

	@Override
	public void writeChar(char v) throws StreamException {
		wrapped.writeChar(v);
	}

	@Override
	public void writeChars(char[] cs) throws StreamException {
		wrapped.writeChars(cs);
	}

	@Override
	public void writeChars(char[] cs, int off, int len) throws StreamException {
		wrapped.writeChars(cs, off, len);
	}

	@Override
	public void writeChars(CharSequence cs) throws StreamException {
		wrapped.writeChars(cs);
	}
	
	@Override
	public void drainBuffer(ByteBuffer buffer) throws StreamException {
		wrapped.drainBuffer(buffer);
	}

}
