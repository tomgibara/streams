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
import java.io.PrintWriter;
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
 * <p>
 * Unless otherwise indicated, attempting to write beyond the capacity of the
 * stream will raise an {@link EndOfStreamException}.
 *
 * @author Tom Gibara
 * @see EndOfStreamException
 */

@FunctionalInterface
public interface ReadStream extends CloseableStream {

	/**
	 * Reads a single byte from the underlying stream. Calling this method on an
	 * exhausted stream will raise an {@link EndOfStreamException}.
	 *
	 * @return the byte read
	 * @throws StreamException
	 *             if the byte couldn't be read
	 * @throws EndOfStreamException
	 *             if the stream contains no more bytes
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
	 * @see #tryReadBytes(byte[], int, int)
	 */

	default void readBytes(byte bs[], int off, int len) throws StreamException {
		int lim = off + len;
		for (int i = off; i < lim; i++) {
			bs[i] = readByte();
		}
	}

	/**
	 * Attempts to read bytes into a byte array up to the end of the stream.
	 * This method returns the number of bytes written into the supplied array.
	 * If the end of the stream is encountered before all the bytes were read,
	 * this number will be less than <code>len</code> and may be zero. In
	 * contrast to other methods on this interface, this method does not raise
	 * an {@link EndOfStreamException} when the stream is exhausted.
	 *
	 * @param bs
	 *            the byte array into which bytes are read
	 * @param off
	 *            the index at which the first byte should be written
	 * @param len
	 *            the number of bytes to be read
	 * @return the number of bytes read
	 * @throws StreamException
	 *             if the bytes could not be read
	 * @see #readBytes(byte[], int, int)
	 */

