package com.tomgibara.streams;

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
 * <li>Initializing via {@link Streams#bytes(byte[])}, calling {@link #reader()}
 * and reading the data via its <em>read</em> methods.
 * <li>Initializing via {@link Streams#bytes()}, calling {@link #writer()},
 * accumulating byte data via its <em>write</em> methods, and retrieving the
 * data via the {@link #bytes()} method.
 * <li>Initializing via {@link Streams#bytes()}, calling {@link #writer()},
 * accumulating byte data via its <em>write</em> methods, calling
 * {@link #reader()} and retrieving the data via its <em>read</em> methods.
 * <li>Initializing via {@link Streams#bytes(int, int)} and alternating calls to
 * {@link #writer()} and {@link #reader()} to provide a reusable buffer for
 * proxying structured byte data.
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
	private ByteWriteStream writer = null;
	private ByteReadStream reader = null;

	StreamBytes(byte[] bytes, int maxCapacity) {
		this.maxCapacity = maxCapacity;
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
	 * Attaches a writer to the object. If there is already an attached writer,
	 * the existing writer is returned. If a reader is attached to the object
	 * when this method is called, the reader is closed and immediately detached
	 * before a writer is created.
	 * 
	 * @return the writer attached to this object
	 */
	public WriteStream writer() {
		if (reader != null) {
			reader.close(); //TODO should this do something?
			reader = null;
		}
		if (writer == null) {
			writer = new ByteWriteStream(bytes, maxCapacity);
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
	
	public ReadStream reader() {
		if (writer != null) {
			bytes = writer.getBytes(true);
			writer = null;
		}
		if (reader == null) {
			reader = new ByteReadStream(bytes);
		}
		return reader;
	}

	/**
	 * <p>
	 * Returns the bytes accumulated by this object. A copy of the internal data
	 * store of the last writer opened on this object is returned. If no writer
	 * has yet been opened via the {@link #writer()} method, a copy of the
	 * initial byte array is returned.
	 * 
	 * @return the byte data stored by this object
	 */

	public byte[] bytes() {
		return writer == null ? bytes.clone() : writer.getBytes(false);
	}

	/**
	 * <p>
	 * Returns current underlying byte array. Calling this method closes the
	 * writer (if necessary) and returns the byte array which backed it.
	 * Subsequent attempts to write to the writer will fail with an
	 * {@link EndOfStreamException} as per the {@link #close()} method, though
	 * the {@link #writer()} method may be called to obtain a new writer that
	 * will reuse the direct byte array, avoiding allocation of a new buffer.
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
}
