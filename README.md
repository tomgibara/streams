Streams
=======

This streams library for java provides a modest abstraction for
streaming core Java types as byte values.

Overview
--------

### Version 1.x

This library may be thought of as providing a simpler
`DataInputStream`/`DataOutputStream` abstraction. It consists of a
small package of classes based around two basic interfaces which are
counterparts to each other:

* `ReadStream` provides methods for reading Java primitives as a
   stream
* `WriteStream` provides corresponding methods for writing Java
   primitives as a stream.

Implementations are provided for reading/writing from/to byte arrays:

* `ByteReadStream` reads values from a byte array
* `ByteWriteStream` writes values to a growable byte array

There are also implementations for bridging to regular Java streams:

* `InputReadStream` reads values from an underlying `InputStream`
* `OutputWriteStream` writes values to an underlying `OutputStream`

And a pair of abstract base classes reduce creating new
implementations to a single method:

* `AbstractReadStream` only requires implementation of the
  `readByte()` method
* `AbstractWriteStream` only requires implementation of the
   `writeByte()` method

Finally a pair of delegating wrapper classes is available:

* `WrappedReadStream` delegates all method calls to a wrapped
  `ReadStream`
* `WrappedWriteStream` delegates all method calls to a wrapped
  `WriteStream`

All the classes are available in the `com.tomgibara.streams` package.
Read the complete javadocs for more API details.

### Version 2.x (under development)

This streams library for Java provides robust unification of the `InputStream`, `OutputStream`, `ByteChannel` and `ByteBuffer` abstractions under a comprehensive API. It's implementation is rooted in an earlier, more modest library that aimed only to provide a better byte stream API.

Examples of some of the things you can do with this API

```java
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
try (ReadStream r = Streams.stream(someInput)) {
	r.readInt();
	r.readChars();
	// etc.
}

// use an OutputStream as a WriteStream
try (WriteStream w = Streams.stream(someOutput)) {
	w.writeLong(1234567890123456789L);
	w.writeChars("Example");
	// etc.
}

// use a ReadableByteChannel as a ReadStream
try (ReadStream r = Streams.stream(someReadableChannel)) {
	r.fillBuffer(buffer);
	// etc.
}

// use a ReadableByteChannel as a ReadStream
try (WriteStream r = Streams.stream(someWritableChannel)) {
	r.drainBuffer(buffer);
	// etc.
}

// access a byte array through a ReadStream
Streams.bytes(bytes).reader();

{ // accumulate bytes with a WriteStream
	StreamBytes bs = Streams.bytes();
	WriteStream w = bs.writer();
	w.writeDouble(0.35);
	w.close();
	byte[] bytes = bs.bytes();
	// use the bytes here
}

{ // accumulate byte data with a WriteStream and read it back
	StreamBytes bs = Streams.bytes();
	WriteStream w = bs.writer();
	w.writeChar('ζ'); // u03b6
	ReadStream r = bs.reader(); // closes the writer
	r.readByte(); // 03
	r.readByte(); // b6
	r.close();
}

// expose a write stream as an output stream
Streams.bytes().writer().asOutputStream();

// limit the number bytes that can be read from a stream
Streams.bytes(128).writer().bounded(64); // write only 64 bytes

// limit the number of bytes that can be read an input stream
Streams.stream(someInput).bounded(1024).asInputStream(); // 1k only

// convert an input stream into a channel...
Streams.stream(someInput).asChannel();

// ... and vice versa
Streams.stream(someReadableChannel).asInputStream();

// create a stream using a lambda
List<Byte> values = new ArrayList<Byte>();
try (WriteStream r = v -> values.add(v)) {
	// the stream has all the supporting methods, eg.
	r.bounded(10);
	r.drainBuffer(buffer);
}

// use a lightweight serialization API to read & write composite values
Producer<Point> prd = someReader.readWith(r -> new Point(r.readInt(), r.readInt()));
Point pt1 = prd.produce();
Point pt2 = prd.produce();
// etc.
Consumer<Point> con = someWriter.writeWith((p,w) -> {w.writeInt(p.x); w.writeInt(p.y);});
con.consume(pt1);
con.consume(pt2);
```


Usage
-----

### Version 1.x

The streams library is available from the Maven central repository:

> Group ID:    `com.tomgibara.streams`
> Artifact ID: `streams`
> Version:     `1.1.0`

The Maven dependency being:

    <dependency>
      <groupId>com.tomgibara.streams</groupId>
      <artifactId>streams</artifactId>
      <version>1.1.0</version>
    </dependency>

Release History
---------------

**2015.07.27** Version 1.1.0

 * Added new WrappedReadStream and WrappedWriteStream
 * Added getDigest() to DigestWriteStream

**2015.07.20** Version 1.0.0

Initial release
