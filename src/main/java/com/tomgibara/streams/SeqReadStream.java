package com.tomgibara.streams;

import java.nio.ByteBuffer;

final class SeqReadStream implements ReadStream {

	private final StreamCloser closer;
	private final ReadStream[] streams;
	private ReadStream stream;
	private int index = 0;

	SeqReadStream(StreamCloser closer, ReadStream... streams) {
		this.closer = closer;
		this.streams = streams;
		stream = streams.length == 0 ? null : streams[0];
	}

	@Override
	public byte readByte() {
		while (stream != null) {
			try {
				return stream.readByte();
			} catch (EndOfStreamException e) {
				advance();
			}
		}
		EndOfStreamException.raise();
		throw new IllegalStateException();
	}

	@Override
	public void readBytes(byte[] bs) {
		ByteBuffer buffer = ByteBuffer.wrap(bs);
		fillBuffer(buffer);
		if (buffer.hasRemaining()) EndOfStreamException.raise();
	}

	@Override
	public void readBytes(byte[] bs, int off, int len) {
		ByteBuffer buffer = ByteBuffer.wrap(bs, off, len);
		fillBuffer(buffer);
		if (buffer.hasRemaining()) EndOfStreamException.raise();
	}

	@Override
	public void fillBuffer(ByteBuffer buffer) {
		while (stream != null) {
			stream.fillBuffer(buffer);
			if (!buffer.hasRemaining()) return;
			advance();
		}
		EndOfStreamException.raise();
	}

	@Override
	public void close() {
		while (stream != null) advance();
		if (index > 0) {
			streams[streams.length - 1].close();
			index = -1;
		}
	}

	private void advance() {
		index ++;
		if (index == streams.length) {
			stream = null;
		} else {
			closer.close(stream);
			stream = streams[index];
		}
	}
}
