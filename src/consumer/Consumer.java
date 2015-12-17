package consumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.jms.JMSException;

import com.fasterxml.jackson.databind.ObjectMapper;

import bean.Flow;
import bean.MessageResult;
import bean.Result;
import mq.JMSUtils;
import web.ProducerServiceLocator;

public class Consumer {

	
	private static ObjectMapper mapper;
	private static Flow flow = new Flow();
	private static Properties properties;
	public static void main(String[] args) throws JMSException, IOException{
		properties = getProperties();
		mapper = new ObjectMapper();
		
		
		//Recuperation instance de jmsUtils
		JMSUtils jmsUtils = JMSUtils.getInstance();

		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
		
		
		int processTime;
		Result result = new Result();
		
		// Creation et Parametrage du pool de Threads
		List<Thread> pool = new ArrayList<Thread>();
		int poolNumber = Integer.parseInt(getProperties().getProperty("pool.threads"));
		
		List<ConsumerRunning> consumerruns = new ArrayList<ConsumerRunning>();
		for(int i = 0; i<poolNumber; i++){
			consumerruns.add(new ConsumerRunning(i,result));
		}
		for (int i=0; i<poolNumber;i++){
			pool.add(new Thread(consumerruns.get(i)));
		}
		
		
		//Start de tout les Threads
		for(Thread t : pool){
			t.start();
		}
		
		
		while (true) {
			//Start de la connection JMS et attente d'un message
			jmsUtils.startConnection();
			String message = jmsUtils.receive();
			jmsUtils.stopConnection();
			System.out.println(" [x] Received :" + message + "'");
			
			
			// Creation du Flow 
			flow = mapper.readValue(message,Flow.class);
			
			//Recuperation du nom du Producer, Parametrage de Producer 
			ProducerServiceLocator.producerName = flow.getProducer();

			
			processTime = (int) flow.getProcessTime();
			
			int countg = 0;
			
			result.setMessageResults(new ArrayList<MessageResult>());
	
			
			
			int indexThread = 0;
			//Parametrage du processTime pour ce job
			for(int i= 0; i<poolNumber;i++){
				consumerruns.get(i).setProcessTime(processTime);
			}
			double start = System.currentTimeMillis();
			double end = start;
			int countMessage = 0;
			while((end-start)<flow.getStop()*1000){

				int i =0;
			
				long round = System.currentTimeMillis();
				long endRound = System.currentTimeMillis();
				
				while((endRound-round)<1000){
					if(i<flow.getFrequency()){
						//Synchronized sur le thread
						synchronized (pool.get(indexThread)) {
							//Set Id message
							System.out.println(countMessage);
							consumerruns.get(indexThread).setMessageId(countMessage);
							countMessage++;
							//Wake up the Thread
							pool.get(indexThread).notify();
							System.out.println("Thread " + indexThread);
						}
						
						//Get the next Thread to waking up
						indexThread = (indexThread+1)%poolNumber;
						
					}
					i++;

					endRound = System.currentTimeMillis();
				}
				System.out.println("Round "+countg);

				++countg;
				//System.out.println("test");
				//System.out.println(end-start);
				end = System.currentTimeMillis();
			}
			
			//set Consumer
			result.consumer=1;

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
			System.out.println((end-start));
			System.out.println("COUNT : " + countg); 
			//synchronized (result) {
			result.orderMessageResults();
			System.out.println(result.getMessageResults().toString());
			System.out.println(result.getMessageResults().size());
				
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


