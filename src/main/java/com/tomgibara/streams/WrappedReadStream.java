package com.tomgibara.streams;

import java.nio.ByteBuffer;

/**
 * Delegates all method calls to a wrapped {@link ReadStream}.
 *
 * @author Tom Gibara
 *
 * @param <S>
 *            the type of {@link ReadStream} being wrapped
 */

public class WrappedReadStream<S extends ReadStream> implements ReadStream {

	/**
	 * The stream that is being wrapped.
	 */

	protected final S wrapped;

	/**
	 * Creates a new stream that wraps an existing {@link ReadStream}.
	 *
	 * @param wrapped
	 *            the {@link ReadStream} to which all calls should be delegated.
	 */

	public WrappedReadStream(S wrapped) {
		if (wrapped == null) throw new IllegalArgumentException("null wrapped");
		this.wrapped = wrapped;
	}

	public void close() throws StreamException {
		wrapped.close();
	}

	public byte readByte() throws StreamException {
		return wrapped.readByte();
	}

	public void readBytes(byte[] bs) throws StreamException {
		wrapped.readBytes(bs);
	}

	public void readBytes(byte[] bs, int off, int len) throws StreamException {
		wrapped.readBytes(bs, off, len);
	}

	public int readInt() throws StreamException {
		return wrapped.readInt();
	}

	public boolean readBoolean() throws StreamException {
		return wrapped.readBoolean();
	}

	public short readShort() throws StreamException {
		return wrapped.readShort();
	}

	public long readLong() throws StreamException {
		return wrapped.readLong();
	}

	public float readFloat() throws StreamException {
		return wrapped.readFloat();
	}

	public double readDouble() throws StreamException {
		return wrapped.readDouble();
	}

	public char readChar() throws StreamException {
		return wrapped.readChar();
	}

	public void readChars(char[] cs) throws StreamException {
		wrapped.readChars(cs);
	}

	public void readChars(char[] cs, int off, int len) throws StreamException {
		wrapped.readChars(cs, off, len);
	}

	public String readChars() throws StreamException {
		return wrapped.readChars();
	}
	
	@Override
	public void fillBuffer(ByteBuffer buffer) throws StreamException {
		wrapped.fillBuffer(buffer);
	}
}
