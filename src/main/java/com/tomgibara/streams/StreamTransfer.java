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

import static com.tomgibara.streams.StreamBuffering.PREFER_ANY;
import static com.tomgibara.streams.StreamBuffering.PREFER_DIRECT;
import static com.tomgibara.streams.StreamBuffering.PREFER_INDIRECT;
import static com.tomgibara.streams.StreamBuffering.UNSUPPORTED;

import java.nio.ByteBuffer;

/**
 * Transfers data from a {@link ReadStream} to a {@link WriteStream}. Instances
 * of this class are generally short lived objects that are used once to affect
 * the the transfer of bytes from a source stream to a target stream.
 *
 * @author Tom Gibara
 *
 * @see ReadStream#to(WriteStream)
 * @see WriteStream#from(ReadStream)
 */

public final class StreamTransfer {

	private static StreamBuffering combine(StreamBuffering src, StreamBuffering dst) {
		switch (src) {
		case UNSUPPORTED: return dst;
		case PREFER_ANY: return dst == UNSUPPORTED || dst == PREFER_ANY ? PREFER_DIRECT : dst;
		case PREFER_DIRECT: return dst == PREFER_INDIRECT ? PREFER_INDIRECT : PREFER_DIRECT;
		case PREFER_INDIRECT: return src;
		default: throw new IllegalStateException("Unexpected buffering type: " + src);
		}
	}

	private static ByteBuffer buffer(StreamBuffering src, StreamBuffering dst, int bufferSize) {
		if (bufferSize == 0) return null;
		StreamBuffering buffering = combine(src, dst);
		switch (buffering) {
		case UNSUPPORTED: return null;
		case PREFER_DIRECT: return ByteBuffer.allocateDirect(bufferSize);
		case PREFER_INDIRECT: return ByteBuffer.allocate(bufferSize);
		default: throw new IllegalStateException("Unexpected buffer type: " + buffering);
		}
	}

	/**
	 * Captures information about a transfer.
	 *
	 * @see StreamTransfer#transferFully()
	 * @see StreamTransfer#transfer(long)
	 */

	public static final class Result {

		private final long bytesTransfered;
		private final boolean sourceExhausted;
		private final boolean targetFull;
		private final ReadStream residualStream;

		Result(long bytesTransferred, boolean sourceExhausted, boolean targetExhausted, ReadStream residualStream) {
			this.bytesTransfered = bytesTransferred;
			this.sourceExhausted = sourceExhausted;
			this.targetFull = targetExhausted;
			this.residualStream = residualStream;
		}

		/**
		 * The number of bytes transferred from the source to the target.
		 *
		 * @return the number of bytes transferred
		 */

		public long bytesTransfered() {
			return bytesTransfered;
		}

		/**
		 * Whether the source stream of a transfer raised an
		 * {@link EndOfStreamException} before the transfer was completed.
		 *
		 * @return whether the source stream of the transfer was exhausted
		 */

		public boolean sourceExhausted() {
			return sourceExhausted;
		}

		/**
		 * Whether the target stream of a transfer raised an
		 * {@link EndOfStreamException} before the transfer was completed.
		 *
		 * @return whether the source stream of the transfer filled
		 */

		public boolean targetFull() {
			return targetFull;
		}

		/**
		 * A stream, possibly empty, containing all of the data that was not
		 * transferred to the target stream. This stream should always be used
		 * to resume from transfers that were interrupted by saturation of the
		 * target stream, since it is possible that residual data may exist;
		 * having been read from the source but rejected by the target.
		 *
		 * @return a stream containing all source bytes not written to the
		 *         target
		 */

		public ReadStream residualStream() {
			return residualStream;
		}
	}

	private final ReadStream source;
	private final WriteStream target;
	private final ByteBuffer buffer;
	private final boolean privateBuffer;

	StreamTransfer(ReadStream source, WriteStream target) {
		this.source = source;
		this.target = target;
		buffer = buffer(source.getBuffering(), target.getBuffering(), Streams.BUFFER_SIZE);
		privateBuffer = true;
	}

	StreamTransfer(ReadStream source, WriteStream target, int bufferSize) {
		if (bufferSize < 0) throw new IllegalArgumentException("negative bufferSize");
		this.source = source;
		this.target = target;
		buffer = buffer(source.getBuffering(), target.getBuffering(), bufferSize);
		privateBuffer = true;
	}

