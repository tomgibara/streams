package com.tomgibara.streams;

public interface CloseableStream extends AutoCloseable {

	@Override
	public void close();
	
}
