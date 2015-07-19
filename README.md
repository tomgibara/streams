Streams
=======

This streams library for java provides a modest abstraction for
streaming core Java types as byte values.

Overview
--------

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

All the classes are available in the `com.tomgibara.streams` package. 


Usage
-----

The streams library is available from the Maven central repository:

> Group ID:    `com.tomgibara.streams`
> Artifact ID: `streams`
> Version:     `1.0.0`

The Maven dependency being:

    <dependency>
      <groupId>com.tomgibara.streams</groupId>
      <artifactId>streams</artifactId>
      <version>1.0.0</version>
    </dependency>

Release History
---------------

**2015.20.07** Version 1.0.0

Initial release
