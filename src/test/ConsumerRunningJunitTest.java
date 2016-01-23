package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import bean.Result;
import consumer.ConsumerRunning;

public class ConsumerRunningJunitTest {

	private ConsumerRunning cr;
	
	@Before
	public void init(){
		cr = new ConsumerRunning(new Result(), 10);
	}
	
	@Test 
	public void testCreateWord(){
		String result =cr.createWord(50);
		assertEquals(50,result.length());
	}
}
