package consumer;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.TimeoutException;

import javax.jms.JMSException;

import com.fasterxml.jackson.databind.ObjectMapper;

import bean.Flow;
import bean.Scenario;
import mq.JMSUtils;
import web.Producer;
import web.ProducerProxy;
import web.ProducerServiceLocator;

public class Consumer {
	
	
	
	private static final String EXCHANGE_NAME = "scenario";
	private static ObjectMapper mapper;
	private static Flow flow = new Flow();
	public static void main(String[] args) throws JMSException, IOException{
		mapper = new ObjectMapper();
		
		JMSUtils jmsUtils = JMSUtils.getInstance();

		 System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		 int processTime;
		 
		 while (true) {
			 jmsUtils.startConnection();
			 String message = jmsUtils.receive();
			 jmsUtils.stopConnection();
			 System.out.println(" [x] Received :" + message + "'");
			 
			 flow = mapper.readValue(message,Flow.class);
			 ProducerServiceLocator.producerName = flow.getProducer();
			 
			 
			 processTime = (int) flow.getProcessTime();
			 
			 ProducerProxy proxy = new ProducerProxy();
			 Producer producer = proxy.getProducer();
			 
			 System.out.println(producer.pingpong(processTime));

		 }

	}
}
