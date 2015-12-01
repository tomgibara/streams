package com.tomgibara.streams;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

class ReadableStreamChannel implements ReadableByteChannel {

	private final ReadStream stream;
	
	ReadableStreamChannel(ReadStream stream) {
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
	public int read(ByteBuffer dst) throws IOException {
		int from = dst.position();
		try {
			stream.fillBuffer(dst);
		} catch (StreamException e) {
			throw new IOException(e);
		}
		int to = dst.position();
		return to - from;
	}

}
