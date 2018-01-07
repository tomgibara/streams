/*
 * Copyright 2018 Tom Gibara
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.WeakHashMap;

public class DataStreamTest extends FuzzStreamTest {

	private final WeakHashMap<DataWriteStream, ByteArrayOutputStream> streams = new WeakHashMap<>();

	@Override
	WriteStream newWriter() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataWriteStream w = new DataWriteStream(new DataOutputStream(out));
		streams.put(w, out);
		return w;
	}

	@Override
	ReadStream newReader(WriteStream writer) {
		return new DataReadStream(new DataInputStream(new ByteArrayInputStream(streams.get(writer).toByteArray())));
	}

	@Override
	boolean closeHonored() {
		return false; // false in this case because the underlying bytestream doesn't close
	}
}
