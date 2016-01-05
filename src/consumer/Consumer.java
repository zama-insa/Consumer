package consumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.jms.JMSException;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import bean.Flow;
import bean.MessageResult;
import bean.Result;
import mq.JMSUtils;
import web.ProducerServiceLocator;

public class Consumer {

	//Logger
	private final static Logger logger = Logger.getLogger(Consumer.class);
	
	//Mapper to get Json from object
	private static ObjectMapper mapper;
	
	
	private static Flow flow = new Flow();
	
	private static Properties properties;
	
	public static void main(String[] args) throws JMSException, IOException{
		
		logger.info("Start of the Consumer");
		
		
		properties = getProperties();
		mapper = new ObjectMapper();
		
		
		//Gather JMS instance
		JMSUtils jmsUtils = JMSUtils.getInstance();
		
		int processTime;
		Result result = new Result();
		
		// Create the pool of Threads
		List<Thread> pool = new ArrayList<Thread>();
		int poolNumber = Integer.parseInt(getProperties().getProperty("pool.threads"));
		List<ConsumerRunning> consumerruns = new ArrayList<ConsumerRunning>();
		for(int i = 0; i<poolNumber; i++){
			consumerruns.add(new ConsumerRunning(i,result));
		}
		for (int i=0; i<poolNumber;i++){
			pool.add(new Thread(consumerruns.get(i)));
		}
		
		logger.info("Creation of "+poolNumber+" Threads");
		
		//Start of every Threads
		for(Thread t : pool){
			t.start();
		}
		
		logger.info("Threads Started and in Wait State");
		

		
		while (true) {
			//Start of JMS Connection and wait for a new Scenario
			jmsUtils.startConnection();
			String message = jmsUtils.receive();
			
			jmsUtils.stopConnection();
			
			logger.info("New Flow received");
			
			
			// Get The flow from Json
			flow = mapper.readValue(message,Flow.class);
			
			//Get the name of the producer
			ProducerServiceLocator.producerName = flow.getProducer();

			//Get the processTime
			processTime = (int) flow.getProcessTime();
			
			
			
			result.setMessageResults(new ArrayList<MessageResult>());
	
			
			
			int indexThread = 0;
			//Set the processTime to all the Sender(ConsummerRunnings)
			for(int i= 0; i<poolNumber;i++){
				consumerruns.get(i).setProcessTime(processTime);
			}
			
			//Set The Time
			long start = System.currentTimeMillis();
			long end = start;
			
			//Count Message
			int countMessage = 0;
			
			//Count The total number of messages send
			int countg = 0;
			//Sleep to respect Start 
			logger.info("Start The Job after waiting "+ flow.getStart()+" s");
			try {
				Thread.sleep(flow.getStart()*1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			logger.info("Job Start");
			// Start Job
			while((end-start)<flow.getStop()*1000){

				int i =0;
			
				long round = System.currentTimeMillis();
				long endRound = System.currentTimeMillis();
				//While for 1 second
				while((endRound-round)<1000){
					
					//Wake Up {Frequency} Threads
					if(i<flow.getFrequency()){
						
						
						
						//Synchronized sur le thread
						synchronized (pool.get(indexThread)) {
							//Set Id message
							//System.out.println(countMessage);
							consumerruns.get(indexThread).setMessageId(countMessage);
							countMessage++;
							//Wake up the Thread
							pool.get(indexThread).notify();
						}
						
						//Get the next Thread to waking up
						indexThread = (indexThread+1)%poolNumber;
						i++;
					}

					endRound = System.currentTimeMillis();
					
				}
				logger.info(i+" Message Send for this second");
				++countg;
				end = System.currentTimeMillis();
			}
			
			//set Consumer
			result.consumer=1;

			//Verify that every Thread is back to wait State
			pool.forEach(x->{
				while(x.getState()!=Thread.State.WAITING){
					try {
						Thread.sleep(10);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			logger.info("Job Done, Time:"+(end-start)+" COUNT:"+countg);
			
			//Sort the List of MessageResult
			result.orderMessageResults();
			logger.info("SIZE : " + result.getMessageResults().size());
			logger.info("Result : "+result.getMessageResults().toString());
			
			
			//Send back to the queue The list of MessageResult
			String json = mapper.writeValueAsString(result);
			jmsUtils.startConnection();
			jmsUtils.send(json);
			jmsUtils.stopConnection();
			logger.info("Result Send");
			//}
			//System.out.println(result.getTime().toString());
			
		}
	}
	
	public static Properties getProperties() {
		if (properties == null) {
			properties = new Properties();
			try {
				properties.load(Consumer.class.getClassLoader().getResourceAsStream("consumer.properties"));
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return properties;
	}
}


