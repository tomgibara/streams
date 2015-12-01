package com.tomgibara.streams;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

class BoundedReadableChannel implements ReadableByteChannel {

	private final ReadableByteChannel channel;
	private long length;
	
	BoundedReadableChannel(ReadableByteChannel channel, long length) {
		if (channel == null) throw new IllegalArgumentException("null channel");
		if (length < 0L) throw new IllegalArgumentException("negative length");
		this.channel = channel;
		this.length = length;
	}
	
	@Override
	public boolean isOpen() {
		return channel.isOpen();
	}
	
	@Override
	public void close() throws IOException {
		channel.close();
	}
	
	@Override
	public int read(ByteBuffer dst) throws IOException {
		int remaining = dst.remaining();
		// happy case - we can read as much as we want
		if (remaining < length) {
			int count = channel.read(dst);
			if (count != -1) length -= count;
			return count;
		}
		// other case - we need to artificially limit the buffer
		int oldLimit = dst.limit();
		dst.limit(dst.position() + (int) length);
		try {
			int count = channel.read(dst);
			if (count != -1) length -= count;
			return count;
		} finally {
			dst.limit(oldLimit);
		}
	}

}
