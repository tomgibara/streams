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
