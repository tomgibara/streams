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
 * Converts an object into byte data.
 *
 * @author Tom Gibara
 *
 * @param <T> the type of object that will be the source of data
 */

@FunctionalInterface
public interface StreamSerializer<T> {

	/**
	 * Called to serialize an object instance to a stream.
	 *
	 * @param value
	 *            the object to be serialized
	 * @param stream
	 *            the stream to which the object should be serialized
	 */

	void serialize(T value, WriteStream stream);

}
