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
import java.io.OutputStream;

/**
 * Writes values to an {@link OutputStream}. Any {@link IOException} encountered
 * by this class is wrapped as {@link StreamException} and rethrown.
 *
 * @author Tom Gibara
 *
 */

final class OutputWriteStream implements WriteStream {

	private final OutputStream out;

	/**
	 * Creates a new stream which writes to an underlying {@link OutputStream}.
	 *
	 * @param out
	 *            an output stream to which bytes should be written
	 */

	OutputWriteStream(OutputStream out) {
		this.out = out;
	}

	@Override
	public void writeByte(byte v) {
		try {
			out.write(v);
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public void writeBytes(byte[] bs, int off, int len) {
		try {
			out.write(bs, off, len);
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public void writeChar(char v) {
		try {
			out.write(v >> 8);
			out.write(v     );
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public void writeShort(short v) {
		try {
			out.write(v >> 8);
			out.write(v     );
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public void writeInt(int v) {
		try {
			out.write(v >> 24);
			out.write(v >> 16);
			out.write(v >>  8);
			out.write(v      );
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public void writeLong(long v) {
		try {
			out.write((int) (v >> 56));
			out.write((int) (v >> 48));
			out.write((int) (v >> 40));
			out.write((int) (v >> 32));
			out.write((int) (v >> 24));
			out.write((int) (v >> 16));
			out.write((int) (v >>  8));
			out.write((int) (v      ));
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public WriteStream bounded(long length) {
		return new OutputWriteStream(new BoundedOutputStream(out, length));
	}
	
	/**
	 * Returns the underlying output stream
	 * 
	 * @return the output stream
	 */

	@Override
	public OutputStream asOutputStream() {
		return out;
	}
	
	/**
	 * Closes the underlying {@link OutputStream}.
	 */

	@Override
	public void close() {
		try {
			out.close();
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

}
