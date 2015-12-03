/*
 * Copyright 2015 Tom Gibara
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
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
