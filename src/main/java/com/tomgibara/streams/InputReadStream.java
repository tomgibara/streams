/*
 * Copyright 2015 Tom Gibara
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Reads values from an {@link InputStream}. Any {@link IOException} encountered
 * by this class is wrapped as {@link StreamException} and rethrown. Any
 * end-of-stream condition is signalled with an {@link EndOfStreamException}.
 *
 * @author Tom Gibara
 *
 * @see EndOfStreamException#EOS
 */

final class InputReadStream implements ReadStream {

	private final InputStream in;

	private byte[] buffer = null;

	/**
	 * Creates a new stream which obtains bytes data from an underlying
	 * {@link InputStream}
	 *
	 * @param in
	 *            an input stream from which bytes should be read
	 */

	InputReadStream(InputStream in) {
		this.in = in;
	}

	@Override
	public byte readByte() {
		try {
			int r = in.read();
			if (r < 0) EndOfStreamException.raise();
			return (byte) r;
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public void readBytes(byte[] bs, int off, int len) {
		try {
			readFully(bs, off, len);
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public int readInt() {
		if (buffer == null) buffer = new byte[8];
		readBytes(buffer, 0, 4);
		return
				 buffer[0]         << 24 |
				(buffer[1] & 0xff) << 16 |
				(buffer[2] & 0xff) <<  8 |
				 buffer[3] & 0xff;
	}

	@Override
	public long readLong() {
		if (buffer == null) buffer = new byte[8];
		readBytes(buffer, 0, 8);
		return
				 (long) buffer[0]         << 56 |
				((long) buffer[1] & 0xff) << 48 |
				((long) buffer[2] & 0xff) << 40 |
				((long) buffer[3] & 0xff) << 32 |
				((long) buffer[4] & 0xff) << 24 |
				(       buffer[5] & 0xff) << 16 |
				(       buffer[6] & 0xff) <<  8 |
				        buffer[7] & 0xff;
	}

	@Override
	public void fillBuffer(ByteBuffer buffer) throws StreamException {
		if (!buffer.hasArray()) {
			ReadStream.super.fillBuffer(buffer);
		} else {
			try {
				int offset = buffer.arrayOffset();
				byte[] array = buffer.array();
				while (buffer.hasRemaining()) {
					int available = in.available();
					if (available <= 0) {
						int b = in.read();
						if (b == -1) return; //EOS
						buffer.put((byte) b);
					} else {
						int position = Math.min(available, buffer.remaining());
						readFully(array, offset + buffer.position(), offset + position);
						buffer.position(position);
					}
				}
			} catch (IOException e) {
				throw new StreamException(e);
			} catch (EndOfStreamException e) {
				// swallowed - unfilled buffer indicates EOS
				return;
			}
		}
	}

	@Override
	public ReadStream bounded(long length) {
		return new InputReadStream(new BoundedInputStream(in, length));
	}

	/**
	 * Returns the underlying input stream.
	 *
	 * @return the input stream
	 */

	@Override
	public InputStream asInputStream() {
		return in;
	}

	/**
	 * Closes the underlying {@link InputStream}.
	 */

	@Override
	public void close() {
		try {
			in.close();
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public StreamBuffering getBuffering() {
		return StreamBuffering.PREFER_INDIRECT;
	}

	private void readFully(byte[] bs, int off, int len) throws IOException {
		while (len > 0) {
			int r = in.read(bs, off, len);
			if (r < 0) EndOfStreamException.raise();
			off += r;
			len -= r;
		}
	}

}
