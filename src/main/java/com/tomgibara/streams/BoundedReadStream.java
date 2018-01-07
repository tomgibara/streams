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

import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

class BoundedReadStream implements ReadStream {

	private final ReadStream stream;
	private long remaining;

	BoundedReadStream(ReadStream stream, long remaining) {
		if (stream == null) throw new IllegalArgumentException("null stream");
		if (remaining < 0L) throw new IllegalArgumentException("negative remaining");
		this.stream = stream;
		this.remaining = remaining;
	}

	@Override
	public byte readByte() throws StreamException {
		if (remaining <= 0) throw EndOfStreamException.instance();
		byte value = stream.readByte();
		remaining--;
		return value;
	}

	@Override
	public ReadStream bounded(long length) {
		if (length < 0L) throw new IllegalArgumentException("negative length");
		return length < remaining ? new BoundedReadStream(stream, length) : this;
	}

	@Override
	public InputStream asInputStream() {
		return new BoundedInputStream(stream.asInputStream(), remaining);
	}

	@Override
	public ReadableByteChannel asChannel() {
		return new BoundedReadableChannel(stream.asChannel(), remaining);
	}

	@Override
	public void close() {
		stream.close();
	}

}
