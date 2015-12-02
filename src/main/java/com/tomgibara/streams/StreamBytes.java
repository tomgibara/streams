package com.tomgibara.streams;

public class StreamBytes {

	private final int maxCapacity;
	private byte[] bytes;
	private ByteWriteStream writer = null;
	private ByteReadStream reader = null;

	StreamBytes(byte[] bytes, int maxCapacity) {
		this.maxCapacity = maxCapacity;
		this.bytes = bytes;
	}
	
	public int getMaxCapacity() {
		return maxCapacity;
	}

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
