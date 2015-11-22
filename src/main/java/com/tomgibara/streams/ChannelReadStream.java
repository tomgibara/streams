package com.tomgibara.streams;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public final class ChannelReadStream implements ReadStream {

	private final ReadableByteChannel channel;
	private final ByteBuffer buffer = ByteBuffer.allocate(8);

	public ChannelReadStream(ReadableByteChannel channel) {
		if (channel == null) throw new IllegalArgumentException("null channel");
		this.channel = channel;
	}
	
	@Override
	public byte readByte() throws StreamException {
		return read(buffer, 7).get();
	}
	
	@Override
	public void readBytes(byte[] bs) throws StreamException {
		fillBuffer(ByteBuffer.wrap(bs));
	}
	
	@Override
	public void readBytes(byte[] bs, int off, int len) throws StreamException {
		fillBuffer(ByteBuffer.wrap(bs, off, len));
	}
	
	@Override
	public int readInt() throws StreamException {
		return read(buffer, 4).getInt();
	}
	
	@Override
	public boolean readBoolean() throws StreamException {
		return read(buffer, 7).get() != 0;
	}
	
	@Override
	public short readShort() throws StreamException {
		return read(buffer, 6).getShort();
	}
	
	@Override
	public long readLong() throws StreamException {
		return read(buffer, 0).getLong();
	}
	
	@Override
	public float readFloat() throws StreamException {
		return read(buffer, 4).getFloat();
	}

	@Override
	public double readDouble() throws StreamException {
		return read(buffer, 0).getDouble();
	}
	
	@Override
	public char readChar() throws StreamException {
		return read(buffer, 6).getChar();
	}
	
	@Override
	public void fillBuffer(ByteBuffer buffer) throws StreamException {
		try {
			while (buffer.hasRemaining()) {
				int count = channel.read(buffer);
				if (count == -1) throw EndOfStreamException.EOS;
			}
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}
	
	@Override
	public void close() {
		try {
			channel.close();
		} catch (IOException e) {
			throw new StreamException();
		}
	}

	private ByteBuffer read(ByteBuffer buffer, int position) {
		buffer.position(position);
		fillBuffer(buffer);
		buffer.position(position);
		return buffer;
	}

}
