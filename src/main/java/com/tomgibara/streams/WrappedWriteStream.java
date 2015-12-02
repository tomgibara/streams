package com.tomgibara.streams;

import java.nio.ByteBuffer;

/**
 * Delegates all method calls to a wrapped {@link WriteStream}.
 *
 * @author Tom Gibara
 *
 * @param <S>
 *            the type of {@link WriteStream} being wrapped
 */

public class WrappedWriteStream<S extends WriteStream> implements WriteStream {

	/**
	 * The stream that is being wrapped.
	 */

	protected final S wrapped;

	/**
	 * Creates a new stream that wraps an existing {@link WriteStream}.
	 *
	 * @param wrapped
	 *            the {@link WriteStream} to which all calls should be delegated.
	 */

	public WrappedWriteStream(S wrapped) {
		if (wrapped == null) throw new IllegalArgumentException("null wrapped");
		this.wrapped = wrapped;
	}

	@Override
	public void writeByte(byte v) throws StreamException {
		wrapped.writeByte(v);
	}

	@Override
	public void close() throws StreamException {
		wrapped.close();
	}

	@Override
	public void writeBytes(byte[] bs) throws StreamException {
		wrapped.writeBytes(bs);
	}

	@Override
	public void writeBytes(byte[] bs, int off, int len) throws StreamException {
		wrapped.writeBytes(bs, off, len);
	}

	@Override
	public void writeInt(int v) throws StreamException {
		wrapped.writeInt(v);
	}

	@Override
	public void writeBoolean(boolean v) throws StreamException {
		wrapped.writeBoolean(v);
	}

	@Override
	public void writeShort(short v) throws StreamException {
		wrapped.writeShort(v);
	}

	@Override
	public void writeLong(long v) throws StreamException {
		wrapped.writeLong(v);
	}

	@Override
	public void writeFloat(float v) throws StreamException {
		wrapped.writeFloat(v);
	}

	@Override
	public void writeDouble(double v) throws StreamException {
		wrapped.writeDouble(v);
	}

	@Override
	public void writeChar(char v) throws StreamException {
		wrapped.writeChar(v);
	}

	@Override
	public void writeChars(char[] cs) throws StreamException {
		wrapped.writeChars(cs);
	}

	@Override
	public void writeChars(char[] cs, int off, int len) throws StreamException {
		wrapped.writeChars(cs, off, len);
	}

	@Override
	public void writeChars(CharSequence cs) throws StreamException {
		wrapped.writeChars(cs);
	}
	
	@Override
	public void drainBuffer(ByteBuffer buffer) throws StreamException {
		wrapped.drainBuffer(buffer);
	}

}
