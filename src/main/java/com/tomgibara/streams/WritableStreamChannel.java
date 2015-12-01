package com.tomgibara.streams;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

class WritableStreamChannel implements WritableByteChannel {

	private final WriteStream stream;
	
	WritableStreamChannel(WriteStream stream) {
		this.stream = stream;
	}
	
	@Override
	public boolean isOpen() {
		return true;
	}

	@Override
	public void close() throws IOException {
		try {
			stream.close();
		} catch (StreamException e) {
			throw new IOException(e);
		}
	}

	@Override
	public int write(ByteBuffer src) throws IOException {
		int from = src.position();
		try {
			stream.drainBuffer(src);
		} catch (StreamException e) {
			throw new IOException(e);
		}
		int to = src.position();
		return to - from;
	}

}
