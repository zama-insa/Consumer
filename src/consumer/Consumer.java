package consumer;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import javax.jms.JMSException;

import com.fasterxml.jackson.databind.ObjectMapper;

import bean.Flow;
import bean.Result;
import bean.Scenario;
import mq.JMSUtils;
import web.Producer;
import web.ProducerProxy;
import web.ProducerServiceLocator;

public class Consumer {



	private static ObjectMapper mapper;
	private static Flow flow = new Flow();
	public static void main(String[] args) throws JMSException, IOException{
		mapper = new ObjectMapper();

		JMSUtils jmsUtils = JMSUtils.getInstance();

		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		int processTime;
		Result result = new Result();
		while (true) {
			jmsUtils.startConnection();
			String message = jmsUtils.receive();
			jmsUtils.stopConnection();
			System.out.println(" [x] Received :" + message + "'");

			flow = mapper.readValue(message,Flow.class);
			ProducerServiceLocator.producerName = flow.getProducer();

			ProducerProxy proxy = new ProducerProxy();
			Producer producer = proxy.getProducer();
			processTime = (int) flow.getProcessTime();	
			double start = System.currentTimeMillis();
			double end = start;
			double round;
			int count = 0;
			int countg = 0;
			result.setTime(new ArrayList<Long>());
			long chronoStart;
			long chronoEnd;
			while((end-start)<flow.getStop()*1000){
				round = System.currentTimeMillis();
				end = System.currentTimeMillis();

				while(end-round<1000){
					if(count<flow.getFrequency()){
						chronoStart = System.currentTimeMillis();
						producer.pingpong(processTime);
						chronoEnd = System.currentTimeMillis();
						result.getTime().add(chronoEnd-chronoStart);
						countg++;
						count++;
					}
					end = System.currentTimeMillis();
				}
				//System.out.println(end-start);
				end = System.currentTimeMillis();
				
				count=0;
			}
			System.out.println(countg); 
			System.out.println(result.getTime().toString());

		}
	}
}


