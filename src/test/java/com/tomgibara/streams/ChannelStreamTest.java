package com.tomgibara.streams;

import java.util.WeakHashMap;

public class ChannelStreamTest extends FuzzStreamTest {

	private final WeakHashMap<ChannelWriteStream, ByteArrayChannel> channels = new WeakHashMap<>();
	
	@Override
	WriteStream newWriter() {
		ByteArrayChannel channel = new ByteArrayChannel(MAX_WRITES * MAX_LEN * 2);
		ChannelWriteStream writer = new ChannelWriteStream(channel);
		channels.put(writer, channel);
		return writer;
	}

	@Override
	ReadStream newReader(WriteStream writer) {
		ByteArrayChannel channel = channels.get(writer);
		channel.truncate(channel.position());
		channel.position(0L);
		ChannelReadStream reader = new ChannelReadStream(channel);
		channels.remove(writer);
		return reader;
	}

}
