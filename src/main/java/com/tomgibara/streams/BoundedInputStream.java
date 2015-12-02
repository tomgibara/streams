package com.tomgibara.streams;

import java.io.IOException;
import java.io.InputStream;

class BoundedInputStream extends InputStream {

	private final InputStream stream;
	private long remaining;

	BoundedInputStream(InputStream stream, long length) {
		if (stream == null) throw new IllegalArgumentException("null stream");
		if (length < 0L) throw new IllegalArgumentException("negative length");
		this.stream = stream;
		this.remaining = length;
	}
	
	@Override
	public int read() throws IOException {
		if (remaining <= 0L) return -1;
		int value = stream.read();
		remaining--;
		return value;
	}

	@Override
	public int read(byte[] b) throws IOException {
		int read;
		if (b.length <= remaining) {
			read = stream.read(b);
		} else {
			read = stream.read(b, 0, (int) remaining);
		}
		remaining -= read;
		return read;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int read;
		if (len <= remaining) {
			read = stream.read(b, off, len);
		} else {
			read = stream.read(b, off, (int) remaining);
		}
		remaining -= read;
		return read;
	}

	@Override
	public long skip(long n) throws IOException {
		return stream.skip(Math.min(n, remaining));
	}

	@Override
	public int available() throws IOException {
		int a = stream.available();
		if (remaining < a) return (int) remaining;
		return a;
	}

	@Override
	public boolean markSupported() {
		return stream.markSupported();
	}

	@Override
	public synchronized void mark(int readlimit) {
		if (remaining <= readlimit) {
			stream.mark(readlimit);
		} else {
			stream.mark((int) remaining);
		}
	}

	@Override
	public synchronized void reset() throws IOException {
		stream.reset();
	}

	@Override
	public void close() throws IOException {
		stream.close();
	}
}
