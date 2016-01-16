package test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.jms.JMSException;

import mq.JMSUtils;

import org.junit.Test;
import org.mockito.Mockito;

import bean.Flow;
import bean.Result;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import consumer.Consumer;

public class ConsumerIntegrationTest {

	@Test
	public void testReceiveFlow() throws JMSException, IOException{
		Flow staticFlow=new Flow("c","p",10,10,2,1,10);
		ObjectMapper mapper=new ObjectMapper();
		String messageFlow=mapper.writeValueAsString(staticFlow);
		JMSUtils jmsutils = Mockito.mock(JMSUtils.class);
		when(jmsutils.receive()).thenReturn(messageFlow);
		Flow flow=Consumer.receiveFlow(jmsutils);
		assertEquals(flow.getConsumer(),staticFlow.getConsumer());
		assertEquals(flow.getFrequency(),staticFlow.getFrequency(),0);
		assertEquals(flow.getMessageLoad(),staticFlow.getMessageLoad());
		assertEquals(flow.getProcessTime(),staticFlow.getProcessTime(),0);
		assertEquals(flow.getProducer(),staticFlow.getProducer());
		assertEquals(flow.getStart(),staticFlow.getStart());
		assertEquals(flow.getStop(),staticFlow.getStop());
		
	}
	@Test
	public void testSendResult() throws JMSException, JsonProcessingException{
		JMSUtils jmsutils = Mockito.mock(JMSUtils.class);
		Result result=new Result();
		result.setConsumer(1);
		ObjectMapper mapper=new ObjectMapper();
		String resultJson=mapper.writeValueAsString(result);
		Consumer.sendResult(jmsutils,result);
		verify(jmsutils).startConnection();
		verify(jmsutils).send(resultJson);
		verify(jmsutils).stopConnection();
	}
	
	@Test
	public void testDurationJob(){
		
	}
	
	@Test 
	public void testNumberResult(){
		
	}
}
