package test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
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
import consumer.ConsumerRunning;

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
	public void testcreatePoolThread(){
		List<ConsumerRunning> cRunning  = new ArrayList<ConsumerRunning>();
		for(int i =0;i<10;i++){
			cRunning.add(new ConsumerRunning(new Result(), i));
		}
		List<Thread> pool= Consumer.createPoolThread(cRunning);
		assertEquals(pool.size(),10);
	}
	
	@Test
	public void testCreateConsumerRunning(){
		List<ConsumerRunning> crs = Consumer.createConsumerRunnings(10, new Result());
		assertEquals(10, crs.size());
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
	
	@Test
	public void testsetConsumerRunning(){
		List<ConsumerRunning> crs = Consumer.createConsumerRunnings(10, new Result());
		Consumer.setConsumerRunnings(crs, 10, 10);
		for(ConsumerRunning cr : crs){
			assertEquals(cr.getProcessTime(),10);
			assertEquals(cr.getSize(),10);
		}
	}
	
	@Test
	public void testWaitEndJob(){
		long start = System.currentTimeMillis();
		Consumer.waitEndJob(1000);
		long end = System.currentTimeMillis();
		assertEquals((end-start),1000,1000);
	}
	
	@Test
	public void testcreateJNDIinput(){
		String input = Consumer.createJNDIinput(5);
		assertEquals(input,"java.naming.factory.initial = org.apache.activemq.jndi.ActiveMQInitialContextFactory\njava.naming.provider.url = tcp://localhost:6161\nqueue.TEST.FOO = TEST.FOO\ntopic.TEST.BAR = Consumer.5");
	}

	@Test
	public void testSetConsumerRunningMessageId(){
		Consumer.messageId =0;
		ConsumerRunning cr = new ConsumerRunning(new Result(), 50);
		Consumer.setConsumerMessageId(cr);
		assertEquals(Consumer.messageId,1);
		assertEquals(cr.getMessageId(),0);
	}
	
}
