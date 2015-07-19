package com.tomgibara.streams;

import java.io.IOException;
import java.io.InputStream;

public final class InputReadStream extends AbstractReadStream {

	private final InputStream in;

	private byte[] buffer = null;
	
	public InputReadStream(InputStream in) {
		if (in == null) throw new IllegalArgumentException("null in");
		this.in = in;
	}
	
	@Override
	public byte readByte() {
		try {
			int r = in.read();
			if (r < 0) throw StreamException.EOS;
			return (byte) r;
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}

	@Override
	public void readBytes(byte[] bs, int off, int len) {
		try {
			while (len > 0) {
				int r = in.read(bs, off, len);
				if (r < 0) throw StreamException.EOS;
				off += r;
				len -= r;
			}
		} catch (IOException e) {
			throw new StreamException(e);
		}
	}
	
	@Override
	public int readInt() {
		if (buffer == null) buffer = new byte[8];
		readBytes(buffer, 0, 4);
		return
				 buffer[0]         << 24 |
				(buffer[1] & 0xff) << 16 |
				(buffer[2] & 0xff) <<  8 |
				 buffer[3] & 0xff;
	}
	
	@Override
	public long readLong() {
		if (buffer == null) buffer = new byte[8];
		readBytes(buffer, 0, 8);
		return
				 (long) buffer[0]         << 56 |
				((long) buffer[1] & 0xff) << 48 |
				((long) buffer[2] & 0xff) << 40 |
				((long) buffer[3] & 0xff) << 32 |
				((long) buffer[4] & 0xff) << 24 |
				(       buffer[5] & 0xff) << 16 |
				(       buffer[6] & 0xff) <<  8 |
				        buffer[7] & 0xff;
	}

}
