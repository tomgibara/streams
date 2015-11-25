package com.tomgibara.streams;

//TODO document
public enum StreamBuffering {

	PREFER_INDIRECT,
	PREFER_DIRECT,
	PREFER_ANY,
	
	// indicates that, even with the use of a buffer, there will be no bulk reads or writes
	UNSUPPORTED;
	
}
