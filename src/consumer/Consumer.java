package consumer;

import java.io.File;
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

import org.apache.log4j.Logger;

import web.ProducerServiceLocator;
import bean.Flow;
import bean.MessageResult;
import bean.Result;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Consumer {

	//Logger
	private final static Logger logger = Logger.getLogger(Consumer.class);
	
	//Mapper to get Json from object
	private static ObjectMapper mapper=new ObjectMapper();
	
	
	private static Flow flow = new Flow();
	
	private static Properties properties;
	private static int index;
	static int messageId;
	
	public static void initConsumer(int consumer){
		String input = createJNDIinput(consumer);
		try {
			
			FileWriter fw = new FileWriter(new File("resources/jndi.properties"));
			fw.write(input);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String createJNDIinput(int consumer){
		String input1 ="java.naming.factory.initial = org.apache.activemq.jndi.ActiveMQInitialContextFactory";
		String input2 = "java.naming.provider.url = tcp://localhost:6161";
		String input3 ="queue.TEST.FOO = TEST.FOO";
		String input4 = "topic.TEST.BAR = Consumer."+consumer;
		return input1+"\n"+input2+"\n"+input3+"\n"+input4;
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
		//not used anymore
		//ScheduledExecutorService scheduledExecutorService =Executors.newScheduledThreadPool(poolnumber);
		
		while (true) {
			
			//Wait for the new flow
			flow=receiveFlow(jmsUtils);
			
			// Run Job
			runJob(flow,consumerRunning, pool,result );
			
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
			sendResult(jmsUtils,result);
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
	
	
	public static Flow receiveFlow(JMSUtils jmsUtils){
		Flow flow=null;
		try {
			//Start of JMS Connection and wait for a new Scenario
			jmsUtils.startConnection();
			String message = jmsUtils.receive();	
			jmsUtils.stopConnection();
			logger.info("New Flow received : " + message );
			// Get The flow from Json
			flow = mapper.readValue(message,Flow.class);
		} catch (JMSException | IOException e) {
			e.printStackTrace();
		}	
		return flow;
	}
	
	//added by zakaria,hope it wont break ur code arthur :p
	public static void sendResult(JMSUtils jmsUtils,Result result){
		String json;
		try {
			json = mapper.writeValueAsString(result);
			jmsUtils.startConnection();
			jmsUtils.send(json);
			jmsUtils.stopConnection();
			logger.info("Result Send");
		} catch (JsonProcessingException | JMSException e) {
			e.printStackTrace();
		}	
	}
	
	//added by zakaria,hope it wont break ur code arthur :p
	public static ScheduledFuture<?> scheduleThreads(List<Thread> pool,List<ConsumerRunning> consumerRunning,int period){
		
		////???? same scheduleExecutorService for run and cancel? ?????//////	
		
		//Create Scheduler
		ScheduledExecutorService scheduledExecutorService =Executors.newScheduledThreadPool(5);
		final ScheduledFuture<?> scheduler = scheduledExecutorService.scheduleAtFixedRate(
				new Runnable (){
					public void run(){
						synchronized (pool.get(index)) {
							int poolnumber=pool.size();
							consumerRunning.get(index).setMessageId(Consumer.messageId);
							messageId++;
							//logger.trace("Index Thread" + index);
							pool.get(index).notify();
							index = (index+1)%poolnumber;								
						}

				}
		},flow.getStart()*1000, period, TimeUnit.MILLISECONDS);
		return scheduler;
	}
	
	
	//added by zakaria,hope it wont break ur code arthur :p
	public static void killThreads(final ScheduledFuture<?> scheduler,int stop){
		//Create Scheduler
		
		//following line is like u put : Executors.newScheduledThreadPool(1);
		ScheduledExecutorService scheduledExecutorService =Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.schedule(new Runnable() {
			public void run() { scheduler.cancel(true); }
		}, (stop*1000), TimeUnit.MILLISECONDS);
	}
	
	
	/*
	 * Run Job when a flow is received
	 */
	public static void runJob(Flow flow, List<ConsumerRunning> consumerRunning,List<Thread> pool, Result result ){
		//Set the Global variable to 0
		messageId = 0;
		index =0;
		
		//Get the name of the producer
		ProducerServiceLocator.producerName = flow.getProducer();

		//Get the processTime
		int processTime = (int) flow.getProcessTime();
		
		//Get The size of the Message
		int size = (int)flow.getMessageLoad();
		
		
		//Create The List of Result
		result.setMessageResults(new ArrayList<MessageResult>());
			
		
		
		//Set the processTime to all the Sender(ConsummerRunnings)
		setConsumerRunnings(consumerRunning, processTime, size);
				
		logger.info("Job Start");
		
		//Get period in ms
		int period = (int) ((1/flow.getFrequency())*1000);	
		
		//Set the job with the period
		final ScheduledFuture<?> scheduler=scheduleThreads(pool, consumerRunning, period);
		
		//Stop the job
		killThreads(scheduler,flow.getStop());
		waitEndJob(flow.getStop()*1000+flow.getProcessTime()+1000);
		//Thread need to wait the end of the job
		
	}
	
	public static void waitEndJob(double time){
		try {
			Thread.sleep((long) (time));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static void setConsumerRunnings(List<ConsumerRunning> crs,int processTime, int size){
		for(int i= 0; i<crs.size();i++){
			crs.get(i).setProcessTime(processTime);
			crs.get(i).setSize(size);
		}
	}
}


