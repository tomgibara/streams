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

	private static final class TestReadStream extends AbstractReadStream {

		final Iterator<Byte> iter;

		TestReadStream(TestWriteStream s) {
			iter = s.list.iterator();
		}

		@Override
		public byte readByte() {
			if (!iter.hasNext()) throw StreamException.EOS;
			return iter.next();
		}

	}
	
}
