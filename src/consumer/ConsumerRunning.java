package consumer;

import java.io.IOException;
import java.rmi.RemoteException;

import javax.sql.rowset.spi.SyncResolver;

import org.apache.axis.AxisFault;
import org.apache.log4j.Logger;

import bean.MessageResult;
import bean.Result;
import bean.Scenario;
import web.Producer;
import web.ProducerProxy;
import mq.JMSUtils;

public class ConsumerRunning implements Runnable{
	
	private int processTime;
	private Result result;
	private int messageId;
	private int size;
	private int tid;
	
	private final static Logger logger = Logger.getLogger(ConsumerRunning.class);
	
	
	public ConsumerRunning(Result result,int tid) {
		// TODO Auto-generated constructor stub
		this.result = result;
		this.tid=tid;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		logger.info(System.currentTimeMillis()+" Thread "+tid+" Started");
		String word;
		while(true){
			
			//Wait State
			try {
				synchronized (Thread.currentThread()) {
					Thread.currentThread().wait();
				}
				
				logger.info("Thread " + tid +" wake up");
				//Create a new Message Result
				MessageResult messageResult = new MessageResult();
				//Set the message id for the new MessageResult
				messageResult.setId(this.messageId);
				
				
				//Create Producer object to do the request 
				ProducerProxy proxy = new ProducerProxy();
				Producer producer = proxy.getProducer();
				
				//Do the request to SOAP producer
				long start = System.currentTimeMillis();
				
				word = createWord(size);
				
				
				producer.pingpong(processTime,word);
				logger.info("Message "+ Consumer.messageId+" send");
				long end = System.currentTimeMillis();
				
				//ADD time to the Message Result
				messageResult.setTime(end-start);
				
				
				//Add MessageResult to Result 
			synchronized (result) {
					result.getMessageResults().add(messageResult);
					//logger.info(result.getMessageResults().toString());
			}
		
			
			} catch (RemoteException e) {
				//If Producer Error
				if(e instanceof AxisFault){
						//Create MessageResult
						MessageResult messageResult = new MessageResult();
						//Set Time to -1 and message Id
						messageResult.setTime(-1);
						messageResult.setId(messageId);
						//Add MessageResult to Result
						result.getMessageResults().add(messageResult);
						//logger.info(result.getMessageResults().toString());
						//e.printStackTrace();
					//}
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}

	public int getProcessTime() {
		return processTime;
	}

	public void setProcessTime(int processTime) {
		this.processTime = processTime;
		//System.out.println(" NEW Process Time");
	}
	public int getMessageId() {
		return messageId;
	}
	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}
	
	public String createWord(int size){
		String result ="";
		for (int i =1;i<size;i++){
			result+="z";
		}
		return result;
	}
	
	
}
