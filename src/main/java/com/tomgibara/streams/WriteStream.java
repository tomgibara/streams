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
 * Abstraction for writing basic Java types into a byte based stream. 
 * 
 * @author Tom Gibara
 *
 */

public interface WriteStream extends CloseableStream {

	/**
	 * Writes a single byte to the stream.
	 * 
	 * @param v
	 *            a byte
	 * @throws StreamException
	 *             if an error occurs writing the byte
	 */

	void writeByte(byte v) throws StreamException;

	/**
	 * Writes an array of bytes to the stream.
	 * 
	 * @param bs
	 *            a byte array
	 * @throws StreamException
	 *             if an error occurs writing the bytes
	 */

	void writeBytes(byte bs[]) throws StreamException;

	/**
	 * Writes an array slice of bytes to the stream.
	 * 
	 * @param bs
	 *            a byte array
	 * @param off
	 *            the index from which the first byte written is read
	 * @param len
	 *            the number of bytes to be written
	 * @throws StreamException
	 *             if an error occurs writing the bytes
	 */

	void writeBytes(byte bs[], int off, int len) throws StreamException;

	/**
	 * Writes a single int to the stream.
	 * 
	 * @param v
	 *            an int
	 * @throws StreamException
	 *             if an error occurs writing the int
	 */

	void writeInt(int v) throws StreamException;

	/**
	 * Writes a single boolean to the stream.
	 * 
	 * @param v
	 *            a boolean
	 * @throws StreamException
	 *             if an error occurs writing the boolean.
	 */

	void writeBoolean(boolean v) throws StreamException;

	/**
	 * Writes a single short to the stream.
	 * 
	 * @param v
	 *            a short
	 * @throws StreamException
	 *             if an error occurs writing the short
	 */

	void writeShort(short v) throws StreamException;

	/**
	 * Writes a single long to the stream.
	 * 
	 * @param v
	 *            a long
	 * @throws StreamException
	 *             if an error occurs writing the long
	 */

	void writeLong(long v) throws StreamException;

	/**
	 * Writes a single float to the stream.
	 * 
	 * @param v
	 *            a float
	 * @throws StreamException
	 *             if an error occurs writing the float
	 */

	void writeFloat(float v) throws StreamException;

	/**
	 * Writes a single double to the stream.
	 * 
	 * @param v
	 *            a double
	 * @throws StreamException
	 *             if an error occurs writing the double
	 */

	void writeDouble(double v) throws StreamException;

	/**
	 * Writes a single char to the stream.
	 * 
	 * @param v
	 *            a char
	 * @throws StreamException
	 *             if an error occurs writing the char
	 */

	void writeChar(char v) throws StreamException;

	/**
	 * Writes an array of chars to the stream.
	 * 
	 * @param bs
	 *            a char array
	 * @throws StreamException
	 *             if an error occurs writing the chars
	 */

	void writeChars(char[] cs) throws StreamException;

	/**
	 * Writes an array slice of chars to the stream.
	 * 
	 * @param bs
	 *            a char array
	 * @param off
	 *            the index from which the first char written is read
	 * @param len
	 *            the number of chars to be written
	 * @throws StreamException
	 *             if an error occurs writing the chars
	 */

	void writeChars(char[] cs, int off, int len) throws StreamException;

	/**
	 * Writes a character sequence (typically a String instance) to the stream.
	 * The stream implementation is expected to record the length of the
	 * character sequence in addition to any character data it contains.
	 * 
	 * @param cs
	 *            a character sequence
	 * @throws StreamException
	 *             if an error occurs writing the chars
	 */

	void writeChars(CharSequence cs) throws StreamException;

}
