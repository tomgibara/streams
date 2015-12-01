package com.tomgibara.streams;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * Writes a limited number of values to a {@link WritableByteChannel}. Any
 * {@link IOException} encountered by this class is wrapped as a
 * {@link StreamException} and rethrown. Any end-of-stream condition is
 * signalled with an {@link EndOfStreamException} except when encountered during
 * a call to {@link #fillBuffer(ByteBuffer)}, in that case, an EOS condition is
 * identified by <code>buffer.hasRemaining()</code> returning true. Note that
 * modifying the channel while accessing it via a stream is likely to produce
 * inconsistencies.
 * 
 * 
 * @author Tom Gibara
 *
 * @see EndOfStreamException#EOS
 */

public class BoundedChannelWriteStream extends AbstractChannelWriteStream {

	/**
	 * Creates a stream that writes to the supplied channel. Bytes will be
	 * written starting from the current channel position. No more than
	 * <i>length</i> bytes may be written to the stream. An 'end of stream'
	 * condition occurs when either the channel is full or when writing to the
	 * channel would exceed the specified length.
	 * 
	 * @param channel
	 *            a byte channel
	 * @param length
	 *            the maximum number of bytes that may be written to the channel
	 */

	public BoundedChannelWriteStream(WritableByteChannel channel, long length) {
		super(new BoundedWritableChannel(channel, length));
	}

}
