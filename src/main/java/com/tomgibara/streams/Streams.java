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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * <p>
 * Main entry point for the streams API.
 *
 * <p>
 * The class provides static methods for creating {@link ReadStream} and
 * {@link WriteStream} instances, together with methods to create
 * {@link StreamBytes}.
 *
 * @author Tom Gibara
 *
 */

public final class Streams {

	private static final int DEFAULT_INITIAL_CAPACITY = 32;
	private static final int DEFAULT_MAXIMUM_CAPACITY = Integer.MAX_VALUE;

	private static final String BUFFER_SIZE_PROPERTY = "com.tomgibara.streams.bufferSize";
	private static final int DEFAULT_BUFFER_SIZE = 8192;

	private static final SeekableByteChannel NULL_SEEKER = new SeekableByteChannel() {
		@Override public boolean isOpen() { return false; }
		@Override public void close() { }
		@Override public int write(ByteBuffer src) { return 0; }
		@Override public SeekableByteChannel truncate(long size) { return null; }
		@Override public long size() { return 0; }
		@Override public int read(ByteBuffer dst) { return 0; }
		@Override public SeekableByteChannel position(long newPosition) { return null; }
		@Override public long position() { return -1L; }
	};

	static final int BUFFER_SIZE = bufferSize();

	private static int bufferSize() {
		PrivilegedAction<String> action = () -> { return System.getProperty(BUFFER_SIZE_PROPERTY); };
		String str = AccessController.doPrivileged(action);
		if (str != null) try {
			int bufferSize = Integer.parseInt(str);
			if (bufferSize > 0) return bufferSize;
			/* fall through */
		} catch (NumberFormatException e) {
			/* fall through */
		}
		return DEFAULT_BUFFER_SIZE;
	}

	private static byte[] array(int capacity) {
		if (capacity < 0) throw new IllegalArgumentException("capacity non-positive");
		return new byte[capacity];
	}

	static SeekableByteChannel seekerForChannel(Channel channel) {
		return channel instanceof SeekableByteChannel ? (SeekableByteChannel) channel : NULL_SEEKER;
	}

	// methods below are to report arguments in debug streams

	static String debugString(byte[] bytes) {
		return bytes == null ? "null" : "bytes[" + bytes.length + "]";
	}

	static String debugString(char[] chars) {
		return chars == null ? "null" : "chars[" + chars.length + "]";
	}

	static String debugString(ByteBuffer buffer) {
		return String.valueOf(buffer);
	}

	static String debugString(Object obj) {
		return obj == null ? "null" : obj.getClass().getTypeName() + '#' + System.identityHashCode(obj);
	}


	/**
	 * Creates a new {@link StreamBytes} to accumulate bytes written via a
	 * {@link WriteStream}.
	 *
	 * @return new bytes with default initial capacity and an unlimited maximum
	 *         capacity
	 */

	public static StreamBytes bytes() {
		return new StreamBytes(new byte[DEFAULT_INITIAL_CAPACITY], 0, DEFAULT_MAXIMUM_CAPACITY);
	}

	/**
	 * Creates a new {@link StreamBytes} to accumulate bytes written via a
	 * {@link WriteStream}.
	 *
	 * @param initialCapacity
	 *            the initial capacity of the byte store
	 * @return new bytes with the specified initial capacity and an unlimited
	 *         maximum capacity
	 */

	public static StreamBytes bytes(int initialCapacity) {
		return new StreamBytes(array(initialCapacity), 0, DEFAULT_MAXIMUM_CAPACITY);
	}

	/**
	 * Creates a new {@link StreamBytes} to accumulate bytes written via a
	 * {@link WriteStream}.
	 *
	 * @param initialCapacity
	 *            the initial capacity of the byte store
	 * @param maximumCapacity
	 *            the maximum capacity to which the byte store may grow
	 * @return new bytes with the specified capacities
	 */

	public static StreamBytes bytes(int initialCapacity, int maximumCapacity) {
		if (maximumCapacity < 0L) throw new IllegalArgumentException("negative maximumCapacity");
		if (initialCapacity > maximumCapacity) throw new IllegalArgumentException("initialCapacity exceeds maximumCapacity");
		return new StreamBytes(array(initialCapacity), 0, maximumCapacity);
	}

