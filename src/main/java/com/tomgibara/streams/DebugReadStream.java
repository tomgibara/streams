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

import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import com.tomgibara.fundament.Producer;

class DebugReadStream extends WrappedReadStream {

	private final String identity;
	private final PrintWriter writer;

	DebugReadStream(ReadStream wrapped, PrintWriter writer, String identity) {
		super(wrapped);
		this.identity = identity == null ? null : identity + '.';
		this.writer = writer;
	}

	@Override
	public long position() throws StreamException {
		writeIdentity();
		writer.println("position()");
		return super.position();
	}

	@Override
	public void close() throws StreamException {
		writeIdentity();
		writer.println("close()");
		super.close();
	}

	@Override
	public byte readByte() throws StreamException {
		writeIdentity();
		writer.println("readByte()");
		return super.readByte();
	}

	@Override
	public void readBytes(byte[] bs) throws StreamException {
		writeIdentity();
		writer.println("readBytes(" + debugString(bs) + ")");
		super.readBytes(bs);
	}

	@Override
	public void readBytes(byte[] bs, int off, int len) throws StreamException {
		writeIdentity();
		writer.println("readBytes(" + debugString(bs) + "," + off + ", " + len + ")");
		super.readBytes(bs, off, len);
	}

	@Override
	public int tryReadBytes(byte[] bs, int off, int len) throws StreamException {
		writeIdentity();
		writer.println("tryReadBytes(" + debugString(bs) + "," + off + ", " + len + ")");
		return super.tryReadBytes(bs, off, len);
	}

	@Override
	public int readInt() throws StreamException {
		writeIdentity();
		writer.println("readInt()");
		return super.readInt();
	}

	@Override
	public boolean readBoolean() throws StreamException {
		writeIdentity();
		writer.println("readBoolean()");
		return super.readBoolean();
	}

	@Override
	public short readShort() throws StreamException {
		writeIdentity();
		writer.println("readShort()");
		return super.readShort();
	}

	@Override
	public long readLong() throws StreamException {
		writeIdentity();
		writer.println("readLong()");
		return super.readLong();
	}

	@Override
	public float readFloat() throws StreamException {
		writeIdentity();
		writer.println("readFloat()");
		return super.readFloat();
	}

	@Override
	public double readDouble() throws StreamException {
		writeIdentity();
		writer.println("readDouble()");
		return super.readDouble();
	}

	@Override
	public char readChar() throws StreamException {
		writeIdentity();
		writer.println("readChar()");
		return super.readChar();
	}

	@Override
	public void readChars(char[] cs) throws StreamException {
		writeIdentity();
		writer.println("readChars(" + debugString(cs) + ")");
		super.readChars(cs);
	}

	@Override
	public void readChars(char[] cs, int off, int len) throws StreamException {
		writeIdentity();
		writer.println("readChars(" + debugString(cs) + ", " + off + ", " + len + ")");
		super.readChars(cs, off, len);
	}

	@Override
	public String readChars() throws StreamException {
		writeIdentity();
		writer.println("readChars()");
		return super.readChars();
	}

	@Override
	public StreamBuffering getBuffering() {
		writeIdentity();
		writer.println("getBuffering()");
		return super.getBuffering();
	}

	@Override
	public void fillBuffer(ByteBuffer buffer) throws StreamException {
		writeIdentity();
		writer.println("fillBuffer(" + debugString(buffer) + ")");
		super.fillBuffer(buffer);
	}

	@Override
	public void skip(long length) throws StreamException {
		writeIdentity();
		writer.println("fillBuffer(" + length + ")");
		super.skip(length);
	}

	@Override
	public ReadStream bounded(long length) {
		writeIdentity();
		writer.println("bounded(" + length + ")");
		return super.bounded(length);
	}

	@Override
	public ReadStream closedWith(StreamCloser closer) {
		writeIdentity();
		writer.println("closedWith(" + debugString(closer) + ")");
		return super.closedWith(closer);
	}

	@Override
	public ReadStream andThen(ReadStream stream) {
		writeIdentity();
		writer.println("andThen(" + debugString(stream) + ")");
		return super.andThen(stream);
	}

	@Override
	public ReadStream andThen(StreamCloser closer, ReadStream stream) {
		writeIdentity();
		writer.println("andThen(" + debugString(closer) + "," + debugString(stream) + ")");
		return super.andThen(closer, stream);
	}

	@Override
	public ReadStream butFirst(ReadStream stream) {
		writeIdentity();
		writer.println("butFirst(" + debugString(stream) + ")");
		return super.butFirst(stream);
	}

	@Override
	public ReadStream butFirst(ReadStream stream, StreamCloser closer) {
		writeIdentity();
		writer.println("butFirst(" + debugString(stream) + "," + debugString(closer) + ")");
		return super.butFirst(stream, closer);
	}

	@Override
	public ReadStream debug(PrintWriter writer, String identity) {
		writeIdentity();
		writer.println("debug(" + debugString(writer) + "," + identity + ")");
		return super.debug(writer, identity);
	}

	@Override
	public InputStream asInputStream() {
		writeIdentity();
		writer.println("asInputStream()");
		return super.asInputStream();
	}

	@Override
	public ReadableByteChannel asChannel() {
		writeIdentity();
		writer.println("asChannel()");
		return super.asChannel();
	}

	@Override
	public StreamTransfer to(WriteStream target) {
		writeIdentity();
		writer.println("to(" + debugString(target) + ")");
		return super.to(target);
	}

	@Override
	public StreamTransfer to(WriteStream target, ByteBuffer buffer) {
		writeIdentity();
		writer.println("to(" + debugString(target) + ", " + debugString(buffer) + ")");
		return super.to(target, buffer);
	}

	@Override
	public StreamTransfer to(WriteStream target, int bufferSize) {
		writeIdentity();
		writer.println("to(" + debugString(target) + "," + bufferSize + ")");
		return super.to(target, bufferSize);
	}

	@Override
	public <T> Producer<T> readWith(StreamDeserializer<T> deserializer) {
		writeIdentity();
		writer.println("readWith(" + debugString(deserializer) + ")");
		return super.readWith(deserializer);
	}

	private void writeIdentity() {
		if (identity != null) writer.print(identity);
	}
}
