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

public final class OutputWriteStream extends AbstractWriteStream {

	private final OutputStream out;

	public OutputWriteStream(OutputStream out) {
		if (out == null) throw new IllegalArgumentException("null out");
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
