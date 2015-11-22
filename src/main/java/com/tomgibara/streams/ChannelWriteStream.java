package com.tomgibara.streams;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class ChannelWriteStream implements WriteStream {

	private final WritableByteChannel channel;
	private final ByteBuffer buffer = ByteBuffer.allocate(8);

	public ChannelWriteStream(WritableByteChannel channel) {
		this.channel = channel;
	}
	
	@Override
	public void writeByte(byte v) throws StreamException {
		drainBuffer( buffer.put(v) );
	}
	
	@Override
	public void writeBytes(byte[] bs) throws StreamException {
		drainBuffer(ByteBuffer.wrap(bs));
	}
	
	@Override
	public void writeBytes(byte[] bs, int off, int len) throws StreamException {
		drainBuffer(ByteBuffer.wrap(bs, off, len));
	}
	
	@Override
	public void writeInt(int v) throws StreamException {
		drainBuffer( buffer.putInt(v) );
	}
	
	@Override
	public void writeBoolean(boolean v) throws StreamException {
		drainBuffer( buffer.put(v ? (byte) 1 : (byte) 0) );
	}
	
	@Override
	public void writeShort(short v) throws StreamException {
		drainBuffer( buffer.putShort(v) );
	}
	
	@Override
	public void writeLong(long v) throws StreamException {
		drainBuffer( buffer.putLong(v) );
	}
	
	@Override
	public void writeFloat(float v) throws StreamException {
		drainBuffer( buffer.putFloat(v) );
	}
	
	@Override
	public void writeDouble(double v) throws StreamException {
		drainBuffer( buffer.putDouble(v) );
	}
	
	@Override
	public void writeChar(char v) throws StreamException {
		drainBuffer( buffer.putChar(v) );
	}
	
	@Override
	public void drainBuffer(ByteBuffer buffer) throws StreamException {
		try {
			while (buffer.hasRemaining()) {
				int count = channel.write(buffer);
				if (count == -1) throw EndOfStreamException.EOS;
			}
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}
	
	private void write(ByteBuffer buffer) {
		buffer.flip();
		drainBuffer(buffer);
		buffer.clear();
	}

}