	/**
	 * Creates a new {@link StreamBytes} to expose bytes through a
	 * {@link ReadStream} or accumulate bytes through a {@link WriteStream}. If
	 * a reader is attached to the returned object before a writer is attached,
	 * all bytes in the array will be readable.
	 *
	 * @param bytes
	 *            a byte array containing the data to be read/overwritten
	 * @return new bytes with an unlimited maximum capacity
	 */

	public static StreamBytes bytes(byte[] bytes) {
		return new StreamBytes(bytes, bytes.length, DEFAULT_MAXIMUM_CAPACITY);
	}

	/**
	 * Creates a new {@link StreamBytes} to expose bytes through a
	 * {@link ReadStream} or accumulate bytes through a {@link WriteStream}.
	 *
	 * @param bytes
	 *            a byte array containing the data to be read/overwritten
	 * @param length
	 *            the number of readable bytes in the supplied array
	 * @return new bytes with an unlimited maximum capacity
	 */

	public static StreamBytes bytes(byte[] bytes, int length) {
		if (length < 0L) throw new IllegalArgumentException("negative length");
		return new StreamBytes(bytes, length, DEFAULT_MAXIMUM_CAPACITY);
	}

	/**
	 * Creates a new {@link StreamBytes} to expose bytes through a
	 * {@link ReadStream} or accumulate bytes through a {@link WriteStream}.
	 *
	 * @param bytes
	 *            a byte array containing the data to be read/overwritten
	 * @param length
	 *            the number of readable bytes in the supplied array
	 * @param maximumCapacity
	 *            the maximum capacity to which the byte store may grow
	 * @return new bytes the specified maximum capacity
	 */

	public static StreamBytes bytes(byte[] bytes, int length, int maximumCapacity) {
		if (length < 0L) throw new IllegalArgumentException("negative length");
		if (maximumCapacity < 0L) throw new IllegalArgumentException("negative maximumCapacity");
		if (bytes.length > maximumCapacity) throw new IllegalArgumentException("initial capacity exceeds maximumCapacity");
		return new StreamBytes(bytes, length, maximumCapacity);
	}

	/**
	 * <p>
	 * Creates a new {@link StreamBuffer} to expose the supplied
	 * <code>ByteBuffer</code> via a paired {@link ReadStream} and
	 * {@link WriteStream}.
	 *
	 * <p>
	 * It is strongly recommended that the supplied {@link ByteBuffer} uses big
	 * endian byte order (<code>ByteOrder.BIG_ENDIAN</code>) for compatibility
	 * with the other stream implementations provided by this library.
	 *
	 * @param buffer
	 *            the buffer to be exposed
	 * @return a new pair of streams backed by the supplied buffer
	 */

	public static StreamBuffer streamBuffer(ByteBuffer buffer) {
		if (buffer == null) throw new IllegalArgumentException("null buffer");
		if (buffer.isReadOnly()) throw new IllegalArgumentException("read-only buffer");
		return new StreamBuffer(buffer);
	}

	/**
	 * <p>
	 * Creates a stream that reads from the supplied channel. Bytes will be read
	 * starting from the current channel position.
	 *
	 * <p>
	 * Any {@link IOException} encountered by this class is wrapped as
	 * {@link StreamException} and rethrown. Any end-of-stream condition is
	 * signalled with an {@link EndOfStreamException} except when encountered
	 * during a call to {@link ReadStream#fillBuffer(ByteBuffer)}, in that case,
	 * an EOS condition is identified by <code>buffer.hasRemaining()</code>
	 * returning true. Note that modifying the channel while accessing it via a
	 * stream is likely to produce inconsistencies.
	 *
	 * <p>
	 * The returned stream supports accessing the stream position via
	 * {@link PositionalStream#position()} only over channels that are
	 * implementations of {@code SeekableByteChannel}.
	 *
	 * @param channel
	 *            a byte channel
	 * @return a stream over the supplied channel
	 *
	 * @see EndOfStreamException#EOS
	 */

	public static ReadStream streamReadable(ReadableByteChannel channel) {
		if (channel == null) throw new IllegalArgumentException("null channel");
		return new ChannelReadStream(channel);
	}

