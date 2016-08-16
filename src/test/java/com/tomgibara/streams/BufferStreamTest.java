package com.tomgibara.streams;

import java.nio.ByteBuffer;
import java.util.WeakHashMap;

public class BufferStreamTest extends FuzzStreamTest {

	private final WeakHashMap<BufferWriteStream, ByteBuffer> buffers = new WeakHashMap<>();

	@Override
	WriteStream newWriter() {
		ByteBuffer buffer = ByteBuffer.allocate(MAX_WRITES * MAX_LEN * 2);
		BufferWriteStream writer = new BufferWriteStream(buffer);
		buffers.put(writer, buffer);
		return writer;
	}

	@Override
	ReadStream newReader(WriteStream writer) {
		ByteBuffer buffer = buffers.remove(writer);
		buffer.flip();
		return new BufferReadStream(buffer);
	}

	@Override
	boolean closeHonored() { return false; }
}
