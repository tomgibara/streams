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

/**
 * <p>
 * A convenient base implementation of the {@link ReadStream} interface. When
 * extending this class, the only method that a concrete implementation is
 * required to implement is the {@link #readByte()} method.
 * 
 * <p>
 * All values are read big-endian.
 * 
 * @author Tom Gibara
 *
 */

public abstract class AbstractReadStream implements ReadStream {

	@Override
	public void close() {
		/* do nothing */
	}

	@Override
	public void readBytes(byte[] bs) {
		for (int i = 0; i < bs.length; i++) {
			bs[i] = readByte();
		}
	}

	@Override
	public void readBytes(byte[] bs, int off, int len) {
		int lim = off + len;
		for (int i = off; i < lim; i++) {
			bs[i] = readByte();
		}
	}

	@Override
	public int readInt() {
		return
				 readByte()         << 24 |
				(readByte() & 0xff) << 16 |
				(readByte() & 0xff) <<  8 |
				 readByte() & 0xff;
	}

	/**
	 * Reads a byte as a boolean. A zero value is returned a false and any
	 * non-zero value as true.
	 * 
	 * @return the boolean value read
	 */

	@Override
	public boolean readBoolean() {
		return readByte() != 0;
	}

	@Override
	public short readShort() {
		return (short) (readByte() << 8 | readByte() & 0xff);
	}

	@Override
	public long readLong() {
		return
				 (long) readByte()         << 56 |
				((long) readByte() & 0xff) << 48 |
				((long) readByte() & 0xff) << 40 |
				((long) readByte() & 0xff) << 32 |
				((long) readByte() & 0xff) << 24 |
				((long) readByte() & 0xff) << 16 |
				((long) readByte() & 0xff) <<  8 |
				 (long) readByte() & 0xff;
	}

	/**
	 * Reads a single float from the stream. The float is read as per
	 * {@link Float#intBitsToFloat(int)}.
	 * 
	 * @return the float read
	 */

	@Override
	public float readFloat() {
		return Float.intBitsToFloat(readInt());
	}

	/**
	 * Reads a single double from the stream. The double is read as per
	 * {@link Double#longBitsToDouble(long)}.
	 * 
	 * @return the double read
	 */

	@Override
	public double readDouble() {
		return Double.longBitsToDouble(readLong());
	}

	/**
	 * Reads a single char from the stream. The charactter is treated as a Java
	 * primitive with a width of two bytes.
	 * 
	 * @return the character read
	 */

	@Override
	public char readChar() {
		return (char) (readByte() << 8 | readByte() & 0xff);
	}

	@Override
	public void readChars(char[] cs) {
		for (int i = 0; i < cs.length; i++) {
			cs[i] = readChar();
		}
	}

	@Override
	public void readChars(char[] cs, int off, int len) {
		int lim = off + len;
		for (int i = off; i < lim; i++) {
			cs[i] = readChar();
		}
	}

	/**
	 * Reads a string from the stream. The length of the string is read as an
	 * int and then the character data is read as per {@link #readChar()}.
	 * 
	 * @return the string read
	 */

	@Override
	public String readChars() {
		char[] cs = new char[readInt()];
		readChars(cs);
		return new String(cs);
	}

}
