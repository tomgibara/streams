package com.tomgibara.streams;

import java.io.IOException;
import java.io.OutputStream;

class BoundedOutputStream extends OutputStream {

	private final OutputStream stream;
	private long remaining;
	
	BoundedOutputStream(OutputStream stream, long length) {
		if (stream == null) throw new IllegalArgumentException("null stream");
		if (length < 0L) throw new IllegalArgumentException("negative length");
		this.stream = stream;
		this.remaining = length;
	}

	@Override
	public void write(int b) throws IOException {
		if (remaining <= 0L) throw new IOException("EOS");
		stream.write(b);
		remaining --;
	}

	@Override
	public void write(byte[] b) throws IOException {
		if (b.length <= remaining) {
			stream.write(b);
			remaining -= b.length;
		} else {
			stream.write(b, 0, (int) remaining);
			remaining = 0L;
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (len <= remaining) {
			stream.write(b);
			remaining -= len;
		} else {
			stream.write(b, 0, (int) remaining);
			remaining = 0L;
		}
	}

	@Override
	public void flush() throws IOException {
		stream.flush();
	}
	
	@Override
	public void close() throws IOException {
		stream.close();
	}

}
