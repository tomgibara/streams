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
