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
package com.tomgibara.streams.sample;

import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.tomgibara.fundament.Consumer;
import com.tomgibara.fundament.Producer;
import com.tomgibara.streams.ByteArrayChannel;
import com.tomgibara.streams.ReadStream;
import com.tomgibara.streams.StreamBytes;
import com.tomgibara.streams.Streams;
import com.tomgibara.streams.WriteStream;

public class SamplesTest {

	private static InputStream someInput = new ByteArrayInputStream(new byte[100]);
	private static OutputStream someOutput = new ByteArrayOutputStream();
	private static ReadableByteChannel someReadableChannel = new ByteArrayChannel(new byte[1000]);
	private static WritableByteChannel someWritableChannel = new ByteArrayChannel(new byte[1000]);
	private static ByteBuffer buffer = ByteBuffer.allocate(1000);
	private static byte[] bytes = new byte[1000];
	private static ReadStream someReader = Streams.bytes(new byte[2000]).readStream();
	private static WriteStream someWriter = Streams.bytes().writeStream();
	
	@Test
	public void samples() {
		
		// read and write all types of primitives
		try (ReadStream r = someReader) {
			r.readBoolean();
			r.readByte();
			r.readFloat();
			// etc.
			// support for bytes ...
			r.readBytes(bytes);
			// ... and strings
			String str = r.readChars();
		}
		
		// use an InputStream as a ReadStream
		try (ReadStream r = Streams.streamInput(someInput)) {
			r.readInt();
			r.readChars();
			// etc.
		}
		
		// use an OutputStream as a WriteStream
		try (WriteStream w = Streams.streamOutput(someOutput)) {
			w.writeLong(1234567890123456789L);
			w.writeChars("Example");
			// etc.
		}
		
		// use a ReadableByteChannel as a ReadStream
		try (ReadStream r = Streams.streamReadable(someReadableChannel)) {
			r.fillBuffer(buffer);
			// etc.
		}

		// use a WritableByteChannel as a ReadStream
		try (WriteStream r = Streams.streamWritable(someWritableChannel)) {
			r.drainBuffer(buffer);
			// etc.
		}

		// access a byte array through a ReadStream
		Streams.bytes(bytes).readStream();
		
		{ // accumulate bytes with a WriteStream
			StreamBytes bs = Streams.bytes();
			WriteStream w = bs.writeStream();
			w.writeDouble(0.35);
			w.close();
			byte[] bytes = bs.bytes();
			// use the bytes here
		}
		
		{ // accumulate byte data with a WriteStream and read it back
			StreamBytes bs = Streams.bytes();
			WriteStream w = bs.writeStream();
			w.writeChar('ζ'); // u03b6
			ReadStream r = bs.readStream(); // closes the writer
			r.readByte(); // 03
			r.readByte(); // b6
			r.close();
		}

		// expose a write stream as an output stream
		Streams.bytes().writeStream().asOutputStream();

		// limit the number bytes that can be read from a stream
		Streams.bytes(128).writeStream().bounded(64); // write only 64 bytes
		
		// limit the number of bytes that can be read an input stream
		Streams.streamInput(someInput).bounded(1024).asInputStream(); // 1k only

		// convert an input stream into a channel...
		Streams.streamInput(someInput).asChannel();
		
		// ... and vice versa
		Streams.streamReadable(someReadableChannel).asInputStream();
		
		// create a stream using a lambda
		List<Byte> values = new ArrayList<Byte>();
		try (WriteStream r = v -> values.add(v)) {
			// the stream has all the supporting methods, eg.
			r.bounded(10);
			r.drainBuffer(buffer);
		}

		// use a lightweight serialization API to read & write non-primitive values
		Producer<Point> prd = someReader.readWith(r -> new Point(r.readInt(), r.readInt()));
		Point pt1 = prd.produce();
		Point pt2 = prd.produce();
		// etc.
		Consumer<Point> con = someWriter.writeWith((p,w) -> {w.writeInt(p.x); w.writeInt(p.y);});
		con.consume(pt1);
		con.consume(pt2);
	}
	
}
