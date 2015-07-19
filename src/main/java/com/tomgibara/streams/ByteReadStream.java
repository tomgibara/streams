package com.tomgibara.streams;

public final class ByteReadStream extends AbstractReadStream {

	private final byte[] bytes;
	private final int limit;
	private int position;

	public ByteReadStream(byte[] bytes) {
		if (bytes == null) throw new IllegalArgumentException("null bytes");
		this.bytes = bytes;
		this.limit = bytes.length;
		this.position = 0;
	}

	public ByteReadStream(byte[] bytes, int off, int len) {
		if (bytes == null) throw new IllegalArgumentException("null bytes");
		if (off < 0) throw new IllegalArgumentException("negative off");
		int length = bytes.length;
		if (off > length) throw new IllegalArgumentException("off exceeds length");
		if (len < 0) throw new IllegalArgumentException("negative len");
		int limit = off + len;
		if (limit > length) throw new IllegalArgumentException("off + len exceeds length");
		this.bytes = bytes;
		this.position = off;
		this.limit = limit;
	}

	@Override
	public byte readByte() {
		requireBytes(1);
		return bytes[position++];
	}

	@Override
	public void readBytes(byte[] bs) {
		int len = bs.length;
		requireBytes(len);
		System.arraycopy(bytes, position, bs, 0, len);
		position += len;
	}

	@Override
	public void readBytes(byte[] bs, int off, int len) {
		requireBytes(len);
		System.arraycopy(bytes, position, bs, off, len);
		position += len;
	}

	@Override
	public char readChar() {
		requireBytes(2);
		byte b0 = bytes[position++];
		byte b1 = bytes[position++];
		return (char) (b0 << 8 | b1 & 0xff);
	}

	@Override
	public short readShort() {
		requireBytes(2);
		byte b0 = bytes[position++];
		byte b1 = bytes[position++];
		return (short) (b0 << 8 | b1 & 0xff);
	}

	@Override
	public int readInt() {
		requireBytes(4);
		byte b0 = bytes[position++];
		byte b1 = bytes[position++];
		byte b2 = bytes[position++];
		byte b3 = bytes[position++];
		return
				 b0         << 24 |
				(b1 & 0xff) << 16 |
				(b2 & 0xff) <<  8 |
				(b3 & 0xff);
	}

	@Override
	public long readLong() {
		requireBytes(8);
		byte b0 = bytes[position++];
		byte b1 = bytes[position++];
		byte b2 = bytes[position++];
		byte b3 = bytes[position++];
		byte b4 = bytes[position++];
		byte b5 = bytes[position++];
		byte b6 = bytes[position++];
		byte b7 = bytes[position++];
		return
				 (long) b0          << 56 |
				(       b1 & 0xffL) << 48 |
				(       b2 & 0xffL) << 40 |
				(       b3 & 0xffL) << 32 |
				(       b4 & 0xffL) << 24 |
				       (b5 & 0xff ) << 16 |
				       (b6 & 0xff ) <<  8 |
				        b7 & 0xff         ;
	}

	@Override
	public void readChars(char[] cs, int off, int len) {
		requireBytes(len * 2);
		for (int i = 0; i < len; i++) {
			byte b0 = bytes[position++];
			byte b1 = bytes[position++];
			cs[off + i] = (char) (b0 << 8 | b1);
		}
	}

	private void requireBytes(int count) {
		if (position + count > limit) throw StreamException.EOS;
	}
}
