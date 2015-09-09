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

import java.util.Arrays;

/**
 * Writes values to a growable byte array. Closing this writer results in all
 * subsequent writes throwing an {@link EndOfStreamException}.
 *
 * @author Tom Gibara
 *
 */

public final class ByteWriteStream extends AbstractWriteStream {

	private static final int DEFAULT_CAPACITY = 32;

	private static final int MAX_CAPACITY_INCR = 1024 * 1024;

	private static final int CLOSED_POSITION = -1;
	
	private int position;
	private byte[] bytes;

	/**
	 * Creates a new stream with a default initial capacity.
	 */

	public ByteWriteStream() {
		this(DEFAULT_CAPACITY);
	}

	/**
	 * Creates a new stream with the specified initial capacity. No new storage
	 * will be allocated unless this initial capacity is exceeded.
	 *
	 * @param initialCapacity
	 *            the initial capacity in bytes.
	 */

	public ByteWriteStream(int initialCapacity) {
		position = 0;
		bytes = new byte[initialCapacity];
	}

	/**
	 * The byte data recorded by the stream. The returned array is a copy of the
	 * internal data store and may thus be subsequently mutated by the caller.
	 *
	 * @return the byte data streamed
	 * @deprecated use {@link #getBytes(boolean)} instead
	 */

	@Deprecated
	public byte[] getBytes() {
		return getBytes(true);
	}

	/**
	 * <p>
	 * The byte data recorded by the stream. Supplying true returns a copy of
	 * the internal data store of the writer. Supplying closes the writer and
	 * returns the byte array which backed the writer. Subsequent attempts to
	 * write to the writer will fail with an {@link EndOfStreamException} as per
	 * the {@link #close()} method.
	 * 
	 * <p>
	 * Note that the length of an uncopied byte array may exceed the number of
	 * bytes written, for this reason direct retrieval of the byte storage is
	 * best reserved for situations where the initial capacity specified and not
	 * exceeded.
	 *
	 * @param copy
	 *            whether the bytes returned should be a copy of the bytes
	 *            accumulated by this writer
	 * @return the byte data streamed
	 */

	public byte[] getBytes(boolean copy) {
		if (copy) return Arrays.copyOf(bytes, position);
		if (!isClosed()) close();
		return bytes;
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

	/**
	 * Closes the writer. All subsequent attempts to write to the writer will
	 * fail with an {@link EndOfStreamException} as per the {@link #close()}
	 * method.
	 */
	
	@Override
	public void close() {
		position = CLOSED_POSITION;
	}

	private boolean isClosed() {
		return position == CLOSED_POSITION;
	}
	
	private void ensureFurtherCapacity(int n) {
		if (isClosed()) throw EndOfStreamException.EOS;
		int required = position + n;
		if (required > bytes.length) {
			int c = bytes.length;
			c += c < DEFAULT_CAPACITY ? DEFAULT_CAPACITY : c;
			if (c - bytes.length > MAX_CAPACITY_INCR) c = bytes.length + MAX_CAPACITY_INCR;
			if (c < required) c = required;
			bytes = Arrays.copyOf(bytes, c);
		}

	}


}
