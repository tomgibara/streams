package com.tomgibara.streams;

import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

//TODO optimize further?
class BoundedReadStream implements ReadStream {

	private final ReadStream stream;
	private long remaining;

	BoundedReadStream(ReadStream stream, long remaining) {
		if (stream == null) throw new IllegalArgumentException("null stream");
		if (remaining < 0L) throw new IllegalArgumentException("negative remaining");
		this.stream = stream;
		this.remaining = remaining;
	}
	
	@Override
	public byte readByte() throws StreamException {
		if (remaining <= 0) throw EndOfStreamException.EOS;
		byte value = stream.readByte();
		remaining--;
		return value;
	}

	@Override
	public ReadStream bounded(long length) {
		if (length < 0L) throw new IllegalArgumentException("negative length");
		return length < remaining ? new BoundedReadStream(stream, length) : this;
	}

	@Override
	public InputStream asInputStream() {
		return new BoundedInputStream(stream.asInputStream(), remaining); 
	}
	
	@Override
	public ReadableByteChannel asChannel() {
		return new BoundedReadableChannel(stream.asChannel(), remaining);
	}
	
	@Override
	public void close() {
		stream.close();
	}

}
