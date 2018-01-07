/*
 * Copyright 2016 Tom Gibara
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

import static com.tomgibara.streams.Streams.debugString;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import com.tomgibara.fundament.Consumer;

final class DebugWriteStream extends WrappedWriteStream {

	private final PrintWriter writer;
	private final String identity;

	public DebugWriteStream(WriteStream wrapped, PrintWriter writer, String identity) {
		super(wrapped);
		if (writer == null) throw new IllegalArgumentException("null writer");
		this.writer = writer;
		this.identity = identity == null ? null : identity + '.';
	}

	@Override
	public void writeByte(byte v) {
		writeIdentity();
		writer.println("writeByte(" + v + ")");
		super.writeByte(v);
	}

	@Override
	public void writeBytes(byte[] bytes) {
		writeIdentity();
		writer.println("writeByte(" +debugString(bytes) + ")");
		super.writeBytes(bytes);
	}

	@Override
	public void writeBytes(byte[] bs, int off, int len) {
		writeIdentity();
		writer.println("writeByte(" +debugString(bs) + ", " + off + ", " + len + ")");
		super.writeBytes(bs, off, len);
	}

	@Override
	public int tryWriteBytes(byte[] bs, int off, int len) throws StreamException {
		writeIdentity();
		writer.println("tryWriteBytes(" +debugString(bs) + ", " + off + ", " + len + ")");
		return super.tryWriteBytes(bs, off, len);
	}

	@Override
	public void writeInt(int v) {
		writeIdentity();
		writer.println("writeInt(" + v + ")");
		super.writeInt(v);
	}

	@Override
	public void writeBoolean(boolean v) {
		writeIdentity();
		writer.println("writeBoolean(" + v + ")");
		super.writeBoolean(v);
	}

	@Override
	public void writeShort(short v) {
		writeIdentity();
		writer.println("writeShort(" + v + ")");
		super.writeShort(v);
	}

	@Override
	public void writeLong(long v) {
		writeIdentity();
		writer.println("writeLong(" + v + ")");
		super.writeLong(v);
	}

	@Override
	public void writeFloat(float v) {
		writeIdentity();
		writer.println("writeFloat(" + v + ")");
		super.writeFloat(v);
	}

	@Override
	public void writeDouble(double v) {
		writeIdentity();
		writer.println("writeDouble(" + v + ")");
		super.writeDouble(v);
	}

	@Override
	public void writeChar(char v) {
		writeIdentity();
		writer.println("writeChar(" + v + ")");
		super.writeChar(v);
	}

	@Override
	public void writeChars(char[] cs) {
		writeIdentity();
		writer.println("writeChars(" +debugString(cs) + ")");
		super.writeChars(cs);
	}

	@Override
	public void writeChars(char[] cs, int off, int len) {
		writeIdentity();
		writer.println("writeChars(" + debugString(cs) + ", " + off + ", " + len + ")");
		super.writeChars(cs, off, len);
	}

	@Override
	public void writeChars(CharSequence cs) {
		writeIdentity();
		writer.println("writeChars(" +debugString(cs) + ")");
		super.writeChars(cs);
	}

	@Override
	public StreamBuffering getBuffering() {
		writeIdentity();
		writer.println("getBuffering()");
		return super.getBuffering();
	}

	@Override
	public void drainBuffer(ByteBuffer buffer) {
		writeIdentity();
		writer.println("drainBuffer(" + debugString(buffer) + ")");
		super.drainBuffer(buffer);
	}

	@Override
	public WriteStream bounded(long length) {
		writeIdentity();
		writer.println("bounded(" + length + ")");
		return super.bounded(length);
	}

	@Override
	public WriteStream closedWith(StreamCloser closer) {
		writeIdentity();
		writer.println("closedWith(" + debugString(closer) + ")");
		return super.closedWith(closer);
	}

	@Override
	public OutputStream asOutputStream() {
		writeIdentity();
		writer.println("asOutputStream()");
		return super.asOutputStream();
	}

	@Override
	public WritableByteChannel asChannel() {
		writeIdentity();
		writer.println("asChannel()");
		return super.asChannel();
	}

	@Override
	public StreamTransfer from(ReadStream source) {
		writeIdentity();
		writer.println("from(" + debugString(source) + ")");
		return super.from(source);
	}

	@Override
	public StreamTransfer from(ReadStream source, int bufferSize) {
		writeIdentity();
		writer.println("from(" + debugString(source) + ", " + bufferSize + ")");
		return super.from(source);
	}

	@Override
	public StreamTransfer from(ReadStream source, ByteBuffer buffer) {
		writeIdentity();
		writer.println("from(" + debugString(source) + ", " + debugString(buffer) + ")");
		return super.from(source, buffer);
	}

	@Override
	public <T> Consumer<T> writeWith(StreamSerializer<T> serializer) {
		writeIdentity();
		writer.println("writeWith(" + debugString(serializer) + ")");
		return super.writeWith(serializer);
	}

	@Override
	public void close() throws StreamException {
		writeIdentity();
		writer.println("close()");
		super.close();
	}

	private void writeIdentity() {
		if (identity != null) writer.print(identity);
	}

}
