package com.goulart.criptolib;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ISO8564Format0Test {
	
	@Test
	public void encode () {
		String pan = "43219876543210987";
		String pin = "1234";
		
		String pinBlock = ISO8564Format0.encode(pin, pan);
		
		assertEquals(pinBlock, "0412AC89ABCDEF67");
	}
	
	@Test
	public void decode () {
		String pan = "43219876543210987";
		String pinBlock = "0412AC89ABCDEF67";
		
		String pin = ISO8564Format0.decode(pinBlock, pan);
		
		assertEquals(pin, "1234");
	}
}
