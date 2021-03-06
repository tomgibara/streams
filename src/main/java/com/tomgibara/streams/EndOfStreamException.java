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

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * <p>
 * Instances of this exception are thrown when a {@link ReadStream} encounters
 * an end-of-stream condition. {@link WriteStream} implementations with
 * fixed-size output may also choose to employ this exception type to indicate
 * that the stream is 'full'.
 *
 * <p>
 * In the interests of efficiency, stream implementations are encouraged to
 * throw an instance of this exception using the {@link #raise()} method. In
 * doing so, users of the library may enjoy much faster performance when
 * end-of-stream conditions are encountered by setting the system property
 * <code>com.tomgibara.streams.eosShared</code> to the value <code>"true"</code>.
 *
 * <p>When this is done, a single shared {@link EndOfStreamException} instance
 * will be used, gaining a significant improvement in performance for
 * applications that encounter them often, but at the loss of stacktraces.
 *
 * @author Tom Gibara
 *
 * @see #raise()
 *
 */

public class EndOfStreamException extends StreamException {

	private static final long serialVersionUID = 5128955277305418544L;

	private static final String SHARED_PROPERTY = "com.tomgibara.streams.eosShared";

	private static final EndOfStreamException EOS = isShared() ? new EndOfStreamException("EOS", null, false, false) : null;

	private static boolean isShared() {
		PrivilegedAction<Boolean> action = () -> { return Boolean.valueOf(System.getProperty(SHARED_PROPERTY)); };
		return AccessController.doPrivileged(action);
	}

	/**
	 * <p>
	 * Returns an {@link EndOfStreamException}. This method may return a shared
	 * instance of the exception that provides better performance for streams
	 * that need to indicate an end-of-stream condition.
	 *
	 * <p>
	 * Enabling shared-instance end-of-stream reporting for the library is done
	 * by setting the system property
	 * <code>com.tomgibara.streams.eosShared</code> to the value
	 * <code>"true"</code>.
	 *
	 * @return a (possibly cached) instance of the exception
	 */

	public static EndOfStreamException instance() {
		return EOS == null ? new EndOfStreamException() : EOS;
	}

	/**
	 * <p>
	 * Raises an {@link EndOfStreamException}. This method may return a shared
	 * instance of the exception that provides better performance for streams
	 * that need to indicate an end-of-stream condition.
	 *
	 * <p>
	 * Enabling shared-instance end-of-stream reporting for the library is done
	 * by setting the system property
	 * <code>com.tomgibara.streams.eosShared</code> to the value
	 * <code>"true"</code>.
	 *
	 * @deprecated
	 * @throws EndOfStreamException
	 *             always
	 * @see EndOfStreamException#instance()
	 */

	@Deprecated
	public static void raise() throws EndOfStreamException {
		if (EOS != null) throw EOS;
		throw new EndOfStreamException();
	}

	public EndOfStreamException() { }

	public EndOfStreamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EndOfStreamException(String message, Throwable cause) {
		super(message, cause);
	}

	public EndOfStreamException(String message) {
		super(message);
	}

	public EndOfStreamException(Throwable cause) {
		super(cause);
	}

}