	default int tryReadBytes(byte bs[], int off, int len) throws StreamException {
		int i = off;
		int lim = off + len;
		try {
			while (i < lim) {
				bs[i] = readByte();
				i++;
			}
			return len;
		} catch (EndOfStreamException e) {
			// swallowed - report how many bytes we've read
			return i - off;
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
		if (!buffer.hasRemaining()) return; // nothing to do
		if (buffer.hasArray()) {
			int offset = buffer.arrayOffset();
			byte[] array = buffer.array();
			int remaining = buffer.remaining();
			int read = tryReadBytes(array, offset, remaining);
			buffer.position(buffer.position() + read);
		} else {
			try {
				do {
					buffer.put(readByte());
				} while (buffer.hasRemaining());
			} catch (EndOfStreamException e) {
				// swallowed - unfilled buffer indicates EOS
				return;
			}
		}
	}

	// convenience methods

	/**
	 * Skips over a specified number of bytes from the stream; the skipped bytes
	 * are discarded. If the stream contains fewer unread bytes, an
	 * {@link EndOfStreamException} is raised.
	 *
	 * @param length
	 *            the number of bytes to skip
	 * @throws StreamException
	 *             if an error occurred when skipping the bytes
	 * @throws IllegalArgumentException
	 *             if the length is negative
	 */
	default void skip(long length) throws StreamException {
		if (length < 0L) throw new IllegalArgumentException("negative length");
		if (length >= Streams.SKIP_BUFFER_LIMIT) {
			int bufferSize = Streams.SKIP_BUFFER_SIZE;
			ByteBuffer buffer = Streams.createTemporaryBuffer(getBuffering(), bufferSize);
			if (buffer != null) {
				do {
					int r = length > Integer.MAX_VALUE ? bufferSize : Math.min((int) length, bufferSize);
					buffer.position(r).flip();
					fillBuffer(buffer);
					if (buffer.hasRemaining()) throw EndOfStreamException.instance();
					length -= r;
				} while (length > 0);
			}
			// falls through if no buffering
		}
		for (; length > 0; length --) {
			readByte();
		}
	}

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
	 * <p>
	 * A stream that exhausts this stream before returning data from a second
	 * stream. Between exhausting this stream and returning data from the
	 * supplied secondary stream, this stream is closed.
	 *
	 * <p>
	 * The secondary stream is closed with the explicit closure of the returned
	 * stream. If the returned stream is closed before this stream is exhausted
	 * then both streams are closed.
	 *
	 * @param stream
	 *            a stream from which data should be read once this stream is
	 *            exhausted
	 * @return a stream that concatenates the output of this stream with another
	 *
	 * @see #andThen(StreamCloser, ReadStream)
	 */

	default ReadStream andThen(ReadStream stream) {
		if (stream == null) throw new IllegalArgumentException("null stream");
		return new SeqReadStream(StreamCloser.closeStream(), this, stream);
	}

	/**
	 * <p>
	 * A stream that exhausts this stream before returning data from a second
	 * stream. Between exhausting this stream and returning data from the
	 * supplied secondary stream, the supplied stream may operated on by the
	 * supplied {@link StreamCloser}.
	 *
	 * <p>
	 * The supplied {@link StreamCloser} is only applied to this stream; the
	 * secondary stream is closed on closure of the returned stream. If the
	 * returned stream is closed before this stream is exhausted then the
	 * supplied {@link StreamCloser} is applied to this stream before the
	 * secondary stream is closed.
	 *
	 * @param closer
	 *            logic to be performed on this stream before returning data
	 *            from the secondary stream
	 * @param stream
	 *            a stream from which data should be read once this stream is
	 *            exhausted
	 * @return a stream that concatenates the output of this stream with another
	 *
	 * @see #andThen(ReadStream)
	 */

	default ReadStream andThen(StreamCloser closer, ReadStream stream) {
		if (closer == null) throw new IllegalArgumentException("null closer");
		if (stream == null) throw new IllegalArgumentException("null stream");
		return new SeqReadStream(closer, this, stream);
	}

	/**
	 * <p>
	 * A stream that exhausts another stream before returning data from this
	 * one. Between exhausting that stream and returning data from this one, the
	 * supplied stream is closed.
	 *
	 * <p>
	 * This stream is closed with the explicit closure of the returned stream.
	 * If the returned stream is closed before the supplied stream is exhausted
	 * then both streams are closed.
	 *
	 * @param stream
	 *            a stream from which data should be read to exhaustion before
	 *            this stream is read
	 * @return a stream that concatenates the output of another stream with this
	 *         one
	 *
	 * @see #butFirst(ReadStream, StreamCloser)
	 */

	default ReadStream butFirst(ReadStream stream) {
		if (stream == null) throw new IllegalArgumentException("null stream");
		return new SeqReadStream(StreamCloser.closeStream(), stream, this);
	}

	/**
	 * <p>
	 * A stream that exhausts another stream before returning data from this
	 * one. Between exhausting that stream and returning data from this one, the
	 * supplied stream may operated on by the supplied {@link StreamCloser}.
	 *
	 * <p>
	 * The supplied {@link StreamCloser} is only applied to the supplied stream;
	 * this stream is closed on closure of the returned stream. If the returned
	 * stream is closed before the supplied stream is exhausted then the
	 * supplied {@link StreamCloser} is applied to it stream before the this
	 * stream is closed.
	 *
	 * @param stream
	 *            a stream from which data should be read to exhaustion before
	 *            this stream is read
	 * @param closer
	 *            logic to be performed on the supplied stream before returning
	 *            data from this one
	 * @return a stream that concatenates the output of another stream with this
	 *         one
	 *
	 * @see #butFirst(ReadStream)
	 */

	default ReadStream butFirst(ReadStream stream, StreamCloser closer) {
		if (stream == null) throw new IllegalArgumentException("null stream");
		if (closer == null) throw new IllegalArgumentException("null closer");
		return new SeqReadStream(closer, stream, this);
	}

	/**
	 * <p>
	 * Wraps the stream in a new {@link ReadStream} that echoes all calls made
	 * via the {@link ReadStream} interface to a specified
	 * <code>PrintWriter</code>. Optionally, an identity can be specified to
	 * distinguishing the output of multiple debugging streams.
	 *
	 * <p>
	 * This method is intended to provide a convenient means of analyzing the
	 * low-level usage of streams for the purpose of analyzing performance
	 * and/or debugging.
	 *
	 * @param writer
	 *            the writer to which method calls should be logged
	 * @param identity
	 *            an identifier for the debug instance, may be null
	 * @return a stream that wraps this stream with logging
	 */

	default ReadStream debug(PrintWriter writer, String identity) {
		if (writer == null) throw new IllegalArgumentException("null writer");
		return new DebugReadStream(this, writer, identity);
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
