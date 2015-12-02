package com.tomgibara.streams;

public class StreamBytes {

	private final boolean safe;
	private final int maxCapacity;
	private byte[] bytes;
	private ByteWriteStream writer;
	private ByteReadStream reader;

	StreamBytes(boolean safe, byte[] bytes, int maxCapacity) {
		this.safe = safe;
		this.maxCapacity = maxCapacity;
		this.bytes = bytes;
	}
	
	public int getMaxCapacity() {
		return maxCapacity;
	}

	public WriteStream writer() {
		if (reader != null) {
			reader.close(); //TODO should this do something?
			reader = null;
		}
		if (writer == null) {
			writer = new ByteWriteStream(bytes, maxCapacity);
		}
		return writer;
	}
	
	public ReadStream reader() {
		if (writer != null) {
			bytes = writer.getBytes(safe);
			writer = null;
		}
		if (reader == null) {
			reader = new ByteReadStream(bytes);
		}
		return reader;
	}
	
	public byte[] bytes() {
		return bytes;
	}
}
