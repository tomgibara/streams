package com.tomgibara.streams;

import static com.tomgibara.streams.StreamBuffering.PREFER_ANY;
import static com.tomgibara.streams.StreamBuffering.PREFER_DIRECT;
import static com.tomgibara.streams.StreamBuffering.PREFER_INDIRECT;
import static com.tomgibara.streams.StreamBuffering.UNSUPPORTED;

import java.nio.ByteBuffer;

//TODO document
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
	
	public StreamTransfer(ReadStream source, WriteStream target, int bufferSize) {
		if (source == null) throw new IllegalArgumentException("null source");
		if (target == null) throw new IllegalArgumentException("null target");
		if (bufferSize < 0) throw new IllegalArgumentException("negative bufferSize");
		this.source = source;
		this.target = target;
		buffer = buffer(source.getBuffering(), target.getBuffering(), bufferSize);
	}

	public StreamTransfer(ReadStream source, WriteStream target, ByteBuffer buffer) {
		if (source == null) throw new IllegalArgumentException("null source");
		if (target == null) throw new IllegalArgumentException("null target");
		if (buffer != null && buffer.isReadOnly()) throw new IllegalArgumentException("buffer read-only");
		this.source = source;
		this.target = target;
		this.buffer = buffer != null && buffer.capacity() == 0 ? null : buffer;
	}

	public StreamBuffering getBuffering() {
		if (buffer == null) return UNSUPPORTED;
		return buffer.isDirect() ? PREFER_DIRECT : PREFER_INDIRECT;
	}
	
	public long transfer(long count) {
		if (count < 0L) throw new IllegalArgumentException("negative count");
		return buffer == null ? transferNoBuffer(count) : transferBuffered(count);
	}
	
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
