Streams
=======

This streams library for Java provides robust unification of the `InputStream`, `OutputStream`, `ByteChannel` and `ByteBuffer` abstractions under a comprehensive API. It's implementation is rooted in an earlier, more modest library that aimed only to provide a better byte stream API.

Overview
--------

The core abstraction of this package are the twin interfaces `ReadStream` and `WriteStream`. These provide methods for reading and writing primitive Java primitives together with a number other methods for manipulating the streams.

The entry point for the package is the `Streams` class which contains static methods for:

 * creating `StreamBytes` to read and write byte-arrays using streams.
 * creating `ReadStream` instances that wrap `InputStream` and `ReadableByteChannel` objects
 * creating `WriteStream` instances that wrap `OutputStream` and `WritableByteChannel` objects

See the examples below to discover the range of methods available on streams; full Javadocs are available.

Examples
--------

Here are some quick-fire examples of how you can use this package.

### Examples of using streams as a sane I/O API

```java
// read and write all types of primitives
try (ReadStream r = someReader) {
	r.readBoolean();
	r.readByte();
	r.readFloat();
	// ... and all other primitives
	// support for byte arrays and strings
	r.readBytes(bytes);
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

// use a WritableByteChannel as a WriteStream
try (WriteStream r = Streams.streamWritable(someWritableChannel)) {
	r.drainBuffer(buffer);
	// etc.
}

// read a byte array through a ReadStream
Streams.bytes(bytes).readStream();

// accumulate bytes with a WriteStream
StreamBytes bs = Streams.bytes();
try (WriteStream w = bs.writeStream()) {
	w.writeDouble(0.35);
}
byte[] bytes = bs.bytes();
// use the bytes here

// accumulate byte data with a WriteStream...
bs = Streams.bytes();
WriteStream w = bs.writeStream();
w.writeChar('ζ'); // u03b6

// ... and read it back with a ReadStream
ReadStream r = bs.readStream(); // this automatically closes the writer
r.readByte(); // 0x03
r.readByte(); // 0xb6
r.close();

// limit the number bytes that can be read from a stream
Streams.bytes(128).writeStream().bounded(64); // write only 64 bytes

// wrap a stream to control its behaviour when closed
someWriter.closedWith(StreamCloser.reportClosed());
// close() on this stream will leave the wrapped stream open
```

### Examples of using streams as utility methods for Java I/O

```java
// expose a write stream as an output stream
Streams.bytes().writeStream().asOutputStream();

// limit the number of bytes that can be read an input stream
Streams.streamInput(someInput).bounded(1024).asInputStream(); // 1k only

// create an 'unclosable' input stream
Streams.streamInput(someInput).closedWith(StreamCloser.doNothing()).asInputStream();

// convert an input stream into a channel...
Streams.streamInput(someInput).asChannel();

// ... and vice versa
Streams.streamReadable(someReadableChannel).asInputStream();
```

### Examples of using streams with other abstractions

```java
// create a stream using a lambda
List<Byte> values = new ArrayList<Byte>();
try (WriteStream rs = v -> values.add(v)) {
	// the stream has all the supporting methods, eg.
	rs.bounded(10);
	rs.drainBuffer(buffer);
}

// use a lightweight serialization API to read & write non-primitive values
Producer<Point> prd = someReader.readWith(s -> new Point(s.readInt(), s.readInt()));
Point pt1 = prd.produce();
Point pt2 = prd.produce();
// etc.
Consumer<Point> con = someWriter.writeWith((p,s) -> {s.writeInt(p.x); s.writeInt(p.y);});
con.consume(pt1);
con.consume(pt2);
```

Usage
-----

The streams library is available from the Maven central repository:

> Group ID:    `com.tomgibara.streams`
> Artifact ID: `streams`
> Version:     `2.0.0`

The Maven dependency being:

    <dependency>
      <groupId>com.tomgibara.streams</groupId>
      <artifactId>streams</artifactId>
      <version>2.0.0</version>
    </dependency>

Release History
---------------

**2015.12.10** Version 2.0.0
 * Evolution of the library into a comprehensive API

**2015.07.27** Version 1.1.0

 * Added new WrappedReadStream and WrappedWriteStream
 * Added getDigest() to DigestWriteStream

**2015.07.20** Version 1.0.0

Initial release
