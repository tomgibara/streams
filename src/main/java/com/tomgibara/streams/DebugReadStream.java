package com.tomgibara.streams;

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
	public void close() throws StreamException {
		writeIdentity();
		writer.println("close()");
		super.close();
	}

	@Override
	public byte readByte() throws StreamException {
		writeIdentity();
		writer.println("close()");
		return super.readByte();
	}

	@Override
	public void readBytes(byte[] bs) throws StreamException {
		writeIdentity();
		writer.println(bs == null ? "readBytes(null)" : "readBytes(byte[" + bs.length + "])");
		super.readBytes(bs);
	}

	@Override
	public void readBytes(byte[] bs, int off, int len) throws StreamException {
		writeIdentity();
		writer.println(bs == null ? "readBytes(null,"+off+""+len+")" : "readBytes(byte[" + bs.length + "],"+off+""+len+")");
		super.readBytes(bs, off, len);
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
		writer.println(cs == null ? "readChars(null)" : "readChars(char["+cs.length+"])");
		super.readChars(cs);
	}

	@Override
	public void readChars(char[] cs, int off, int len) throws StreamException {
		writeIdentity();
		writer.println(cs == null ? "readChars(null,"+off+""+len+")" : "readChars(char[" + cs.length + "],"+off+""+len+")");
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
		writer.println(buffer == null ? "fillBuffer(null)" : "fillBuffer(" + buffer + ")");
		super.fillBuffer(buffer);
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
		writer.println(closer == null ? "closedWith(null)" : "closedWith(StreamCloser)");
		return super.closedWith(closer);
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
		writer.println(target == null ? "to(null)" : "to(WriteStream)");
		return super.to(target);
	}

	@Override
	public StreamTransfer to(WriteStream target, ByteBuffer buffer) {
		writeIdentity();
		writer.println(target == null ? "to(null)" : "to(WriteStream)");
		return super.to(target, buffer);
	}

	@Override
	public StreamTransfer to(WriteStream target, int bufferSize) {
		writeIdentity();
		writer.println(target == null ? "to(null," + bufferSize + ")" : "to(WriteStream," + bufferSize + ")");
		return super.to(target, bufferSize);
	}

	@Override
	public <T> Producer<T> readWith(StreamDeserializer<T> deserializer) {
		writeIdentity();
		writer.println(deserializer == null ? "readWith(null)" : "readWith(StreamDeserializer)");
		return super.readWith(deserializer);
	}

	private void writeIdentity() {
		if (identity != null) writer.write(identity);
	}
}
