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

import java.io.IOException;
import java.io.OutputStream;

class WriteOutputStream extends OutputStream {

	private final WriteStream stream;
	
	public WriteOutputStream(WriteStream stream) {
		this.stream = stream;
	}
	
	@Override
	public void write(int b) throws IOException {
		try {
			stream.writeByte((byte) b);
		} catch (StreamException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void write(byte[] b) throws IOException {
		try {
			stream.writeBytes(b);
		} catch (StreamException e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		try {
			stream.writeBytes(b, off, len);
		} catch (StreamException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void close() throws IOException {
		try {
			stream.close();
		} catch (StreamException e) {
			throw new IOException(e);
		}
	}

}
