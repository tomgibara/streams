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

import com.tomgibara.fundament.Producer;

/**
 * <p>
 * An abstraction for reading basic Java types into a byte based stream.
 * Attempting to read from an exhausted stream will raise an
 * {@link EndOfStreamException}.
 * 
 * <p>
 * Due to the presence of default implementations, the only method required for
 * a concrete implementation is the {@link #readByte()} method. In the default
 * implementations all values are read big-endian.
 * 
 * @author Tom Gibara
 *
 */

public interface ReadStream extends CloseableStream {

	/**
	 * Reads a single byte from the underlying stream.
	 *
	 * @return the byte read
	 * @throws StreamException
	 *             if the byte couldn't be read
	 */

	byte readByte() throws StreamException;

	/**
	 * Reads bytes into a byte array. The array is fully filled with bytes from
	 * the stream or an {@link EndOfStreamException} is thrown.
	 *
	 * @param bs
	 *            the byte array into which bytes are read
	 * @throws StreamException
	 *             if the bytes could not be read
	 */

	default void readBytes(byte bs[]) throws StreamException {
		for (int i = 0; i < bs.length; i++) {
			bs[i] = readByte();
		}
	}

	/**
	 * Reads bytes into a byte array. The specified array segment is fully
	 * filled with bytes from the stream or an {@link EndOfStreamException} is
	 * thrown.
	 *
	 * @param bs
	 *            the byte array into which bytes are read
	 * @param off
	 *            the index at which the first byte read should be written
	 * @param len
	 *            the number of bytes to be read
	 * @throws StreamException
	 *             if the bytes could not be read
	 */

	default void readBytes(byte bs[], int off, int len) throws StreamException {
		int lim = off + len;
		for (int i = off; i < lim; i++) {
			bs[i] = readByte();
		}
	}

	/**
	 * Reads a single int from the underlying stream.
	 *
	 * @return the int read
	 * @throws StreamException
	 *             if the int couldn't be read
	 */

	default int readInt() throws StreamException {
		return
				 readByte()         << 24 |
				(readByte() & 0xff) << 16 |
				(readByte() & 0xff) <<  8 |
				 readByte() & 0xff;
	}

	/**
	 * <p>
	 * Reads a single boolean from the underlying stream.
	 * 
	 * <p>
	 * In the default implementation a zero value is returned a false and any
	 * non-zero value as true.
	 *
	 * @return the boolean read
	 * @throws StreamException
	 *             if the boolean couldn't be read
	 */

	default boolean readBoolean() throws StreamException {
		return readByte() != 0;
	}

	/**
	 * Reads a single short from the underlying stream.
	 *
	 * @return the short read
	 * @throws StreamException
	 *             if the short couldn't be read
	 */

	default short readShort() throws StreamException {
		return (short) (readByte() << 8 | readByte() & 0xff);
	}

	/**
	 * Reads a long byte from the underlying stream.
	 *
	 * @return the long read
	 * @throws StreamException
	 *             if the long couldn't be read
	 */

	default long readLong() throws StreamException {
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
	 * <p>
	 * Reads a single float from the underlying stream.
	 *
	 * <p>
	 * The float is read as per {@link Float#intBitsToFloat(int)}.
	 * 
	 * @return the float read
	 * @throws StreamException
	 *             if the float couldn't be read
	 */

	default float readFloat() throws StreamException {
		return Float.intBitsToFloat(readInt());
	}

	/**
	 * <p>
	 * Reads a double float from the underlying stream.
	 *
	 * <p>
	 * The double is read as per {@link Double#longBitsToDouble(long)}.
	 *
	 * @return the double read
	 * @throws StreamException
	 *             if the double couldn't be read
	 */

	default double readDouble() throws StreamException {
		return Double.longBitsToDouble(readLong());
	}

	/**
	 * <p>
	 * Reads a single char from the underlying stream.
	 *
	 * <p>
	 * In the default implementation, the character is treated as a Java
	 * primitive with a width of two bytes.
	 *
	 * @return the char read
	 * @throws StreamException
	 *             if the char couldn't be read
	 */

	default char readChar() throws StreamException {
		return (char) (readByte() << 8 | readByte() & 0xff);
	}

	/**
	 * Reads characters into a char array. The array is fully filled with
	 * characters from the stream or an {@link EndOfStreamException} is thrown.
	 *
	 * @param cs
	 *            the char array into which characters are read
	 * @throws StreamException
	 *             if the characters could not be read
	 */

	default void readChars(char[] cs) throws StreamException {
		for (int i = 0; i < cs.length; i++) {
			cs[i] = readChar();
		}
	}

	/**
	 * Reads characters into a char array. The specified array segment is fully
	 * filled with characters from the stream or an {@link EndOfStreamException}
	 * is thrown.
	 *
	 * @param cs
	 *            the char array into which characters are read
	 * @param off
	 *            the index at which the first character read should be written
	 * @param len
	 *            the number of characters to be read
	 * @throws StreamException
	 *             if the characters could not be read
	 */

	default void readChars(char[] cs, int off, int len) throws StreamException {
		int lim = off + len;
		for (int i = off; i < lim; i++) {
			cs[i] = readChar();
		}
	}

	/**
	 * <p>
	 * Reads a string from the stream. The stream implementation is expected to
	 * restore the length and character data of a string of character string.
	 *
	 * <p>
	 * In the default implementation the length of the string is read as an int
	 * and then the character data is read as per {@link #readChar()}.
	 *
	 * @return the string read
	 * @throws StreamException
	 *             if the string could not be read
	 */

	default String readChars() throws StreamException {
		char[] cs = new char[readInt()];
		readChars(cs);
		return new String(cs);
	}

	// convenience methods
	
	default <T> Producer<T> readWith(StreamDeserializer<T> deserializer) {
		return () -> deserializer.deserialize(this);
	}
	
	// closeable

	default void close() {
		/* do nothing */
	}
}
