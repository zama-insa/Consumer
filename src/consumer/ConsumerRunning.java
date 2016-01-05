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
	private int tid;
	private Result result;
	private int messageId;
	
	private final static Logger logger = Logger.getLogger(ConsumerRunning.class);

	
	
	public ConsumerRunning(int tid, Result result) {
		// TODO Auto-generated constructor stub
		this.processTime = 0;
		this.tid = tid;
		this.result = result;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		logger.info("Threads " + tid +" started");
		
		while(true){
			
			//Wait State
			try {
				synchronized (Thread.currentThread()) {
					Thread.currentThread().wait();
				}
				
				//Create a new Message Result
				MessageResult messageResult = new MessageResult();
				//Set the message id for the new MessageResult
				messageResult.setId(messageId);
				
				
				//Create Producer object to do the request 
				ProducerProxy proxy = new ProducerProxy();
				Producer producer = proxy.getProducer();
				
				logger.info("Thread "+tid+ " Waking Up");
				//Do the request to SOAP producer
				long start = System.currentTimeMillis();
				producer.pingpong(processTime);
				long end = System.currentTimeMillis();
				
				//ADD time to the Message Result
				messageResult.setTime(end-start);
				
				
				//Add MessageResult to Result 
				synchronized (result) {
						result.getMessageResults().add(messageResult);
				}
		
			}catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
						//e.printStackTrace();
					//}
				}
				
			}
		}
		
		
	}

	public int getProcessTime() {
		return processTime;
	}

	public void setProcessTime(int processTime) {
		this.processTime = processTime;
		System.out.println(tid+" NEW Process Time");
	}
	public int getMessageId() {
		return messageId;
	}
	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}
	
}
