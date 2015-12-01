package com.tomgibara.streams;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

class BoundedWritableChannel implements WritableByteChannel {

	private final WritableByteChannel channel;
	private long length;
	
	BoundedWritableChannel(WritableByteChannel channel, long length) {
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
	public int write(ByteBuffer src) throws IOException {
		int remaining = src.remaining();
		// happy case - we can read as much as we want
		if (remaining < length) {
			int count = channel.write(src);
			if (count != -1) length -= count;
			return count;
		}
		// other case - we need to artificially limit the buffer
		int oldLimit = src.limit();
		src.limit(src.position() + (int) length);
		try {
			int count = channel.write(src);
			if (count != -1) length -= count;
			return count;
		} finally {
			src.limit(oldLimit);
		}
	}

}
