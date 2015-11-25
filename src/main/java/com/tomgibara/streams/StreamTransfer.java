package com.tomgibara.streams;

import static com.tomgibara.streams.StreamBuffering.PREFER_ANY;
import static com.tomgibara.streams.StreamBuffering.PREFER_DIRECT;
import static com.tomgibara.streams.StreamBuffering.PREFER_INDIRECT;
import static com.tomgibara.streams.StreamBuffering.UNSUPPORTED;

import java.nio.ByteBuffer;

/**
 * Transfers data from a {@link ReadStream} to a {@link WriteStream}. Instances
 * of this class are generally short lived objects that are used once to affect
 * the the transfer of bytes from a source stream to a target stream.
 * 
 * @author Tom Gibara
 *
 */
public final class StreamTransfer {

	private static StreamBuffering combine(StreamBuffering src, StreamBuffering dst) {
		switch (src) {
		case UNSUPPORTED: return dst;
		case PREFER_ANY: return dst == UNSUPPORTED || dst == PREFER_ANY ? PREFER_DIRECT : dst;
		case PREFER_DIRECT: return dst == PREFER_INDIRECT ? PREFER_INDIRECT : PREFER_DIRECT;
		case PREFER_INDIRECT: return src;
		default: throw new IllegalStateException("Unexpected buffering type: " + src);
		}
	}
	
	private static ByteBuffer buffer(StreamBuffering src, StreamBuffering dst, int bufferSize) {
		if (bufferSize == 0) return null;
		StreamBuffering buffering = combine(src, dst);
		switch (buffering) {
		case UNSUPPORTED: return null;
		case PREFER_DIRECT: return ByteBuffer.allocateDirect(bufferSize);
		case PREFER_INDIRECT: return ByteBuffer.allocate(bufferSize);
		default: throw new IllegalStateException("Unexpected buffer type: " + buffering);
		}
	}
	
	private final ReadStream source;
	private final WriteStream target;
	private final ByteBuffer buffer;

	/**
	 * Creates a new object for transferring data between the specified source
	 * stream to the specified target stream, using a buffer of a specfied size.
	 * Supplying a zero buffer size disables buffering. This constructor takes
	 * care of choosing the best buffering allocation strategy for the supplied
	 * streams based on their indicated preferences. Note that if neither the
	 * source nor the target supports buffering, then no buffer will be
	 * allocated irrespective of the value supplied for <code>bufferSize</code>.
	 * 
	 * @param source
	 *            the stream supplying byte data
	 * @param target
	 *            the stream receiving byte data
	 * @param bufferSize
	 *            the size of the desired buffer or zero to disable buffering
	 */
	public StreamTransfer(ReadStream source, WriteStream target, int bufferSize) {
		if (source == null) throw new IllegalArgumentException("null source");
		if (target == null) throw new IllegalArgumentException("null target");
		if (bufferSize < 0) throw new IllegalArgumentException("negative bufferSize");
		this.source = source;
		this.target = target;
		buffer = buffer(source.getBuffering(), target.getBuffering(), bufferSize);
	}

	/**
	 * Creates a new object for transferring data between the specified source
	 * stream to the specified target stream, using a buffer of a specfied size.
	 * Supplying a null or empty buffer disables buffering. Note that if neither
	 * the source nor the target supports buffering, then buffer will remain
	 * unused.
	 * 
	 * @param source
	 *            the stream supplying byte data
	 * @param target
	 *            the stream receiving byte data
	 * @param buffer
	 *            a buffer that may be used for the transfer
	 */
	public StreamTransfer(ReadStream source, WriteStream target, ByteBuffer buffer) {
		if (source == null) throw new IllegalArgumentException("null source");
		if (target == null) throw new IllegalArgumentException("null target");
		if (buffer != null && buffer.isReadOnly()) throw new IllegalArgumentException("buffer read-only");
		this.source = source;
		this.target = target;
		this.buffer = buffer != null && buffer.capacity() == 0 ? null : buffer;
	}

	/**
	 * The buffering strategy in operation for this transfer.
	 * 
	 * @return the specific buffering used (ie. never
	 *         {@link StreamBuffering#PREFER_ANY})
	 */

	public StreamBuffering buffering() {
		if (buffer == null) return UNSUPPORTED;
		return buffer.isDirect() ? PREFER_DIRECT : PREFER_INDIRECT;
	}

	/**
	 * Transfers the specified number of bytes from the source to the target.
	 * Fewer bytes may be transferred if an end-of-stream condition occurs in
	 * either the source or the target.
	 * 
	 * @param count
	 *            the number of bytes to be transferred
	 * @return the actual number of bytes transferred
	 */

	public long transfer(long count) {
		if (count < 0L) throw new IllegalArgumentException("negative count");
		return buffer == null ? transferNoBuffer(count) : transferBuffered(count);
	}
	
	/**
	 * Transfers the bytes from the source to the target until one or possibly
	 * both streams are exhausted.
	 * 
	 * @return the number of bytes transferred
	 */

	public long transferFully() {
		return buffer == null ? transferNoBuffer() : transferBuffered();
	}
	
	private long transferNoBuffer() {
		long count = 0L;
		try {
			while (true) {
				target.writeByte(source.readByte());
				count ++;
			}
		} catch (EndOfStreamException e) {
			/* we swallow this - it's essentially inevitable */
			return count;
		}
	}

	private long transferBuffered() {
		buffer.clear();
		long count = 0L;
		while (true) {
			source.fillBuffer(buffer);
			boolean srcExhausted = buffer.hasRemaining();
			buffer.flip();
			count += buffer.remaining();
			target.drainBuffer(buffer);
			boolean dstExhausted = buffer.hasRemaining();
			count -= buffer.remaining();
			buffer.clear();
			if (srcExhausted || dstExhausted) return count;
		}
	}

	private long transferNoBuffer(long count) {
		long c = 0L;
		try {
			while (c < count) {
				target.writeByte(source.readByte());
				c ++;
			}
		} catch (EndOfStreamException e) {
			/* we swallow this - it's essentially inevitable */
		}
		return c;
	}

	private long transferBuffered(long count) {
		int capacity = buffer.capacity();
		if (capacity == 0) throw new IllegalArgumentException("buffer has zero capacity");
		buffer.clear();
		long c = count;
		while (c > 0) {
			if (c < capacity) buffer.limit((int) c);
			source.fillBuffer(buffer);
			boolean srcExhausted = buffer.hasRemaining();
			buffer.flip();
			c -= buffer.remaining();
			target.drainBuffer(buffer);
			boolean dstExhausted = buffer.hasRemaining();
			c += buffer.remaining();
			buffer.clear();
			if (srcExhausted || dstExhausted) break;
		}
		return count - c;
	}
}
