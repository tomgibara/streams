/*
 * Copyright 2015 Tom Gibara
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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

/**
 * Writes values to a {@link WritableByteChannel}. Any {@link IOException}
 * encountered by this class is wrapped as a {@link StreamException} and
 * rethrown. Any end-of-stream condition is signalled with an
 * {@link EndOfStreamException} except when encountered during a call to
 * {@link #fillBuffer(ByteBuffer)}, in that case, an EOS condition is identified
 * by <code>buffer.hasRemaining()</code> returning true. Note that modifying the
 * channel while accessing it via a stream is likely to produce inconsistencies.
 *
 *
 * @author Tom Gibara
 *
 * @see EndOfStreamException#EOS
 */

final class ChannelWriteStream implements WriteStream {

	private final WritableByteChannel channel;
	private final ByteBuffer buffer = ByteBuffer.allocate(8);

	/**
	 * Creates a stream that writes to the supplied channel. Bytes will be
	 * written starting from the current channel position.
	 *
	 * @param channel
	 *            a byte channel
	 */

	ChannelWriteStream(WritableByteChannel channel) {
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

	/**
	 * Creates a stream that writes to the underlying channel. Bytes will be
	 * written starting from the current channel position. No more than
	 * <i>length</i> bytes may be written to the stream. An 'end of stream'
	 * condition occurs when either the channel is full or when writing to the
	 * channel would exceed the specified length.
	 *
	 * @param length
	 *            the maximum number of bytes that may be written to the channel
	 */

	@Override
	public WriteStream bounded(long length) {
		return new ChannelWriteStream(new BoundedWritableChannel(channel, length));
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

	@Override
	public StreamBuffering getBuffering() {
		return StreamBuffering.PREFER_DIRECT;
	}

	private void write(ByteBuffer buffer) {
		buffer.flip();
		drainBuffer(buffer);
		boolean eos = buffer.hasRemaining();
		buffer.clear();
		if (eos) throw EndOfStreamException.instance();
	}

}
