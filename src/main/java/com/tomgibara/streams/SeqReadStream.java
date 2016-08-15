/*
 * Copyright 2016 Tom Gibara
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
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
