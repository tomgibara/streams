package com.tomgibara.streams;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

//TODO provide skip method on streams?
class ReadInputStream extends InputStream {

	private final ReadStream stream;
	
	public ReadInputStream(ReadStream stream) {
		this.stream = stream;
	}
	
	@Override
	public int read() throws IOException {
		try {
			return stream.readByte() & 0xff;
		} catch (EndOfStreamException e) {
			return -1;
		} catch (StreamException e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return read(ByteBuffer.wrap(b));
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return read(ByteBuffer.wrap(b, off, len));
	}

	@Override
	public void close() throws IOException {
		try {
			stream.close();
		} catch (StreamException e) {
			throw new IOException(e);
		}
	}

	private int read(ByteBuffer buffer) throws IOException {
		try {
			stream.fillBuffer(buffer);
		} catch (StreamException e) {
			throw new IOException(e);
		}
		return buffer.position();
	}
}
