package com.tomgibara.streams;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

/**
 * Writes values to a {@link WritableByteChannel}. Any {@link IOException}
 * encountered by this class is wrapped as a {@link StreamException} and
 * rethrown.
 * 
 * @author Tom Gibara
 *
 */

//TODO eliminate
abstract class AbstractChannelWriteStream implements WriteStream {

	final WritableByteChannel channel;
	private final ByteBuffer buffer = ByteBuffer.allocate(8);

	public AbstractChannelWriteStream(WritableByteChannel channel) {
		this.channel = channel;
	}
	
	@Override
	public void writeByte(byte v) throws StreamException {
		write( buffer.put(v) );
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
		write( buffer.putInt(v) );
	}
	
	@Override
	public void writeBoolean(boolean v) throws StreamException {
		write( buffer.put(v ? (byte) 1 : (byte) 0) );
	}
	
	@Override
	public void writeShort(short v) throws StreamException {
		write( buffer.putShort(v) );
	}
	
	@Override
	public void writeLong(long v) throws StreamException {
		write( buffer.putLong(v) );
	}
	
	@Override
	public void writeFloat(float v) throws StreamException {
		write( buffer.putFloat(v) );
	}
	
	@Override
	public void writeDouble(double v) throws StreamException {
		write( buffer.putDouble(v) );
	}
	
	@Override
	public void writeChar(char v) throws StreamException {
		write( buffer.putChar(v) );
	}
	
	@Override
	public void drainBuffer(ByteBuffer buffer) throws StreamException {
		try {
			while (buffer.hasRemaining()) {
				int count = channel.write(buffer);
				if (count == -1) return;
			}
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public OutputStream asOutputStream() {
		return Channels.newOutputStream(channel);
	}
	
	@Override
	public WritableByteChannel asChannel() {
		return channel;
	}
	
	/**
	 * Closes the underlying channel.
	 * 
	 * @throws StreamException
	 *             if an {@link IOException} occurs while closing the channel
	 */
	
	@Override
	public void close() throws StreamException {
		try {
			channel.close();
		} catch (IOException e) {
			throw new StreamException();
		}
	}
	
	private void write(ByteBuffer buffer) {
		buffer.flip();
		drainBuffer(buffer);
		boolean eos = buffer.hasRemaining();
		buffer.clear();
		if (eos) throw EndOfStreamException.EOS;
	}

}
