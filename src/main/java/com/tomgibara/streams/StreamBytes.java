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

import java.util.Arrays;

/**
 * <p>
 * Instances of this class can accumulate a byte array via an attached
 * {@link WriteStream} and/or stream a byte array via an attached
 * {@link ReadStream}.
 * 
 * <p>
 * The class is designed to support a variety of modes of operation, examples
 * include:
 * 
 * <ul>
 * <li>Initializing via {@link Streams#bytes(byte[],int)}, calling
 * {@link #readStream()} and reading the data via its <em>read</em> methods.
 * <li>Initializing via {@link Streams#bytes()}, calling {@link #writeStream()},
 * accumulating byte data via its <em>write</em> methods, and retrieving the
 * data via the {@link #bytes()} method.
 * <li>Initializing via {@link Streams#bytes()}, calling {@link #writeStream()},
 * accumulating byte data via its <em>write</em> methods, calling
 * {@link #readStream()} and retrieving the data via its <em>read</em> methods.
 * <li>Initializing via {@link Streams#bytes(int, int)} and alternating calls to
 * {@link #writeStream()} and {@link #readStream()} to provide a reusable buffer for
 * proxying structured byte data.
 * </ul>
 * 
 * @author Tom Gibara
 * 
 * @see Streams
 * @see ReadStream
 * @see WriteStream
 *
 */

public class StreamBytes {

	private final int maxCapacity;
	private byte[] bytes;
	private int length;
	private BytesWriteStream writer = null;
	private BytesReadStream reader = null;

	StreamBytes(byte[] bytes, int length, int maxCapacity) {
		this.maxCapacity = maxCapacity;
		this.length = length;
		this.bytes = bytes;
	}

	/**
	 * The maximum capacity permitted for writers attached to this object. When
	 * unconstrained this method will report the maximum possible array size.
	 * 
	 * @return the maximum capacity in bytes
	 */

	public int getMaxCapacity() {
		return maxCapacity;
	}

	/**
	 * <p>
	 * The number of bytes that are stored in the byte array that underlies this
	 * object. Calling this method will cause any attached writer to be closed.
	 * 
	 * <p>
	 * The initial value of this field is dependent on the parameters passed to
	 * the <code>bytes</code> factory methods in {@link Streams}.
	 * 
	 * @return the number of bytes.
	 */

	public int length() {
		detachWriter();
		return length;
	}

	/**
	 * Attaches a writer to the object. If there is already an attached writer,
	 * the existing writer is returned. If a reader is attached to the object
	 * when this method is called, the reader is closed and immediately detached
	 * before a writer is created.
	 * 
	 * @return the writer attached to this object
	 */
	public WriteStream writeStream() {
		detachReader();
		if (writer == null) {
			writer = new BytesWriteStream(bytes, maxCapacity);
		}
		return writer;
	}
	
	/**
	 * Attaches a reader to the object. If there is already any attached reader,
	 * the existing reader is returned. If a writer is attached to the object
	 * when this method is called, the writer is closed and immediately detached
	 * before the reader is created.
	 * 
	 * @return the reader attached to this object
	 */
	
	public ReadStream readStream() {
		detachWriter();
		if (reader == null) {
			reader = new BytesReadStream(bytes, 0, length);
		}
		return reader;
	}

	/**
	 * <p>
	 * Returns the bytes accumulated by this object. A copy of the internal data
	 * store of the last writer opened on this object is returned. If no writer
	 * has yet been opened via the {@link #writeStream()} method, a copy of the
	 * initial byte array is returned.
	 * 
	 * @return the byte data stored by this object
	 */

	public byte[] bytes() {
		return writer == null ? Arrays.copyOf(bytes, length) : writer.getBytes(false);
	}

	/**
	 * <p>
	 * Returns current underlying byte array. Calling this method closes the
	 * writer (if necessary) and returns the byte array which backed it.
	 * Subsequent attempts to write to the writer will fail with an
	 * {@link StreamException}, though the {@link #writeStream()} method
	 * may be called to obtain a new writer that will reuse the direct byte
	 * array, avoiding allocation of a new buffer.
	 * 
	 * <p>
	 * Note that the length of an uncopied byte array may exceed the number of
	 * bytes written, for this reason direct retrieval of the byte storage is
	 * best reserved for situations where the initial capacity specified and not
	 * exceeded.
	 * 
	 * @return the byte data stored by this object
	 */

	public byte[] directBytes() {
		return writer == null ? bytes : writer.getBytes(true);
	}
	
	private void detachWriter() {
		if (writer != null) {
			bytes = writer.getBytes(true);
			length = writer.position();
			writer = null;
		}
	}
	
	private void detachReader() {
		if (reader != null) {
			reader.close();
			reader = null;
		}
	}
}
