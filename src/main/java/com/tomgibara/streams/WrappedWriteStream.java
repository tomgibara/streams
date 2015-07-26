package com.tomgibara.streams;

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
	
	public void writeByte(byte v) throws StreamException {
		wrapped.writeByte(v);
	}

	public void close() throws StreamException {
		wrapped.close();
	}

	public void writeBytes(byte[] bs) throws StreamException {
		wrapped.writeBytes(bs);
	}

	public void writeBytes(byte[] bs, int off, int len) throws StreamException {
		wrapped.writeBytes(bs, off, len);
	}

	public void writeInt(int v) throws StreamException {
		wrapped.writeInt(v);
	}

	public void writeBoolean(boolean v) throws StreamException {
		wrapped.writeBoolean(v);
	}

	public void writeShort(short v) throws StreamException {
		wrapped.writeShort(v);
	}

	public void writeLong(long v) throws StreamException {
		wrapped.writeLong(v);
	}

	public void writeFloat(float v) throws StreamException {
		wrapped.writeFloat(v);
	}

	public void writeDouble(double v) throws StreamException {
		wrapped.writeDouble(v);
	}

	public void writeChar(char v) throws StreamException {
		wrapped.writeChar(v);
	}

	public void writeChars(char[] cs) throws StreamException {
		wrapped.writeChars(cs);
	}

	public void writeChars(char[] cs, int off, int len) throws StreamException {
		wrapped.writeChars(cs, off, len);
	}

	public void writeChars(CharSequence cs) throws StreamException {
		wrapped.writeChars(cs);
	}

}
