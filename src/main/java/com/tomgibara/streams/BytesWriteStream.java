/*
 * Copyright 2010 Tom Gibara
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
import java.util.Arrays;

/**
 * Writes values to a growable byte array. Closing this writer results in all
 * subsequent writes throwing an {@link EndOfStreamException}.
 *
 * @author Tom Gibara
 *
 */

final class BytesWriteStream implements WriteStream {

	private static final int MIN_CAPACITY_INCR = 32;
	private static final int MAX_CAPACITY_INCR = 1024 * 1024;

	private byte[] bytes;
	private final int maxCapacity;
	private int position;

	BytesWriteStream(byte[] bytes, int maxCapacity) {
		// invariant: maxCapacity >= bytes.length
		this.bytes = bytes;
		this.maxCapacity = maxCapacity;
		position = 0;
	}

	@Override
	public void writeByte(byte v) {
		ensureFurtherCapacity(1);
		bytes[position++] = v;
	}

	@Override
	public void writeBytes(byte[] vs) {
		final int length = vs.length;
		ensureFurtherCapacity(length);
		System.arraycopy(vs, 0, bytes, position, length);
		position += length;
	}

	@Override
	public void writeBytes(byte[] vs, int off, int len) {
		ensureFurtherCapacity(len);
		System.arraycopy(vs, off, bytes, position, len);
		position += len;
	}

	@Override
	public void writeBoolean(boolean v) {
		ensureFurtherCapacity(1);
		bytes[position++] = (byte) (v ? -1 : 0);
	}

	@Override
	public void writeInt(int v) {
		ensureFurtherCapacity(4);
		bytes[position++] = (byte) (v >> 24);
		bytes[position++] = (byte) (v >> 16);
		bytes[position++] = (byte) (v >>  8);
		bytes[position++] = (byte) (v      );
	}

	@Override
	public void writeChar(char v) {
		ensureFurtherCapacity(2);
		bytes[position++] = (byte) (v >>  8);
		bytes[position++] = (byte) (v      );
	}

	@Override
	public void writeChars(char[] vs, int off, int len) {
		ensureFurtherCapacity(len * 2);
		final int lim = off + len;
		for (int i = off; i < lim; i++) {
			final char v = vs[i];
			bytes[position++] = (byte) (v >>  8);
			bytes[position++] = (byte) (v      );
		}
	}

	@Override
	public void writeShort(short v) {
		ensureFurtherCapacity(2);
		bytes[position++] = (byte) (v >>  8);
		bytes[position++] = (byte) (v      );
	}

	@Override
	public void writeLong(long v) {
		ensureFurtherCapacity(8);
		bytes[position++] = (byte) (v >> 56);
		bytes[position++] = (byte) (v >> 48);
		bytes[position++] = (byte) (v >> 40);
		bytes[position++] = (byte) (v >> 32);
		bytes[position++] = (byte) (v >> 24);
		bytes[position++] = (byte) (v >> 16);
		bytes[position++] = (byte) (v >>  8);
		bytes[position++] = (byte) (v      );
	}

	@Override
	public void drainBuffer(ByteBuffer buffer) throws StreamException {
		int length = attemptFurtherCapacity( buffer.remaining() );
		buffer.get(bytes, position, length);
		position += length;
	}

	/**
	 * Closes the writer. All subsequent attempts to write to the writer will
	 * fail with an {@link EndOfStreamException} as per the {@link #close()}
	 * method.
	 */

	@Override
	public void close() {
		position = -1 - position;
	}

	@Override
	public StreamBuffering getBuffering() {
		return StreamBuffering.PREFER_ANY;
	}

	/**
	 * <p>
	 * The byte data recorded by the stream. Supplying false returns a copy of
	 * the internal data store of the writer. Supplying true closes the writer
	 * and returns the byte array which backed the writer. Subsequent attempts
	 * to write to the writer will fail with an {@link EndOfStreamException} as
	 * per the {@link #close()} method.
	 *
	 * <p>
	 * Note that the length of an uncopied byte array may exceed the number of
	 * bytes written, for this reason direct retrieval of the byte storage is
	 * best reserved for situations where the initial capacity specified and not
	 * exceeded.
	 *
	 * @param direct
	 *            whether the bytes returned should be a copy of the bytes
	 *            accumulated by this writer
	 * @return the byte data streamed
	 */

	byte[] getBytes(boolean direct) {
		if (!direct) return Arrays.copyOf(bytes, position < 0 ? -1 - position : position);
		if (!isClosed()) close();
		return bytes;
	}

	int position() {
		return position < 0 ? -1 - position : position;
	}

	private boolean isClosed() {
		return position < 0;
	}

	private void ensureFurtherCapacity(int n) {
		if (isClosed()) StreamException.raiseClosed();
		int required = position + n;
		// checks overflow
		if (required < 0) EndOfStreamException.raise();
		if (required > bytes.length) {
			if (required > maxCapacity) EndOfStreamException.raise();
			int c = bytes.length;
			c += c < MIN_CAPACITY_INCR ? MIN_CAPACITY_INCR : c;
			if (c - bytes.length > MAX_CAPACITY_INCR) c = bytes.length + MAX_CAPACITY_INCR;
			if (c < required) c = required;
			if (c > maxCapacity) c = maxCapacity;
			bytes = Arrays.copyOf(bytes, c);
		}
	}

	private int attemptFurtherCapacity(int n) {
		if (isClosed()) StreamException.raiseClosed();
		int required = position + n;
		// checks overflow
		if (required < 0) required = Integer.MAX_VALUE;
		if (required > maxCapacity) required = maxCapacity;
		if (required > bytes.length) {
			int c = bytes.length;
			c += c < MIN_CAPACITY_INCR ? MIN_CAPACITY_INCR : c;
			if (c - bytes.length > MAX_CAPACITY_INCR) c = bytes.length + MAX_CAPACITY_INCR;
			if (c < required) c = required;
			if (c > maxCapacity) c = maxCapacity;
			bytes = Arrays.copyOf(bytes, c);
		}
		return required - position;
	}
}
