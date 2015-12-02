package com.tomgibara.streams;

public class StreamBytes {

	private final boolean safe;
	private byte[] bytes;
	private ByteWriteStream writer;
	private ByteReadStream reader;

	StreamBytes(boolean safe, byte[] bytes) {
		this.safe = safe;
		this.bytes = bytes;
	}

	public WriteStream writer() {
		if (reader != null) {
			reader.close(); //TODO should this do something?
			reader = null;
		}
		if (writer == null) {
			writer = new ByteWriteStream(bytes);
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
