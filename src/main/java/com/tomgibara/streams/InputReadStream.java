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

/**
 * Reads values from an {@link InputStream}. Any {@link IOException} encountered
 * by this class is wrapped as {@link StreamException} and rethrown. Any
 * end-of-stream condition is signalled with an {@link EndOfStreamException}.
 *
 * @author Tom Gibara
 *
 * @see EndOfStreamException#EOS
 */

public final class InputReadStream implements ReadStream {

	private final InputStream in;

	private byte[] buffer = null;

	/**
	 * Creates a new stream which obtains bytes data from an underlying
	 * {@link InputStream}
	 *
	 * @param in
	 *            an input stream from which bytes should be read
	 */

	public InputReadStream(InputStream in) {
		if (in == null) throw new IllegalArgumentException("null in");
		this.in = in;
	}

	@Override
	public byte readByte() {
		try {
			int r = in.read();
			if (r < 0) throw EndOfStreamException.EOS;
			return (byte) r;
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public void readBytes(byte[] bs, int off, int len) {
		try {
			while (len > 0) {
				int r = in.read(bs, off, len);
				if (r < 0) throw EndOfStreamException.EOS;
				off += r;
				len -= r;
			}
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

}
