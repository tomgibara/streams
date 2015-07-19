package com.tomgibara.streams;

public abstract class AbstractReadStream implements ReadStream {

	@Override
	public void close() {
		/* do nothing */
	}

	@Override
	public void readBytes(byte[] bs) {
		for (int i = 0; i < bs.length; i++) {
			bs[i] = readByte();
		}
	}

	@Override
	public void readBytes(byte[] bs, int off, int len) {
		int lim = off + len;
		for (int i = off; i < lim; i++) {
			bs[i] = readByte();
		}
	}

	@Override
	public int readInt() {
		return
				 readByte()         << 24 |
				(readByte() & 0xff) << 16 |
				(readByte() & 0xff) <<  8 |
				 readByte() & 0xff;
	}

	@Override
	public boolean readBoolean() {
		return readByte() != 0;
	}

	@Override
	public short readShort() {
		return (short) (readByte() << 8 | readByte() & 0xff);
	}

	@Override
	public long readLong() {
		return
				 (long) readByte()         << 56 |
				((long) readByte() & 0xff) << 48 |
				((long) readByte() & 0xff) << 40 |
				((long) readByte() & 0xff) << 32 |
				((long) readByte() & 0xff) << 24 |
				((long) readByte() & 0xff) << 16 |
				((long) readByte() & 0xff) <<  8 |
				 (long) readByte() & 0xff;
	}

	@Override
	public float readFloat() {
		return Float.intBitsToFloat(readInt());
	}

	@Override
	public double readDouble() {
		return Double.longBitsToDouble(readLong());
	}

	@Override
	public char readChar() {
		return (char) (readByte() << 8 | readByte() & 0xff);
	}

	@Override
	public void readChars(char[] cs) {
		for (int i = 0; i < cs.length; i++) {
			cs[i] = readChar();
		}
	}

	@Override
	public void readChars(char[] cs, int off, int len) {
		int lim = off + len;
		for (int i = off; i < lim; i++) {
			cs[i] = readChar();
		}
	}

	@Override
	public String readChars() {
		char[] cs = new char[readInt()];
		readChars(cs);
		return new String(cs);
	}

}
