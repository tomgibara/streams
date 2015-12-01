package com.tomgibara.streams;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/**
 * Reads a limited number of values from a {@link ReadableByteChannel}. Any
 * {@link IOException} encountered by this class is wrapped as
 * {@link StreamException} and rethrown. Any end-of-stream condition is
 * signalled with an {@link EndOfStreamException} except when encountered during
 * a call to {@link #fillBuffer(ByteBuffer)}, in that case, an EOS condition is
 * identified by <code>buffer.hasRemaining()</code> returning true. Note that
 * modifying the channel while accessing it via a stream is likely to produce
 * inconsistencies.
 * 
 * @author Tom Gibara
 *
 * @see EndOfStreamException#EOS
 */

public final class BoundedChannelReadStream extends AbstractChannelReadStream {

	/**
	 * Creates a stream which reads from the supplied channel. Bytes will be
	 * read starting from the current channel position. No more than
	 * <i>length</i> bytes may be read from the stream. An 'end of stream'
	 * condition occurs when either the channel is exhausted or when reading
	 * from the channel would exceed the specified length.
	 * 
	 * @param channel
	 *            a byte channel
	 * @param length
	 *            the maximum number of bytes that may be read from the channel
	 */
	public BoundedChannelReadStream(ReadableByteChannel channel, long length) {
		super(new BoundedReadableChannel(channel, length));
	}
	
}
