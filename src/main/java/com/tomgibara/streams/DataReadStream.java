/*
 * Copyright 2018 Tom Gibara
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

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

final class DataReadStream implements ReadStream {

	private final DataInput in;

	DataReadStream(DataInput in) {
		this.in = in;
	}

	@Override
	public byte readByte() throws StreamException {
		try {
			return in.readByte();
		} catch (EOFException e) {
			throw EndOfStreamException.instance();
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public void readBytes(byte[] bs, int off, int len) throws StreamException {
		try {
			in.readFully(bs, off, len);
		} catch (EOFException e) {
			throw EndOfStreamException.instance();
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public void readBytes(byte[] bs) throws StreamException {
		try {
			in.readFully(bs);
		} catch (EOFException e) {
			throw EndOfStreamException.instance();
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public short readShort() throws StreamException {
		try {
			return in.readShort();
		} catch (EOFException e) {
			throw EndOfStreamException.instance();
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public int readInt() throws StreamException {
		try {
			return in.readInt();
		} catch (EOFException e) {
			throw EndOfStreamException.instance();
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public long readLong() throws StreamException {
		try {
			return in.readLong();
		} catch (EOFException e) {
			throw EndOfStreamException.instance();
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public boolean readBoolean() throws StreamException {
		try {
			return in.readBoolean();
		} catch (EOFException e) {
			throw EndOfStreamException.instance();
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public char readChar() throws StreamException {
		try {
			return in.readChar();
		} catch (EOFException e) {
			throw EndOfStreamException.instance();
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public float readFloat() throws StreamException {
		try {
			return in.readFloat();
		} catch (EOFException e) {
			throw EndOfStreamException.instance();
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public double readDouble() throws StreamException {
		try {
			return in.readDouble();
		} catch (EOFException e) {
			throw EndOfStreamException.instance();
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public InputStream asInputStream() {
		if (in instanceof DataInputStream) {
			return (DataInputStream) in;
		}
		return ReadStream.super.asInputStream();
	}

	@Override
	public StreamBuffering getBuffering() {
		return StreamBuffering.PREFER_INDIRECT;
	}

	@Override
	public void close() {
		if (in instanceof Closeable) {
			try {
				((Closeable) in).close();
			} catch (IOException e) {
				throw new StreamException(e);
			}
		}
	}
}
