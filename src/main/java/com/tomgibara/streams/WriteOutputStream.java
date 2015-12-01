package com.tomgibara.streams;

import java.io.IOException;
import java.io.OutputStream;

class WriteOutputStream extends OutputStream {

	private final WriteStream stream;
	
	public WriteOutputStream(WriteStream stream) {
		this.stream = stream;
	}
	
	@Override
	public void write(int b) throws IOException {
		try {
			stream.writeByte((byte) b);
		} catch (StreamException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(byte[] b) throws IOException {
		try {
			stream.writeBytes(b);
		} catch (StreamException e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		try {
			stream.writeBytes(b, off, len);
		} catch (StreamException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void close() throws IOException {
		try {
			stream.close();
		} catch (StreamException e) {
			throw new IOException(e);
		}
	}

}
