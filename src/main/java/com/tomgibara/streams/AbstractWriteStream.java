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

/**
 * <p>
 * A convenient base implementation of the {@link WriteStream} interface. When
 * extending this class, the only method that a concrete implementation is
 * required to implement is the {@link #writeByte(byte)} method.
 * 
 * <p>
 * All values are written big-endian.
 * 
 * @author Tom Gibara
 *
 */
public abstract class AbstractWriteStream implements WriteStream {

	/**
	 * Writes a single boolean to the stream. A false value is encoded as zero
	 * and a true value as minus one (ie. all bits set).
	 * 
	 * @param v
	 *            a boolean
	 * @throws StreamException
	 *             if an error occurs writing the boolean.
	 */

	@Override
	public void writeBoolean(boolean v) {
		writeByte( (byte) (v ? -1 : 0) );
	}

	@Override
	public void writeShort(short v) {
		writeByte( (byte) (v >>  8) );
		writeByte( (byte) (v      ) );
	}

	@Override
	public void writeChar(char v) {
		writeByte( (byte) (v >>  8) );
		writeByte( (byte) (v      ) );
	}

	@Override
	public void writeInt(int v) {
		writeByte( (byte) (v >> 24) );
		writeByte( (byte) (v >> 16) );
		writeByte( (byte) (v >>  8) );
		writeByte( (byte) (v      ) );
	}

	@Override
	public void writeLong(long v) {
		writeByte( (byte) (v >> 56) );
		writeByte( (byte) (v >> 48) );
		writeByte( (byte) (v >> 40) );
		writeByte( (byte) (v >> 32) );
		writeByte( (byte) (v >> 24) );
		writeByte( (byte) (v >> 16) );
		writeByte( (byte) (v >>  8) );
		writeByte( (byte) (v      ) );
	}

	/**
	 * Writes a single float to the stream. The float is written a per
	 * {@link Float#floatToIntBits(float)}.
	 * 
	 * @param v
	 *            a float
	 * @throws StreamException
	 *             if an error occurs writing the float
	 */

	@Override
	public void writeFloat(float v) {
		writeInt(Float.floatToIntBits(v));
	}

	/**
	 * Writes a single double to the stream. The double is written a per
	 * {@link Double#doubleToLongBits(double)}.
	 * 
	 * @param v
	 *            a double
	 * @throws StreamException
	 *             if an error occurs writing the double
	 */

	@Override
	public void writeDouble(double v) {
		writeLong(Double.doubleToLongBits(v));
	}

	@Override
	public void writeBytes(byte[] bs) {
		writeBytes(bs, 0, bs.length);
	}

	@Override
	public void writeBytes(byte[] bs, int off, int len) {
		final int lim = off + len;
		for (int i = off; i < lim; i++) writeByte(bs[i]);
	}

	/**
	 * Writes a single char to the stream. The char is treated as a Java
	 * primitive with a width of two bytes.
	 * 
	 * @param v
	 *            a char
	 * @throws StreamException
	 *             if an error occurs writing the char
	 */

	@Override
	public void writeChars(char[] cs) {
		writeChars(cs, 0, cs.length);
	}

	@Override
	public void writeChars(char[] cs, int off, int len) {
		final int lim = off + len;
		for (int i = off; i < lim; i++) writeChar(cs[i]);
	}

	/**
	 * Writes a character sequence to the stream. The length of the character
	 * sequence is encoded as an int followed by its character data.
	 * 
	 * @param cs
	 *            a character sequence
	 * @throws StreamException
	 *             if an error occurs writing the chars
	 */

	@Override
	public void writeChars(CharSequence cs) {
		final int length = cs.length();
		writeInt(length);
		if (cs instanceof String) {
			writeChars(((String) cs).toCharArray());
		} else {
			for (int i = 0; i < length; i++) {
				writeChar(cs.charAt(i));
			}
		}
	}

	@Override
	public void close() {
		/* do nothing */
	}

}
