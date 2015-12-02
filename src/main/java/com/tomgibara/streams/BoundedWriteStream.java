package com.tomgibara.streams;

import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;

class BoundedWriteStream implements WriteStream {

	private final WriteStream stream;
	private long remaining;
	
	BoundedWriteStream(WriteStream stream, long remaining) {
		if (stream == null) throw new IllegalArgumentException("null stream");
		if (remaining < 0L) throw new IllegalArgumentException("negative remaining");
		this.stream = stream;
		this.remaining = remaining;
	}
	
	@Override
	public void writeByte(byte v) throws StreamException {
		if (remaining <= 0) throw EndOfStreamException.EOS;
		stream.writeByte(v);
		remaining--;
	}

	@Override
	public WriteStream bounded(long length) {
		if (length < 0L) throw new IllegalArgumentException("negative length");
		return length < remaining ? new BoundedWriteStream(stream, length) : this;
	}

	@Override
	public OutputStream asOutputStream() {
		return new BoundedOutputStream(stream.asOutputStream(), remaining);
	}

	@Override
	public WritableByteChannel asChannel() {
		return new BoundedWritableChannel(stream.asChannel(), remaining);
	}

	@Override
	public void close() throws StreamException {
		stream.close();
	}

}
