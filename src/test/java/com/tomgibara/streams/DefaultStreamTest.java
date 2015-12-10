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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultStreamTest extends FuzzStreamTest {

	@Override
	WriteStream newWriter() {
		return new TestWriteStream();
	}

	@Override
	ReadStream newReader(WriteStream writer) {
		return new TestReadStream((TestWriteStream) writer);
	}

	private static final class TestWriteStream implements WriteStream {

		final List<Byte> list = new ArrayList<Byte>();
		boolean closed = false;

		@Override
		public void writeByte(byte v) {
			if (closed) StreamException.raiseClosed();
			list.add(v);
		}
		
		@Override
		public void close() {
			closed = true;
		}

	}

	private static final class TestReadStream implements ReadStream {

		final Iterator<Byte> iter;
		boolean closed = false;

		TestReadStream(TestWriteStream s) {
			iter = s.list.iterator();
		}

		@Override
		public byte readByte() {
			if (closed) StreamException.raiseClosed();
			if (!iter.hasNext()) EndOfStreamException.raise();
			return iter.next();
		}

		@Override
		public void close() {
			closed = true;
		}

	}

}
