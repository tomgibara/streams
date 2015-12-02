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
 * Reads values from a byte array. When the bytes in the array (or array
 * segment) are exhausted calling any read method on this class will result in
 * an {@link EndOfStreamException} being thrown. Closing this stream has no
 * effect.
 *
 * @author Tom Gibara
 *
 * @see EndOfStreamException#EOS
 */

public final class ByteReadStream implements ReadStream {

	private final byte[] bytes;
	private final int limit;
	private int position;

	/**
	 * Creates a stream that reads values from the supplied byte data.
	 * The supplied byte array is not modified.
	 *
	 * @param bytes the data to be streamed
	 */

	public ByteReadStream(byte[] bytes) {
		if (bytes == null) throw new IllegalArgumentException("null bytes");
		this.bytes = bytes;
		this.limit = bytes.length;
		this.position = 0;
	}

	/**
	 * Creates a stream that reads values from the specified byte array segment.
	 * The supplied byte array is not modified.
	 *
	 * @param bytes
	 *            contains the bytes to be streamed
	 * @param off
	 *            the index of the first byte to be streamed
	 * @param len
	 *            the number of bytes that should be streamed.
	 */

	public ByteReadStream(byte[] bytes, int off, int len) {
		if (bytes == null) throw new IllegalArgumentException("null bytes");
		if (off < 0) throw new IllegalArgumentException("negative off");
		int length = bytes.length;
		if (off > length) throw new IllegalArgumentException("off exceeds length");
		if (len < 0) throw new IllegalArgumentException("negative len");
		int limit = off + len;
		if (limit > length) throw new IllegalArgumentException("off + len exceeds length");
		this.bytes = bytes;
		this.position = off;
		this.limit = limit;
	}

	public int position() {
		return position;
	}

	@Override
	public byte readByte() {
		requireBytes(1);
		return bytes[position++];
	}

	@Override
	public void readBytes(byte[] bs) {
		int len = bs.length;
		requireBytes(len);
		System.arraycopy(bytes, position, bs, 0, len);
		position += len;
	}

	@Override
	public void readBytes(byte[] bs, int off, int len) {
		requireBytes(len);
		System.arraycopy(bytes, position, bs, off, len);
		position += len;
	}

	@Override
	public char readChar() {
		requireBytes(2);
		byte b0 = bytes[position++];
		byte b1 = bytes[position++];
		return (char) (b0 << 8 | b1 & 0xff);
	}

	@Override
	public short readShort() {
		requireBytes(2);
		byte b0 = bytes[position++];
		byte b1 = bytes[position++];
		return (short) (b0 << 8 | b1 & 0xff);
	}

	@Override
	public int readInt() {
		requireBytes(4);
		byte b0 = bytes[position++];
		byte b1 = bytes[position++];
		byte b2 = bytes[position++];
		byte b3 = bytes[position++];
		return
				 b0         << 24 |
				(b1 & 0xff) << 16 |
				(b2 & 0xff) <<  8 |
				(b3 & 0xff);
	}

	@Override
	public long readLong() {
		requireBytes(8);
		byte b0 = bytes[position++];
		byte b1 = bytes[position++];
		byte b2 = bytes[position++];
		byte b3 = bytes[position++];
		byte b4 = bytes[position++];
		byte b5 = bytes[position++];
		byte b6 = bytes[position++];
		byte b7 = bytes[position++];
		return
				 (long) b0          << 56 |
				(       b1 & 0xffL) << 48 |
				(       b2 & 0xffL) << 40 |
				(       b3 & 0xffL) << 32 |
				(       b4 & 0xffL) << 24 |
				       (b5 & 0xff ) << 16 |
				       (b6 & 0xff ) <<  8 |
				        b7 & 0xff         ;
	}

	@Override
	public void readChars(char[] cs, int off, int len) {
		requireBytes(len * 2);
		for (int i = 0; i < len; i++) {
			byte b0 = bytes[position++];
			byte b1 = bytes[position++];
			cs[off + i] = (char) (b0 << 8 | b1 & 0xff);
		}
	}

	@Override
	public StreamBuffering getBuffering() {
		return StreamBuffering.PREFER_ANY;
	}
	
	@Override
	public void fillBuffer(ByteBuffer buffer) throws StreamException {
		int length = Math.min(buffer.remaining(), limit - position);
		buffer.put(bytes, position, length);
		position += length;
	}
	
	private void requireBytes(int count) {
		if (position + count > limit) throw EndOfStreamException.EOS;
	}

}
