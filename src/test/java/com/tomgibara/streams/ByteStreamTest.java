package com.tomgibara.streams;


public class ByteStreamTest extends FuzzStreamTest {

	@Override
	WriteStream newWriter() { return new ByteWriteStream(); }
	
	@Override
	ReadStream newReader(WriteStream writer) {
		return new ByteReadStream(((ByteWriteStream) writer).getBytes());
	}

}
