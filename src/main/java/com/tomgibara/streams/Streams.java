package com.tomgibara.streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.MessageDigest;

public final class Streams {

	private static final int DEFAULT_CAPACITY = 32;

	private static byte[] array(int capacity) {
		if (capacity < 1) throw new IllegalArgumentException("capacity non-positive");
		return new byte[capacity];
	}

	public static StreamBytes unsafeBytes() {
		return new StreamBytes(false, new byte[DEFAULT_CAPACITY]);
	}
	
	public static StreamBytes unsafeBytes(int capacity) {
		return new StreamBytes(false, array(capacity));
	}
	
	public static StreamBytes unsafeBytes(byte[] bytes) {
		return new StreamBytes(false, bytes);
	}
	
	public static StreamBytes bytes() {
		return new StreamBytes(true, new byte[DEFAULT_CAPACITY]);
	}
	
	public static StreamBytes bytes(int capacity) {
		return new StreamBytes(true, array(capacity));
	}
	
	public static StreamBytes bytes(byte[] bytes) {
		return new StreamBytes(true, bytes);
	}

	/**
	 * <p>
	 * Creates a stream that reads from the supplied channel. Bytes will be read
	 * starting from the current channel position.
	 * 
	 * <p>
	 * Any {@link IOException} encountered by this class is wrapped as
	 * {@link StreamException} and rethrown. Any end-of-stream condition is
	 * signalled with an {@link EndOfStreamException} except when encountered
	 * during a call to {@link #fillBuffer(ByteBuffer)}, in that case, an EOS
	 * condition is identified by <code>buffer.hasRemaining()</code> returning
	 * true. Note that modifying the channel while accessing it via a stream is
	 * likely to produce inconsistencies.
	 * 
	 * @param channel
	 *            a byte channel
	 *
	 * @see EndOfStreamException#EOS
	 */

	public static ReadStream stream(ReadableByteChannel channel) {
		if (channel == null) throw new IllegalArgumentException("null channel");
		return new ChannelReadStream(channel);
	}
	
	/**
	 * <p>
	 * Creates a stream that writes to the supplied channel. Bytes will be
	 * written starting from the current channel position.
	 *
	 * <p>
	 * Any {@link IOException} encountered by the stream is wrapped as a
	 * {@link StreamException} and rethrown. Any end-of-stream condition is
	 * signalled with an {@link EndOfStreamException} except when encountered
	 * during a call to {@link #fillBuffer(ByteBuffer)}, in that case, an EOS
	 * condition is identified by <code>buffer.hasRemaining()</code> returning
	 * true. Note that modifying the channel while accessing it via a stream is
	 * likely to produce inconsistencies.
	 * 
	 * @param channel
	 *            a byte channel
	 *
	 * @see EndOfStreamException#EOS
	 */

	public static WriteStream stream(WritableByteChannel channel) {
		if (channel == null) throw new IllegalArgumentException("null channel");
		return new ChannelWriteStream(channel);
	}

	/**
	 * <p>
	 * Creates a new stream which obtains bytes data from an underlying
	 * {@link InputStream}
	 * 
	 * <p>
	 * Any {@link IOException} encountered by this class is wrapped as
	 * {@link StreamException} and rethrown. Any end-of-stream condition is
	 * signalled with an {@link EndOfStreamException}.
	 *
	 * @param in
	 *            an input stream from which bytes should be read
	 *
	 * @see EndOfStreamException#EOS
	 */

	public static ReadStream stream(InputStream in) {
		if (in == null) throw new IllegalArgumentException("null in");
		return new InputReadStream(in);
	}

	/**
	 * <p>
	 * Creates a new stream which writes to an underlying {@link OutputStream}.
	 *
	 * <p>
	 * Any {@link IOException} encountered by this class is wrapped as
	 * {@link StreamException} and rethrown.
	 *
	 * @param out
	 *            an output stream to which bytes should be written
	 */

	public static WriteStream stream(OutputStream out) {
		if (out == null) throw new IllegalArgumentException("null out");
		return new OutputWriteStream(out);
	}

	
	/**
	 * Creates a new stream which writes values to the supplied digest. Closing
	 * this stream has no effect.
	 *
	 * @param digest
	 *            digests the resulting byte stream
	 */

	public static WriteStream stream(MessageDigest digest) {
		if (digest == null) throw new IllegalArgumentException("null digest");
		return new DigestWriteStream(digest);
	}

	private Streams() { }

}
