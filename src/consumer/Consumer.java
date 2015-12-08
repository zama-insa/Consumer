package consumer;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.TimeoutException;

import javax.jms.JMSException;

import mq.JMSUtils;
import web.Producer;
import web.ProducerProxy;

public class Consumer {

	
	private static final String EXCHANGE_NAME = "scenario";
	
	public static void main(String[] args) throws JMSException, IOException{
		JMSUtils jmsUtils = JMSUtils.getInstance();

		 System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		 int processTime;
		 while (true) {
			 String message = jmsUtils.receive();
	
			 System.out.println(" [x] Received :" + message + "'");
			 
			 processTime = Integer.parseInt(message);
			 
			 ProducerProxy proxy = new ProducerProxy();
			 Producer producer = proxy.getProducer();
	
			 System.out.println(producer.pingpong(processTime));

		 }

	}
}
