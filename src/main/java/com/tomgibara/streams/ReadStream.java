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

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

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

@FunctionalInterface
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
	 * Fills the buffer with bytes read from the stream. Bytes will be written
	 * starting from <i>position</i> and continuing until <i>limit</i> is
	 * reached. If an 'end-of-stream' condition occurs, no
	 * {@link EndOfStreamException} is raised, instead the buffer is returned
	 * without reaching its limit.
	 *
	 * @param buffer
	 *            the buffer to contain the read bytes
	 * @throws StreamException
	 *             if the bytes could not be read
	 */

	default void fillBuffer(ByteBuffer buffer) throws StreamException {
		try {
			while (buffer.hasRemaining()) {
				buffer.put(readByte());
			}
		} catch (EndOfStreamException e) {
			// swallowed - unfilled buffer indicates EOS
			return;
		}
	}

	// convenience methods

	/**
	 * A reader that draws from the same stream of bytes, but which will not
	 * permit more than the specified number of bytes to be read without
	 * reporting an end-of-stream condition.
	 *
	 * @param length
	 *            the greatest number of bytes that the returned stream may
	 *            return
	 * @return a stream limited to a specified number of bytes
	 */

	default ReadStream bounded(long length) {
		return new BoundedReadStream(this, length);
	}

	/**
	 * A reader that guards the close method of this reader with the specified
	 * closer implementation.
	 *
	 * @param closer
	 *            provides the logic to be performed on stream closure
	 *
	 * @return a stream returning the same bytes as this reader
	 */

	default ReadStream closedWith(StreamCloser closer) {
		if (closer == null) throw new IllegalArgumentException("null closer");
		return new ClosedReadStream(this, closer);
	}

	/**
	 * Returns an <code>InputStream</code> that draws from the same stream of
	 * bytes.
	 *
	 * @return the stream as an <code>InputStream</code>
	 */

	default InputStream asInputStream() {
		return new ReadInputStream(this);
	}

	/**
	 * Returns a <code>ReadableByteChannel</code> that draws from the same
	 * stream of bytes.
	 *
	 * @return the stream as an <code>ReadableByteChannel</code>
	 */

	default ReadableByteChannel asChannel() {
		return new ReadableStreamChannel(this);
	}

	/**
	 * Creates a new object for transferring data to the specified target
	 * stream. This constructor takes care of choosing the best buffering
	 * allocation strategy for the supplied streams based on their indicated
	 * preferences. Note that if neither the source nor the target supports
	 * buffering, then no buffer will be allocated, otherwise a buffer will be
	 * allocated at a default size chosen by the implementation.
	 *
	 * @param target
	 *            the stream receiving byte data
	 * @return an object for transferring data between the streams
	 */

	default StreamTransfer to(WriteStream target) {
		if (target == null) throw new IllegalArgumentException("null target");
		return new StreamTransfer(this, target);
	}

	/**
	 * Creates a new object for transferring data to the specified target
	 * stream, using a buffer of a specified size. Supplying a zero buffer-size
	 * disables buffering. This constructor takes care of choosing the best
	 * buffering allocation strategy for the supplied streams based on their
	 * indicated preferences. Note that if neither the source nor the target
	 * supports buffering, then no buffer will be allocated irrespective of the
	 * value supplied for <code>bufferSize</code>.
	 *
	 * @param target
	 *            the stream receiving byte data
	 * @param bufferSize
	 *            the size of the desired buffer or zero to disable buffering
	 * @return an object for transferring data between the streams
	 */

	default StreamTransfer to(WriteStream target, int bufferSize) {
		if (target == null) throw new IllegalArgumentException("null target");
		return new StreamTransfer(this, target, bufferSize);
	}

	/**
	 * Creates a new object for transferring data to the specified target
	 * stream, using a supplied buffer. Supplying a null or empty buffer
	 * disables buffering. Note that if neither the source nor the target
	 * supports buffering, then buffer will remain unused.
	 *
	 * @param target
	 *            the stream receiving byte data
	 * @param buffer
	 *            a buffer that may be used for the transfer
	 * @return an object for transferring data between the streams
	 */

	default StreamTransfer to(WriteStream target, ByteBuffer buffer) {
		if (target == null) throw new IllegalArgumentException("null target");
		return new StreamTransfer(this, target, buffer);
	}

	/**
	 * Attaches a deserializer to the stream to allow object values to be
	 * produced from the primitive values returned by this stream.
	 *
	 * @param deserializer
	 *            converts byte data into objects
	 * @param <T>
	 *            the type of object created by the deserializer
	 * @return a producer that produces object from the supplied deserializer
	 */

	default <T> Producer<T> readWith(StreamDeserializer<T> deserializer) {
		return () -> deserializer.deserialize(this);
	}

	// closeable

	default void close() {
		/* do nothing */
	}
}
