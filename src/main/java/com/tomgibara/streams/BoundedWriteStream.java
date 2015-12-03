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

import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;

class BoundedWriteStream implements WriteStream {

	private final WriteStream stream;
	private long remaining;
	
	BoundedWriteStream(WriteStream stream, long remaining) {
		if (stream == null) throw new IllegalArgumentException("null stream");
		if (remaining < 0L) throw new IllegalArgumentException("negative remaining");
		this.stream = stream;
		this.remaining = remaining;
	}
	
	@Override
	public void writeByte(byte v) throws StreamException {
		if (remaining <= 0) EndOfStreamException.raise();
		stream.writeByte(v);
		remaining--;
	}

	@Override
	public WriteStream bounded(long length) {
		if (length < 0L) throw new IllegalArgumentException("negative length");
		return length < remaining ? new BoundedWriteStream(stream, length) : this;
	}

	@Override
	public OutputStream asOutputStream() {
		return new BoundedOutputStream(stream.asOutputStream(), remaining);
	}

	@Override
	public WritableByteChannel asChannel() {
		return new BoundedWritableChannel(stream.asChannel(), remaining);
	}

	@Override
	public void close() throws StreamException {
		stream.close();
	}

}