	/**
	 * <p>
	 * Creates a stream that writes to the supplied channel. Bytes will be
	 * written starting from the current channel position.
	 *
	 * <p>
	 * Any {@link IOException} encountered by the stream is wrapped as a
	 * {@link StreamException} and rethrown. Any end-of-stream condition is
	 * signalled with an {@link EndOfStreamException} except when encountered
	 * during a call to {@link WriteStream#drainBuffer(ByteBuffer)}, in that
	 * case, an EOS condition is identified by
	 * <code>buffer.hasRemaining()</code> returning true. Note that modifying
	 * the channel while accessing it via a stream is likely to produce
	 * inconsistencies.
	 *
	 * <p>
	 * The returned stream supports accessing the stream position via
	 * {@link PositionalStream#position()} only over channels that are
	 * implementations of {@code SeekableByteChannel}.
	 *
	 * @param channel
	 *            a byte channel
	 * @return a stream over the supplied channel
	 *
	 * @see EndOfStreamException#EOS
	 */

	public static WriteStream streamWritable(WritableByteChannel channel) {
		if (channel == null) throw new IllegalArgumentException("null channel");
		return new ChannelWriteStream(channel);
	}

	/**
	 * <p>
	 * Creates a new stream which obtains bytes data from an underlying
	 * {@link InputStream}
	 *
	 * <p>
	 * Any {@link IOException} encountered by this class is wrapped as
	 * {@link StreamException} and rethrown. Any end-of-stream condition is
	 * signalled with an {@link EndOfStreamException}.
	 *
	 * @param in
	 *            an input stream from which bytes should be read
	 * @return a stream over the supplied <code>InputStream</code>
	 *
	 * @see EndOfStreamException#EOS
	 */

	public static ReadStream streamInput(InputStream in) {
		if (in == null) throw new IllegalArgumentException("null in");
		return new InputReadStream(in);
	}

	/**
	 * <p>
	 * Creates a new stream which writes to an underlying {@link OutputStream}.
	 *
	 * <p>
	 * Any {@link IOException} encountered by this class is wrapped as
	 * {@link StreamException} and rethrown.
	 *
	 * @param out
	 *            an output stream to which bytes should be written
	 * @return a stream over the supplied <code>OutputStream</code>
	 */

	public static WriteStream streamOutput(OutputStream out) {
		if (out == null) throw new IllegalArgumentException("null out");
		return new OutputWriteStream(out);
	}

	/**
	 * <p>
	 * Creates a new stream which obtains bytes data from an underlying
	 * {@link DataInput}.
	 *
	 * <p>
	 * Any {@link IOException} encountered by this class is wrapped as
	 * {@link StreamException} and rethrown. Any end-of-stream condition is
	 * signalled with an {@link EndOfStreamException}.
	 *
	 * <p>
	 * The close method will close the {@code DataInput} instance only if it
	 * implements the {@code Closeable} interface.
	 *
	 * @param in
	 *            a data-input from which
	 * @return a stream over the supplied {@code DataInput}
	 */

	public static ReadStream streamDataInput(DataInput in) {
		if (in == null) throw new IllegalArgumentException("null in");
		return new DataReadStream(in);
	}

	/**
	 * <p>
	 * Creates a new stream which writes to an underlying {@link DataOutput}.
	 *
	 * <p>
	 * Any {@link IOException} encountered by this class is wrapped as
	 * {@link StreamException} and rethrown. Any end-of-stream condition is
	 * signalled with an {@link EndOfStreamException}.
	 *
	 * <p>
	 * The close method will close the {@code DataInput} instance only if it
	 * implements the {@code Closeable} interface.
	 *
	 * @param out
	 *            a data-output to which bytes should be written
	 * @return a stream over the supplied {@code DataOutput}
	 */

	public static WriteStream streamDataOutput(DataOutput out) {
		if (out == null) throw new IllegalArgumentException("null out");
		return new DataWriteStream(out);
	}

	/**
	 * An empty stream for which all attempts to read yield an
	 * {@link EndOfStreamException}.
	 *
	 * @return an empty {@link ReadStream}
	 */

	public static ReadStream streamFromEmpty() {
		return EmptyReadStream.INSTANCE;
	}

	/**
	 * An empty stream for which all attempts to write yield an
	 * {@link EndOfStreamException}.
	 *
	 * @return an empty {@link WriteStream}
	 */

	public static WriteStream streamToEmpty() {
		return EmptyWriteStream.INSTANCE;
	}

