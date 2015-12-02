package com.tomgibara.streams;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Reads values from a {@link ReadableByteChannel}. Any {@link IOException}
 * encountered by this class is wrapped as {@link StreamException} and rethrown.
 * Any end-of-stream condition is signalled with an {@link EndOfStreamException}
 * except when encountered during a call to {@link #fillBuffer(ByteBuffer)}, in
 * that case, an EOS condition is identified by
 * <code>buffer.hasRemaining()</code> returning true. Note that modifying the
 * channel while accessing it via a stream is likely to produce inconsistencies.
 * .
 * 
 * @author Tom Gibara
 *
 * @see EndOfStreamException#EOS
 */

public final class ChannelReadStream implements ReadStream {

	private final ReadableByteChannel channel;
	private final ByteBuffer buffer = ByteBuffer.allocate(8);

	/**
	 * Creates a stream that reads from the supplied channel. Bytes will be read
	 * starting from the current channel position.
	 * 
	 * @param channel
	 *            a byte channel
	 */

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
	public StreamBuffering getBuffering() {
		return StreamBuffering.PREFER_DIRECT;
	}
	
	@Override
	public void fillBuffer(ByteBuffer buffer) throws StreamException {
		try {
			while (buffer.hasRemaining()) {
				int count = channel.read(buffer);
				if (count == -1) return;
			}
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}
	
	/**
	 * Creates a stream which reads from the underlying channel. Bytes will be
	 * read starting from the current channel position. No more than
	 * <i>length</i> bytes may be read from the stream. An 'end of stream'
	 * condition occurs when either the channel is exhausted or when reading
	 * from the channel would exceed the specified length.
	 * 
	 * @param length
	 *            the maximum number of bytes that may be read from the channel
	 */

	@Override
	public ReadStream bounded(long length) {
		return new ChannelReadStream(new BoundedReadableChannel(channel, length));
	}

	@Override
	public ReadableByteChannel asChannel() {
		return channel;
	}

	@Override
	public InputStream asInputStream() {
		return Channels.newInputStream(channel);
	}
	
	/**
	 * Closes the underlying channel.
	 * 
	 * @throws StreamException
	 *             if an {@link IOException} occurs while closing the channel
	 */
	
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
		if (buffer.hasRemaining()) throw EndOfStreamException.EOS;
		buffer.position(position);
		return buffer;
	}

}
