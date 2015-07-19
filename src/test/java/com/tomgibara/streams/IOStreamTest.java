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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class IOStreamTest extends FuzzStreamTest {

	private HashMap<OutputWriteStream, ByteArrayOutputStream> map = new HashMap<>();
	
	@Override
	WriteStream newWriter() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		OutputWriteStream wri = new OutputWriteStream(out);
		map.put(wri, out);
		return wri;
	}

	@Override
	ReadStream newReader(WriteStream writer) {
		ByteArrayOutputStream out = map.get(writer);
		byte[] bytes = out.toByteArray();
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		return new InputReadStream(in);
	}

}