	/**
	 * <p>
	 * A stream that reads byte data from a fixed sequence of underlying
	 * streams.
	 *
	 * <p>
	 * This is a multi-stream analogue of {@link ReadStream#andThen(ReadStream)}
	 * or {@link ReadStream#butFirst(ReadStream)} methods. Each stream is closed
	 * after it has been exhausted, with the last stream being closed on an end
	 * of stream condition.
	 *
	 * <p>
	 * All unclosed streams are closed when {@link CloseableStream#close()} is
	 * called.
	 *
	 * @param streams
	 *            streams from which byte data is to be read
	 * @return a stream that concatenates the byte data of multiple streams.
	 */

	public static ReadStream concatReadStreams(ReadStream... streams) {
		if (streams == null) throw new IllegalArgumentException("null streams");
		for (ReadStream stream : streams) {
			if (stream == null) throw new IllegalArgumentException("null stream");
		}
		return new SeqReadStream(StreamCloser.closeStream(), streams);
	}

	/**
	 * <p>
	 * A stream that reads byte data from a fixed sequence of underlying
	 * streams.
	 *
	 * <p>
	 * This is a multi-stream analogue of
	 * {@link ReadStream#andThen(StreamCloser, ReadStream)} or
	 * {@link ReadStream#butFirst(StreamCloser, ReadStream)} methods. The
	 * supplied {@link StreamCloser} operates on each stream (including the
	 * last) after it has been exhausted.
	 *
	 * <p>
	 * All unclosed streams are operated on by the {@link StreamCloser} when
	 * {@link CloseableStream#close()} is called.
	 *
	 * @param streams
	 *            streams from which byte data is to be read
	 * @param closer
	 *            logic to be performed on each stream before returning data
	 *            from the next stream
	 * @return a stream that concatenates the byte data of multiple streams.
	 */

	public static ReadStream concatReadStreams(StreamCloser closer, ReadStream... streams) {
		if (closer == null) throw new IllegalArgumentException("null closer");
		if (streams == null) throw new IllegalArgumentException("null streams");
		for (ReadStream stream : streams) {
			if (stream == null) throw new IllegalArgumentException("null stream");
		}
		return new SeqReadStream(closer, streams);
	}

	/**
	 * <p>
	 * A stream that writes byte data to a fixed sequence of underlying streams.
	 *
	 * <p>
	 * This is a multi-stream analogue of
	 * {@link WriteStream#andThen(WriteStream)} or
	 * {@link WriteStream#butFirst(WriteStream)} methods. Each stream is
	 * closed after it has been filled, with the last stream being closed on
	 * an end of stream condition.
	 *
	 * <p>
	 * All unclosed streams are closed when {@link CloseableStream#close()} is
	 * called.
	 *
	 * @param streams
	 *            streams to which byte data is to be written
	 * @return a stream that splits its writing across multiple streams
	 */

	public static WriteStream concatWriteStreams(WriteStream... streams) {
		if (streams == null) throw new IllegalArgumentException("null streams");
		for (WriteStream stream : streams) {
			if (stream == null) throw new IllegalArgumentException("null stream");
		}
		return new SeqWriteStream(StreamCloser.closeStream(), streams);
	}

	/**
	 * <p>
	 * A stream that writes byte data to a fixed sequence of underlying streams.
	 *
	 * <p>
	 * This is a multi-stream analogue of
	 * {@link WriteStream#andThen(StreamCloser, WriteStream)} or
	 * {@link WriteStream#butFirst(StreamCloser, WriteStream)} methods. Each
	 * stream is closed after it has been filled, with the last stream being
	 * closed on an end of stream condition.
	 *
	 * <p>
	 * All unclosed streams are operated on by the {@link StreamCloser} when
	 * {@link CloseableStream#close()} is called.
	 *
	 * @param streams
	 *            streams to which byte data is to be written
	 * @param closer
	 *            logic to be performed on each stream before writing data to
	 *            the next stream
	 * @return a stream that splits its writing across multiple streams
	 */

	public static WriteStream concatWriteStreams(StreamCloser closer, WriteStream... streams) {
		if (closer == null) throw new IllegalArgumentException("null closer");
		if (streams == null) throw new IllegalArgumentException("null streams");
		for (WriteStream stream : streams) {
			if (stream == null) throw new IllegalArgumentException("null stream");
		}
		return new SeqWriteStream(closer, streams);
	}

	private Streams() { }

}
