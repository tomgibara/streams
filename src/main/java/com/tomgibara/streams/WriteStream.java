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
import java.io.PrintWriter;
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

@FunctionalInterface
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

	/**
	 * A writer that contributes to the same stream of bytes, but which will not
	 * permit more than the specified number of bytes to be written without
	 * reporting an end-of-stream condition.
	 *
	 * @param length
	 *            the greatest number of bytes that the returned stream may
	 *            contribute
	 * @return a stream limited to a specified number of bytes
	 */

	default WriteStream bounded(long length) {
		return new BoundedWriteStream(this, length);
	}

	/**
	 * A writer that guards the close method of this writer with the specified
	 * closer implementation.
	 *
	 * @param closer
	 *            provides the logic to be performed on stream closure
	 *
	 * @return a stream that supplies bytes to this writer
	 */

	default WriteStream closedWith(StreamCloser closer) {
		if (closer == null) throw new IllegalArgumentException("null closer");
		return new ClosedWriteStream(this, closer);
	}

	/**
	 * <p>
	 * A stream that fills this stream before writing data to a second stream.
	 * Between filling this stream and writing data to the supplied secondary
	 * stream, this stream is closed.
	 *
	 * <p>
	 * The secondary stream is closed with the explicit closure of the returned
	 * stream. If the returned stream is closed before this stream is full then
	 * both streams are closed.
	 *
	 * @param closer
	 *            logic to be performed on this stream before writing data to
	 *            the secondary stream
	 * @param stream
	 *            a stream to which data should be written once this stream is
	 *            full
	 * @return a stream that splits its writing across this stream and another
	 *
	 * @see #andThen(StreamCloser, WriteStream)
	 */

	default WriteStream andThen(WriteStream stream) {
		if (stream == null) throw new IllegalArgumentException("null stream");
		return new SeqWriteStream(StreamCloser.closeStream(), this, stream);
	}

	/**
	 * <p>
	 * A stream that fills this stream before writing data to a second stream.
	 * Between filling this stream and writing data to the supplied secondary
	 * stream, the supplied stream may operated on by the supplied
	 * {@link StreamCloser}.
	 *
	 * <p>
	 * The supplied {@link StreamCloser} is only applied to this stream; the
	 * secondary stream is closed on closure of the returned stream. If the
	 * returned stream is closed before this stream is full then the supplied
	 * {@link StreamCloser} is applied to this stream before the secondary
	 * stream is closed.
	 *
	 * @param closer
	 *            logic to be performed on this stream before writing data to
	 *            the secondary stream
	 * @param stream
	 *            a stream to which data should be written once this stream is
	 *            full
	 * @return a stream that splits its writing across this stream and another
	 *
	 * @see #andThen(WriteStream)
	 */

	default WriteStream andThen(StreamCloser closer, WriteStream stream) {
		if (closer == null) throw new IllegalArgumentException("null closer");
		if (stream == null) throw new IllegalArgumentException("null stream");
		return new SeqWriteStream(closer, this, stream);
	}

	/**
	 * <p>
	 * A stream that fills another stream before writing data to this one.
	 * Between filling that stream and writing data to this one, the supplied
	 * stream is closed.
	 *
	 * <p>
	 * This stream is closed with the explicit closure of the returned stream.
	 * If the returned stream is closed before the supplied stream is full then
	 * both streams are closed.
	 *
	 * @param stream
	 *            a stream to which data should be written before this stream is
	 *            written to
	 * @return a stream that splits its writing across another stream and this
	 *
	 * @see #butFirst(WriteStream, StreamCloser)
	 */

	default WriteStream butFirst(WriteStream stream) {
		if (stream == null) throw new IllegalArgumentException("null stream");
		return new SeqWriteStream(StreamCloser.closeStream(), stream, this);
	}

	/**
	 * <p>
	 * A stream that fills another stream before writing data to this one.
	 * Between filling that stream and writing data to this one, the supplied
	 * stream may operated on by the supplied {@link StreamCloser}.
	 *
	 * <p>
	 * The supplied {@link StreamCloser} is only applied to the supplied stream;
	 * this stream is closed on closure of the returned stream. If the returned
	 * stream is closed before the supplied stream is full then the supplied
	 * {@link StreamCloser} is applied to it stream before the this stream is
	 * closed.
	 *
	 * @param stream
	 *            a stream to which data should be written before this stream is
	 *            written to
	 * @param closer
	 *            logic to be performed on the supplied stream before writing
	 *            data to this one
	 * @return a stream that splits its writing across another stream and this
	 *
	 * @see #butFirst(WriteStream)
	 */

	default WriteStream butFirst(WriteStream stream, StreamCloser closer) {
		if (stream == null) throw new IllegalArgumentException("null stream");
		if (closer == null) throw new IllegalArgumentException("null closer");
		return new SeqWriteStream(closer, stream, this);
	}

	/**
	 * Wraps the stream in a new {@link WriteStream} that echoes all calls made
	 * via the {@link WriteStream} interface to a specified
	 * <code>PrintWriter</code>. Optionally, an identity can be specified to
	 * distinguishing the output of multiple debugging streams.
	 * 
	 * @param writer
	 *            the writer to which method calls should be logged
	 * @param identity
	 *            an identifier for the debug instance, may be null
	 * @return a stream that wraps this stream with logging
	 */

	default WriteStream debug(PrintWriter writer, String identity) {
		if (writer == null) throw new IllegalArgumentException("null writer");
		return new DebugWriteStream(this, writer, identity);
	}

	/**
	 * Returns an <code>OutputStream</code> that draws from the same stream of
	 * bytes.
	 *
	 * @return the stream as an <code>OutputStream</code>
	 */

	default OutputStream asOutputStream() {
		return new WriteOutputStream(this);
	}

	/**
	 * Returns a <code>WritableByteChannel</code> that draws from the same
	 * stream of bytes.
	 *
	 * @return the stream as an <code>WritableByteChannel</code>
	 */

	default WritableByteChannel asChannel() {
		return new WritableStreamChannel(this);
	}

	/**
	 * Creates a new object for transferring data from the specified source
	 * stream. This constructor takes care of choosing the best buffering
	 * allocation strategy for the supplied streams based on their indicated
	 * preferences. Note that if neither the source nor the target supports
	 * buffering, then no buffer will be allocated, otherwise a buffer will be
	 * allocated at a default size chosen by the implementation.
	 *
	 * @param source
	 *            the stream supplying byte data
	 * @return an object for transferring data between the streams
	 */

	default StreamTransfer from(ReadStream source) {
		if (source == null) throw new IllegalArgumentException("null source");
		return new StreamTransfer(source, this);
	}

	/**
	 * Creates a new object for transferring data from the specified source
	 * stream, using a buffer of a specified size. Supplying a zero buffer-size
	 * disables buffering. This constructor takes care of choosing the best
	 * buffering allocation strategy for the supplied streams based on their
	 * indicated preferences. Note that if neither the source nor the target
	 * supports buffering, then no buffer will be allocated irrespective of the
	 * value supplied for <code>bufferSize</code>.
	 *
	 * @param source
	 *            the stream supplying byte data
	 * @param bufferSize
	 *            the size of the desired buffer or zero to disable buffering
	 * @return an object for transferring data between the streams
	 */

	default StreamTransfer from(ReadStream source, int bufferSize) {
		if (source == null) throw new IllegalArgumentException("null source");
		return new StreamTransfer(source, this, bufferSize);
	}

	/**
	 * Creates a new object for transferring data from the specified source
	 * stream, using a supplied buffer. Supplying a null or empty buffer
	 * disables buffering. Note that if neither the source nor the target
	 * supports buffering, then buffer will remain unused.
	 *
	 * @param source
	 *            the stream supplying byte data
	 * @param buffer
	 *            a buffer that may be used for the transfer
	 * @return an object for transferring data between the streams
	 */

	default StreamTransfer from(ReadStream source, ByteBuffer buffer) {
		if (source == null) throw new IllegalArgumentException("null source");
		return new StreamTransfer(source, this, buffer);
	}

	/**
	 * Attaches a serializer to the stream to allow object values to be written
	 * as primitive values to this stream.
	 *
	 * @param serializer
	 *            converts byte data into objects
	 * @param <T>
	 *            the type of object accepted by the serializer
	 * @return a consumer that supplies objects to the specified serializer
	 */

	default <T> Consumer<T> writeWith(StreamSerializer<T> serializer) {
		return v -> serializer.serialize(v, this);
	}

	// closeable

	@Override
	default void close() {
		/* do nothing */
	}

}
