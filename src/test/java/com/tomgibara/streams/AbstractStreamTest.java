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

public class AbstractStreamTest extends FuzzStreamTest {

	@Override
	WriteStream newWriter() {
		return new TestWriteStream();
	}

	@Override
	ReadStream newReader(WriteStream writer) {
		return new TestReadStream((TestWriteStream) writer);
	}

	private static final class TestWriteStream extends AbstractWriteStream {

		final List<Byte> list = new ArrayList<Byte>();

		@Override
		public void writeByte(byte v) {
			list.add(v);
		}

	}

	private static final class TestReadStream implements ReadStream {

		final Iterator<Byte> iter;

		TestReadStream(TestWriteStream s) {
			iter = s.list.iterator();
		}

		@Override
		public byte readByte() {
			if (!iter.hasNext()) throw EndOfStreamException.EOS;
			return iter.next();
		}

	}

}
