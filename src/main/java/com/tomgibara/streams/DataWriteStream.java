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
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;

final class DataWriteStream implements WriteStream {

	private final DataOutput out;

	DataWriteStream(DataOutput out) {
		this.out = out;
	}

	@Override
	public void writeByte(byte v) throws StreamException {
		try {
			out.writeByte(v);
		} catch (EOFException e) {
			throw EndOfStreamException.instance();
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public void writeShort(short v) throws StreamException {
		try {
			out.writeShort(v);
		} catch (EOFException e) {
			throw EndOfStreamException.instance();
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public void writeInt(int v) throws StreamException {
		try {
			out.writeInt(v);
		} catch (EOFException e) {
			throw EndOfStreamException.instance();
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public void writeLong(long v) throws StreamException {
		try {
			out.writeLong(v);
		} catch (EOFException e) {
			throw EndOfStreamException.instance();
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public void writeBoolean(boolean v) throws StreamException {
		try {
			out.writeBoolean(v);
		} catch (EOFException e) {
			throw EndOfStreamException.instance();
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public void writeChar(char v) throws StreamException {
		try {
			out.writeChar(v);
		} catch (EOFException e) {
			throw EndOfStreamException.instance();
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public void writeFloat(float v) throws StreamException {
		try {
			out.writeFloat(v);
		} catch (EOFException e) {
			throw EndOfStreamException.instance();
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public void writeDouble(double v) throws StreamException {
		try {
			out.writeDouble(v);
		} catch (EOFException e) {
			throw EndOfStreamException.instance();
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public void writeBytes(byte[] bs) throws StreamException {
		try {
			out.write(bs);
		} catch (EOFException e) {
			throw EndOfStreamException.instance();
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public void writeBytes(byte[] bs, int off, int len) throws StreamException {
		try {
			out.write(bs, off, len);
		} catch (EOFException e) {
			throw EndOfStreamException.instance();
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public OutputStream asOutputStream() {
		if (out instanceof DataOutputStream) {
			return (DataOutputStream) out;
		}
		return WriteStream.super.asOutputStream();
	}

	@Override
	public StreamBuffering getBuffering() {
		return StreamBuffering.PREFER_INDIRECT;
	}

	@Override
	public void close() {
		if (out instanceof Closeable) {
			try {
				((Closeable) out).close();
			} catch (IOException e) {
				throw new StreamException(e);
			}
		}
	}
}
