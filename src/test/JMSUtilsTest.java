package test;

import java.io.IOException;

import javax.jms.JMSException;

import mq.JMSUtils;

public class JMSUtilsTest {
	public static void main(String[] args) throws IOException, JMSException {
		JMSUtils jms = JMSUtils.getInstance();
		jms.startConnection();
		// Send 
		System.out.println("Sending message..");
		jms.send("Hello I am (consumer) number 1");
		// Receive
		System.out.println("Wait for incoming message..");
		String message = jms.receive();
		System.out.println("Message received: " + message);
		jms.stopConnection();
	}
}