	StreamTransfer(ReadStream source, WriteStream target, ByteBuffer buffer) {
		if (buffer != null && buffer.isReadOnly()) throw new IllegalArgumentException("buffer read-only");
		this.source = source;
		this.target = target;
		this.buffer = buffer != null && buffer.capacity() == 0 ? null : buffer;
		privateBuffer = false;
	}

	/**
	 * The buffering strategy in operation for this transfer.
	 *
	 * @return the specific buffering used (ie. never
	 *         {@link StreamBuffering#PREFER_ANY})
	 */

	// not necessarily ready for public consumption
	StreamBuffering buffering() {
		if (buffer == null) return UNSUPPORTED;
		return buffer.isDirect() ? PREFER_DIRECT : PREFER_INDIRECT;
	}

	/**
	 * Transfers the specified number of bytes from the source to the target.
	 * Fewer bytes may be transferred if an end-of-stream condition occurs in
	 * either the source or the target.
	 *
	 * @param count
	 *            the number of bytes to be transferred
	 * @return the result of the transfer
	 */

	public Result transfer(long count) {
		if (count < 0L) throw new IllegalArgumentException("negative count");
		return buffer == null ? transferNoBuffer(count) : transferBuffered(count);
	}

	/**
	 * Transfers the bytes from the source to the target until one or possibly
	 * both streams are exhausted.
	 *
	 * @return the result of the transfer
	 */

	public Result transferFully() {
		return buffer == null ? transferNoBuffer() : transferBuffered();
	}

	@SuppressWarnings("resource")
	private Result transferNoBuffer() {
		long count = 0L;
		while (true) {
			byte b;
			try {
				b = source.readByte();
			} catch (EndOfStreamException e) {
				/* we swallow this - it's essentially inevitable */
				return new Result(count, true, false, EmptyReadStream.INSTANCE);
			}
			try {
				target.writeByte(b);
			} catch (EndOfStreamException e) {
				return new Result(count, false, true, new SingleReadStream(b).andThen(source));
			}
			count ++;
		}
	}

	@SuppressWarnings("resource")
	private Result transferBuffered() {
		buffer.clear();
		long count = 0L;
		while (true) {
			source.fillBuffer(buffer);
			boolean srcExhausted = buffer.hasRemaining();
			buffer.flip();
			count += buffer.remaining();
			target.drainBuffer(buffer);
			boolean dstExhausted = buffer.hasRemaining();
			count -= buffer.remaining();
			if (srcExhausted || dstExhausted) {
				ReadStream residual;
				if (privateBuffer) {
					residual = new BufferReadStream(buffer);
				} else {
					byte[] bytes = new byte[buffer.remaining()];
					buffer.put(bytes);
					residual = new BytesReadStream(bytes);
					buffer.clear();
				}
				return new Result(count, srcExhausted, dstExhausted, residual.andThen(source));
			}
			buffer.clear();
		}
	}

	@SuppressWarnings("resource")
	private Result transferNoBuffer(long count) {
		long c = 0L;
		while (c < count) {
			byte b;
			try {
				b = source.readByte();
			} catch (EndOfStreamException e) {
				return new Result(c, true, false, EmptyReadStream.INSTANCE);
			}
			try {
				target.writeByte(b);
			} catch (EndOfStreamException e) {
				return new Result(c, false, true, new SingleReadStream(b).andThen(source));
			}
			c++;
		}
		return new Result(c, false, false, source);
	}

	private Result transferBuffered(long count) {
		int capacity = buffer.capacity();
		if (capacity == 0) throw new IllegalArgumentException("buffer has zero capacity");
		buffer.clear();
		long c = count;
		boolean srcExhausted = false;
		boolean dstExhausted = false;
		while (c > 0) {
			if (c < capacity) buffer.limit((int) c);
			source.fillBuffer(buffer);
			srcExhausted = buffer.hasRemaining();
			buffer.flip();
			c -= buffer.remaining();
			target.drainBuffer(buffer);
			dstExhausted = buffer.hasRemaining();
			c += buffer.remaining();
			buffer.clear();
			if (srcExhausted || dstExhausted) break;
		}
		return new Result(count - c, srcExhausted, dstExhausted, source);
	}
}
