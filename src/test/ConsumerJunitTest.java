package test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;

import mq.JMSUtils;

import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import bean.Flow;
import bean.Result;
import consumer.Consumer;

public class ConsumerJunitTest {
	//i created a scheduler which works with 2 threads with a period of 100 ms and verify if i could kill it
	@Test
	public void testKillThreads() throws InterruptedException{
		final int toleranceInMs=500;
		ScheduledExecutorService scheduledExecutorService =Executors.newScheduledThreadPool(2);
		final ScheduledFuture<?> scheduler = scheduledExecutorService.scheduleAtFixedRate(
				new Runnable (){
					public void run(){
						synchronized (new Integer(1)) {
							//any dummy operation							
						}

				}
		},0,100,TimeUnit.MILLISECONDS);
		Consumer.killThreads(scheduler,3);
		Thread.sleep(3000+toleranceInMs);
		assertTrue(scheduler.isCancelled());
	}
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
	public void testStartThread(){
		List<Thread> pool=new ArrayList<Thread>();
		int poolnumber=5;
		for(int i=0;i<poolnumber;i++){
			pool.add(Mockito.mock(Thread.class));
		}
		Consumer.startThread(pool);
		for(Thread t:pool){
			verify(t,times(1)).start();
		}
	}
}
