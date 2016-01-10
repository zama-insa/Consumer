package consumer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
	private static int index;
	static int messageId;
	
	public static void initConsumer(int consumer){
		String input1 ="java.naming.factory.initial = org.apache.activemq.jndi.ActiveMQInitialContextFactory";
		String input2 = "java.naming.provider.url = tcp://localhost:6161";
		String input3 ="queue.TEST.FOO = TEST.FOO";
		String input4 = "topic.TEST.BAR = Consumer."+consumer;
		try {
			FileWriter fw = new FileWriter(new File("resources/jndi.properties"));
			fw.write(input1+"\n"+input2+"\n"+input3+"\n"+input4);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws JMSException, IOException{
		
		
		
		logger.info("Start of the Consumer");
		
		
		properties = getProperties();
		mapper = new ObjectMapper();
		
		int consumerNumber = Integer.parseInt(args[0]);
		int poolnumber =  Integer.parseInt(args[1]);
		
		initConsumer(consumerNumber);
		//Gather JMS instance
		JMSUtils jmsUtils = JMSUtils.getInstance();
		
		int processTime;
		
		int size;
		
		Result result = new Result();

		//create Runnable
		List<ConsumerRunning> consumerRunning = createConsumerRunnings(poolnumber, result);
		
		// Create the pool of Threads
		List<Thread> pool = createPoolThread(consumerRunning);
		
		
		logger.info("Creation of "+poolnumber+" Threads");
		
		//Start of every Threads
		startThread(pool);
		
		logger.info("Threads Started and in Wait State");
		
		
		//Create Scheduler
		ScheduledExecutorService scheduledExecutorService =Executors.newScheduledThreadPool(poolnumber);
		
		while (true) {
			
			//Start of JMS Connection and wait for a new Scenario
			jmsUtils.startConnection();
			String message = jmsUtils.receive();
			
			jmsUtils.stopConnection();
			logger.info("New Flow received");
			
			//Set the Global variable to 0
			messageId = 0;
			index =0;
			
			// Get The flow from Json
			flow = mapper.readValue(message,Flow.class);
			
			//Get the name of the producer
			ProducerServiceLocator.producerName = flow.getProducer();

			//Get the processTime
			processTime = (int) flow.getProcessTime();
			
			//Get The size of the Message
			size = (int)flow.getMessageLoad();
			
			
			//Create The List of Result
			result.setMessageResults(new ArrayList<MessageResult>());
				
			
			
			//Set the processTime to all the Sender(ConsummerRunnings)
			for(int i= 0; i<poolnumber;i++){
				consumerRunning.get(i).setProcessTime(processTime);
				consumerRunning.get(i).setSize(size);
			}
			
			

			
			logger.info("Job Start");
			int period = (int) ((1/flow.getFrequency())*1000);
			final ScheduledFuture<?> scheduler = scheduledExecutorService.scheduleAtFixedRate(
					new Runnable (){
						public void run(){
							synchronized (pool.get(index)) {
								consumerRunning.get(index).setMessageId(Consumer.messageId);
								messageId++;
								logger.info("Index Thread" + index);
								pool.get(index).notify();
								index = (index+1)%poolnumber;								
							}

					}
			},flow.getStart()*1000, period, TimeUnit.MILLISECONDS);
				
				
				//System.out.println(period);
				//System.out.println(flow.getStop());
				
				
			scheduledExecutorService.schedule(new Runnable() {
				public void run() { scheduler.cancel(true); }
			}, (flow.getStop()*1000), TimeUnit.MILLISECONDS);
				//System.out.println("TEST");
			//}
			try {
				Thread.sleep((long) (flow.getStop()*1000+flow.getProcessTime()+1000));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//set Consumer
			result.setConsumer(consumerNumber);
		
			
			logger.info("Job Done");
			
			
			//fill no Data
			
			result.fillNoData((int) (flow.getFrequency()*(flow.getStop()-flow.getStart())));
			
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
	
	public static void  startThread(List<Thread> pool){
		for(Thread t : pool){
			t.start();
		}
	}
	
	public static List<Thread> createPoolThread(List<ConsumerRunning> consummerRunning){
		List<Thread> pool = new ArrayList<Thread>();
		for (int i=0; i<consummerRunning.size();i++){
			pool.add(new Thread(consummerRunning.get(i)));
		}
		return pool;
	}
	
	
	
	public static List<ConsumerRunning> createConsumerRunnings (int poolnumber,Result result){
		List<ConsumerRunning> consumerRunnings = new ArrayList<ConsumerRunning> ();
		for(int i=0;i<poolnumber;i++){
			consumerRunnings.add(new ConsumerRunning(result, i));
		}
		return consumerRunnings;
	}
	
	
}


