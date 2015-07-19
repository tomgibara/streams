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
 * An abstraction for reading basic Java types into a byte based stream.
 * Attempting to read from an exhausted stream will raise an
 * {@link EndOfStreamException}.
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

	void readBytes(byte bs[]) throws StreamException;

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

	void readBytes(byte bs[], int off, int len) throws StreamException;

	/**
	 * Reads a single int from the underlying stream.
	 *
	 * @return the int read
	 * @throws StreamException
	 *             if the int couldn't be read
	 */

	int readInt() throws StreamException;

	/**
	 * Reads a single boolean from the underlying stream.
	 *
	 * @return the boolean read
	 * @throws StreamException
	 *             if the boolean couldn't be read
	 */

	boolean readBoolean() throws StreamException;

	/**
	 * Reads a single short from the underlying stream.
	 *
	 * @return the short read
	 * @throws StreamException
	 *             if the short couldn't be read
	 */

	short readShort() throws StreamException;

	/**
	 * Reads a long byte from the underlying stream.
	 *
	 * @return the long read
	 * @throws StreamException
	 *             if the long couldn't be read
	 */

	long readLong() throws StreamException;

	/**
	 * Reads a single float from the underlying stream.
	 *
	 * @return the float read
	 * @throws StreamException
	 *             if the float couldn't be read
	 */

	float readFloat() throws StreamException;

	/**
	 * Reads a single double from the underlying stream.
	 *
	 * @return the double read
	 * @throws StreamException
	 *             if the double couldn't be read
	 */

	double readDouble() throws StreamException;

	/**
	 * Reads a single char from the underlying stream.
	 *
	 * @return the char read
	 * @throws StreamException
	 *             if the char couldn't be read
	 */

	char readChar() throws StreamException;

	/**
	 * Reads characters into a char array. The array is fully filled with
	 * characters from the stream or an {@link EndOfStreamException} is thrown.
	 *
	 * @param cs
	 *            the char array into which characters are read
	 * @throws StreamException
	 *             if the characters could not be read
	 */

	void readChars(char[] cs) throws StreamException;

	/**
	 * Reads characters into a char array. The specified array segment is fully
	 * filled with characters from the stream or an {@link EndOfStreamException}
	 * is thrown.
	 *
	 * @param bs
	 *            the char array into which characters are read
	 * @param off
	 *            the index at which the first character read should be written
	 * @param len
	 *            the number of characters to be read
	 * @throws StreamException
	 *             if the characters could not be read
	 */

	void readChars(char[] cs, int off, int len) throws StreamException;

	/**
	 * Reads a string from the stream. The stream implementation is expected to
	 * restore the length and character data of a string of character string.
	 *
	 * @return the string read
	 * @throws StreamException
	 *             if the string could not be read
	 */
	String readChars() throws StreamException;

}
