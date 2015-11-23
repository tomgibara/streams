package com.tomgibara.streams;

import java.nio.ByteBuffer;

//TODO document
public class Streams {

	public static long transfer(ReadStream in, WriteStream out) {
		if (in == null) throw new IllegalArgumentException("null in");
		if (out == null) throw new IllegalArgumentException("null out");
		long count = 0L;
		try {
			while (true) {
				out.writeByte(in.readByte());
				count ++;
			}
		} catch (EndOfStreamException e) {
			/* we swallow this - it's essentially inevitable */
			return count;
		}
	}
	

	public static long transfer(ReadStream in, WriteStream out, long count) {
		if (in == null) throw new IllegalArgumentException("null in");
		if (out == null) throw new IllegalArgumentException("null out");
		if (count < 0L) throw new IllegalArgumentException("negative count");
		long c = 0L;
		try {
			while (c < count) {
				out.writeByte(in.readByte());
				c ++;
			}
		} catch (EndOfStreamException e) {
			/* we swallow this - it's essentially inevitable */
		}
		return c;
	}


	public static void transfer(ReadStream in, WriteStream out, ByteBuffer buffer) {
		if (in == null) throw new IllegalArgumentException("null in");
		if (out == null) throw new IllegalArgumentException("null out");
		if (buffer == null) throw new IllegalArgumentException("null buffer");
		if (buffer.capacity() == 0) throw new IllegalArgumentException("buffer has zero capacity");
		buffer.clear();
		while (true) {
			in.fillBuffer(buffer);
			boolean last = buffer.hasRemaining();
			buffer.flip();
			out.drainBuffer(buffer);
			buffer.clear();
			if (last) return;
		}
	}


	public static void transfer(ReadStream in, WriteStream out, long count, ByteBuffer buffer) {
		if (in == null) throw new IllegalArgumentException("null in");
		if (out == null) throw new IllegalArgumentException("null out");
		if (count < 0L) throw new IllegalArgumentException("negative count");
		if (buffer == null) throw new IllegalArgumentException("null buffer");
		int capacity = buffer.capacity();
		if (capacity == 0) throw new IllegalArgumentException("buffer has zero capacity");
		buffer.clear();
		boolean eos = false;
		while (!eos && count > 0) {
			if (count < capacity) {
				buffer.limit((int) count);
				count = 0;
			} else {
				count -= capacity;
			}
			in.fillBuffer(buffer);
			eos = buffer.hasRemaining();
			buffer.flip();
			out.drainBuffer(buffer);
			buffer.clear();
		}
	}
	
}
