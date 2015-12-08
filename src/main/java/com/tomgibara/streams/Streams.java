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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

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

	private static byte[] array(int capacity) {
		if (capacity < 0) throw new IllegalArgumentException("capacity non-positive");
		return new byte[capacity];
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
	 * Creates a stream that reads from the supplied channel. Bytes will be read
	 * starting from the current channel position.
	 * 
	 * <p>
	 * Any {@link IOException} encountered by this class is wrapped as
	 * {@link StreamException} and rethrown. Any end-of-stream condition is
	 * signalled with an {@link EndOfStreamException} except when encountered
	 * during a call to {@link #fillBuffer(ByteBuffer)}, in that case, an EOS
	 * condition is identified by <code>buffer.hasRemaining()</code> returning
	 * true. Note that modifying the channel while accessing it via a stream is
	 * likely to produce inconsistencies.
	 * 
	 * @param channel
	 *            a byte channel
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
	 * during a call to {@link #fillBuffer(ByteBuffer)}, in that case, an EOS
	 * condition is identified by <code>buffer.hasRemaining()</code> returning
	 * true. Note that modifying the channel while accessing it via a stream is
	 * likely to produce inconsistencies.
	 * 
	 * @param channel
	 *            a byte channel
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
	 */

	public static WriteStream streamOutput(OutputStream out) {
		if (out == null) throw new IllegalArgumentException("null out");
		return new OutputWriteStream(out);
	}

	private Streams() { }

}
