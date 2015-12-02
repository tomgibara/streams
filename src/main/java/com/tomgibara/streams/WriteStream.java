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

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import com.tomgibara.fundament.Consumer;

/**
 * <p>
 * An abstraction for writing basic Java types into a byte based stream.
 *
 * <p>
 * Due to the presence of default implementations, the only method that a
 * concrete implementation is required to implement is the
 * {@link #writeByte(byte)} method. In the default implementations all values
 * are written big-endian.
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

	default void writeBytes(byte bs[]) throws StreamException {
		writeBytes(bs, 0, bs.length);
	}

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

	default void writeBytes(byte bs[], int off, int len) throws StreamException {
		final int lim = off + len;
		for (int i = off; i < lim; i++) writeByte(bs[i]);
	}

	/**
	 * Writes a single int to the stream.
	 *
	 * @param v
	 *            an int
	 * @throws StreamException
	 *             if an error occurs writing the int
	 */

	default void writeInt(int v) throws StreamException {
		writeByte( (byte) (v >> 24) );
		writeByte( (byte) (v >> 16) );
		writeByte( (byte) (v >>  8) );
		writeByte( (byte) (v      ) );
	}

	/**
	 * <p>
	 * Writes a single boolean to the stream.
	 *
	 * <p>
	 * In the default implementation a false value is encoded as zero and a true
	 * value as minus one (ie. all bits set).
	 *
	 * @param v
	 *            a boolean
	 * @throws StreamException
	 *             if an error occurs writing the boolean.
	 */

	default void writeBoolean(boolean v) throws StreamException {
		writeByte( (byte) (v ? -1 : 0) );
	}

	/**
	 * Writes a single short to the stream.
	 *
	 * @param v
	 *            a short
	 * @throws StreamException
	 *             if an error occurs writing the short
	 */

	default void writeShort(short v) throws StreamException {
		writeByte( (byte) (v >>  8) );
		writeByte( (byte) (v      ) );
	}

	/**
	 * Writes a single long to the stream.
	 *
	 * @param v
	 *            a long
	 * @throws StreamException
	 *             if an error occurs writing the long
	 */

	default void writeLong(long v) throws StreamException {
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
	 * <p>
	 * Writes a single float to the stream.
	 *
	 * <p>
	 * In the default implementation, the float is written as per
	 * {@link Float#floatToIntBits(float)}.
	 *
	 * @param v
	 *            a float
	 * @throws StreamException
	 *             if an error occurs writing the float
	 */

	default void writeFloat(float v) throws StreamException {
		writeInt(Float.floatToIntBits(v));
	}

	/**
	 * <p>
	 * Writes a single double to the stream.
	 *
	 * <p>
	 * In the default implementation, the double is written as per
	 * {@link Double#doubleToLongBits(double)}.
	 *
	 * @param v
	 *            a double
	 * @throws StreamException
	 *             if an error occurs writing the double
	 */

	default void writeDouble(double v) throws StreamException {
		writeLong(Double.doubleToLongBits(v));
	}

	/**
	 * <p>
	 * Writes a single char to the stream.
	 *
	 * <p>
	 * In the default implementation, the char is treated as a Java primitive
	 * with a width of two bytes.
	 *
	 * @param v
	 *            a char
	 * @throws StreamException
	 *             if an error occurs writing the char
	 */

	default void writeChar(char v) throws StreamException {
		writeByte( (byte) (v >>  8) );
		writeByte( (byte) (v      ) );
	}

	/**
	 * Writes an array of chars to the stream.
	 *
	 * @param cs
	 *            a char array
	 * @throws StreamException
	 *             if an error occurs writing the chars
	 */

	default void writeChars(char[] cs) throws StreamException {
		writeChars(cs, 0, cs.length);
	}

	/**
	 * Writes an array slice of chars to the stream.
	 *
	 * @param cs
	 *            a char array
	 * @param off
	 *            the index from which the first char written is read
	 * @param len
	 *            the number of chars to be written
	 * @throws StreamException
	 *             if an error occurs writing the chars
	 */

	default void writeChars(char[] cs, int off, int len) throws StreamException {
		final int lim = off + len;
		for (int i = off; i < lim; i++) writeChar(cs[i]);
	}

	/**
	 * <p>
	 * Writes a character sequence (typically a String instance) to the stream.
	 * The stream implementation is expected to record the length of the
	 * character sequence in addition to any character data it contains.
	 *
	 * <p>
	 * In the default implementation, the length of the character sequence is
	 * encoded as an int followed by its character data.
	 *
	 * @param cs
	 *            a character sequence
	 * @throws StreamException
	 *             if an error occurs writing the chars
	 */

	default void writeChars(CharSequence cs) throws StreamException {
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

	/**
	 * Indicates the preferred buffering strategy for this stream
	 * implementation.
	 * 
	 * @return the preferred buffering strategy
	 */

	default StreamBuffering getBuffering() {
		return StreamBuffering.UNSUPPORTED;
	}
	
	/**
	 * Writes bytes to the stream from a buffer. Bytes will be read starting at
	 * <i>position</i> and continuing until <i>limit</i> is reached. If an
	 * 'end-of-stream' condition occurs, no {@link EndOfStreamException} is
	 * raised, instead the buffer is returned without reaching its limit.
	 * 
	 * @param buffer
	 *            the buffer containing bytes to be written
	 * @return the number of bytes written
	 * @throws StreamException
	 *             if the bytes could not be written
	 */
	
	default void drainBuffer(ByteBuffer buffer) throws StreamException {
		try {
			for (int i = buffer.remaining(); i > 0; i--) {
				writeByte(buffer.get());
			}
		} catch (EndOfStreamException e) {
			// swallowed - unfilled buffer indicates EOS
			return;
		}
	}

	// convenience methods

	default WriteStream bounded(long length) {
		return new BoundedWriteStream(this, length);
	}
	
	default OutputStream asOutputStream() {
		return new WriteOutputStream(this);
	}

	default WritableByteChannel asChannel() {
		return new WritableStreamChannel(this);
	}
	
	default <T> Consumer<T> writeWith(StreamSerializer<T> serializer) {
		return v -> serializer.serialize(v, this);
	}

	// closeable
	
	@Override
	default void close() {
		/* do nothing */
	}

}
