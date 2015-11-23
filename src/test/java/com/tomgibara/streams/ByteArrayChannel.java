package com.tomgibara.streams;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

public class ByteArrayChannel implements SeekableByteChannel, ReadableByteChannel, WritableByteChannel {

	private boolean open = true;
	private int position = 0;
	private byte[] bytes;
	
	public ByteArrayChannel(int size) {
		this.bytes = new byte[size];
	}
	
	public ByteArrayChannel(byte[] bytes) {
		if (bytes == null) throw new IllegalArgumentException("null bytes");
		this.bytes = bytes;
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	
	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public void close() {
		open = false;
	}

	@Override
	public int write(ByteBuffer src) throws ClosedChannelException {
		if (!open) throw new ClosedChannelException();
		if (position == bytes.length) return -1;
		int length = Math.min(src.remaining(), bytes.length - position);
		src.get(bytes, position, length);
		position += length;
		return length;
	}

	@Override
	public int read(ByteBuffer dst) throws ClosedChannelException {
		if (!open) throw new ClosedChannelException();
		if (position == bytes.length) return -1;
		int length = Math.min(dst.remaining(), bytes.length - position);
		dst.put(bytes, position, length);
		position += length;
		return length;
	}

	@Override
	public long position() {
		return position;
	}

	@Override
	public ByteArrayChannel position(long newPosition) {
		if (newPosition < 0L) throw new IllegalArgumentException("negative newPosition");
		position = newPosition > bytes.length ? bytes.length : (int) newPosition;
		return this;
	}

	@Override
	public long size() {
		return bytes.length;
	}

	@Override
	public ByteArrayChannel truncate(long size) {
		if (size < bytes.length) {
			bytes = Arrays.copyOf(bytes, (int) size);
			if (position > bytes.length) position = bytes.length;
		}
		return this;
	}

}
