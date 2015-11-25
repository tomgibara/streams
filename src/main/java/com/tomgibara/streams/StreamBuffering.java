package com.tomgibara.streams;

/**
 * Indicates a streams preferred buffering strategy. In the descriptions below,
 * <code>direct</code> refers to the direct allocation strategy that can be used
 * to create <code>ByteBuffer</code> instances.
 * 
 * @author Tom Gibara
 *
 * @see ReadStream#getBuffering()
 * @see WriteStream#getBuffering()
 */

public enum StreamBuffering {

	/**
	 * Indicates that, with the use of any buffer, there will be no bulk reads or writes.
	 */
	UNSUPPORTED,

	/**
	 * Indicates that a stream operates most efficiently with direct buffering.
	 */
	PREFER_DIRECT,

	/**
	 * Indicates that a stream operates equivalently well with either direct or non-direct (ie. heap-allocated) buffering.
	 */
	PREFER_ANY,

	/**
	 * Indicates that a stream operates most efficiently with non-direct (ie. heap allocated) buffering.
	 */
	PREFER_INDIRECT,
	
	
}
