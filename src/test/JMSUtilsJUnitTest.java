package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import mq.JMSUtils;

import org.junit.Test;
import org.mockito.Mockito;


public class JMSUtilsJUnitTest {
	//test if getInstance generate connection,topic,session,msgConsumer and msgProducer
	@Test
	public void testGetInstance() throws Exception{
		try {
			JMSUtils toBeTested=JMSUtils.getInstance();
			assertNotNull(toBeTested.getTopic());
			assertNotNull(toBeTested.getConnection());
			assertNotNull(toBeTested.getSession());
			assertNotNull(toBeTested.getMsgConsumer());
			assertNotNull(toBeTested.getMsgProducer());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//test receive functionality, to do that i mocked messageConsumer and textMessage
	@Test
	public void testReceive() throws Exception{
		MessageConsumer msgConsumer = Mockito.mock(MessageConsumer.class);
		TextMessage textmessage =  Mockito.mock(TextMessage.class);
		when(textmessage.getText()).thenReturn("message test");
		when(msgConsumer.receive()).thenReturn(textmessage);
		JMSUtils jmsutils=JMSUtils.getInstance();
		jmsutils.setMsgConsumer(msgConsumer);
		assertEquals(jmsutils.receive(),"message test");

	}
	
	@Test
	public void testSend() throws IOException, JMSException{
		JMSUtils toBeTested=JMSUtils.getInstance();
		MessageProducer msgProducer = Mockito.mock(MessageProducer.class);
		toBeTested.setMsgProducer(msgProducer);
		toBeTested.send("new message");
		verify(msgProducer).send((Message)anyObject());
	}
}
