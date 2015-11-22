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
	

	public static void transfer(ReadStream in, WriteStream out, long count, ByteBuffer buffer) {
		if (in == null) throw new IllegalArgumentException("null in");
		if (out == null) throw new IllegalArgumentException("null out");
		if (count < 0L) throw new IllegalArgumentException("negative count");
		if (buffer == null) throw new IllegalArgumentException("null buffer");
		buffer.clear();
		int capacity = buffer.capacity();
		while (count > 0) {
			if (count < capacity) {
				buffer.limit((int) count);
				count = 0;
			} else {
				count -= capacity;
			}
			in.fillBuffer(buffer);
			buffer.flip();
			out.drainBuffer(buffer);
			buffer.flip();
		}
	}
	
}
