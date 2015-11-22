package com.tomgibara.streams;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.WeakHashMap;

public class ChannelStreamTest extends FuzzStreamTest {

	private final WeakHashMap<ChannelWriteStream, SeekableByteChannel> channels = new WeakHashMap<>();
	
	@Override
	WriteStream newWriter() {
		try {
			Path path = Files.createTempFile("channel-", "-test");
			SeekableByteChannel channel = Files.newByteChannel(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.DELETE_ON_CLOSE);
			ChannelWriteStream writer = new ChannelWriteStream(channel);
			channels.put(writer, channel);
			return writer;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	ReadStream newReader(WriteStream writer) {
		try {
			SeekableByteChannel channel = channels.get(writer);
			channel.position(0L);
			ChannelReadStream reader = new ChannelReadStream(channel);
			channels.remove(writer);
			return reader;
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}

}
