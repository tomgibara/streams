package com.tomgibara.streams;

import java.nio.ByteBuffer;

final class SeqWriteStream implements WriteStream {

	private final StreamCloser closer;
	private final WriteStream[] streams;
	private WriteStream stream;
	private int index = 0;

	SeqWriteStream(StreamCloser closer, WriteStream... streams) {
		this.closer = closer;
		this.streams = streams;
		stream = streams.length == 0 ? null : streams[0];
	}

	@Override
	public void writeByte(byte v) {
		while (stream != null) {
			try {
				stream.writeByte(v);
				return;
			} catch (EndOfStreamException e) {
				advance();
			}
		}
		EndOfStreamException.raise();
	}

	@Override
	public void writeBytes(byte[] bs) {
		ByteBuffer buffer = ByteBuffer.wrap(bs);
		drainBuffer(buffer);
		if (buffer.hasRemaining()) EndOfStreamException.raise();
	}

	@Override
	public void writeBytes(byte[] bs, int off, int len) {
		ByteBuffer buffer = ByteBuffer.wrap(bs, off, len);
		drainBuffer(buffer);
		if (buffer.hasRemaining()) EndOfStreamException.raise();
	}

	@Override
	public void drainBuffer(ByteBuffer buffer) {
		while (stream != null) {
			stream.drainBuffer(buffer);
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
