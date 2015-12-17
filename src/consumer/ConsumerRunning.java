package consumer;

import java.io.IOException;
import java.rmi.RemoteException;

import javax.sql.rowset.spi.SyncResolver;

import org.apache.axis.AxisFault;

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
	public ConsumerRunning(int tid, Result result) {
		// TODO Auto-generated constructor stub
		this.processTime = 0;
		this.tid = tid;
		this.result = result;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Thread "+tid+" Start");
		
		while(true){
			try {
				synchronized (Thread.currentThread()) {
					Thread.currentThread().wait();
				}
				MessageResult messageResult = new MessageResult();
				messageResult.setId(messageId);
				ProducerProxy proxy = new ProducerProxy();
				Producer producer = proxy.getProducer();
				System.out.println(tid+ " Waking Up");
				
				long start = System.currentTimeMillis();
				producer.pingpong(processTime);
				long end = System.currentTimeMillis();
				
				//Ajout du temps pour le message Result
				messageResult.setTime(end-start);
				
				
				//Ajout du message Result à la list des Result
				synchronized (result) {
						result.getMessageResults().add(messageResult);
				}
		
			}catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				if(e instanceof AxisFault){
						MessageResult messageResult = new MessageResult();
						messageResult.setTime(-1);
						messageResult.setId(messageId);
						result.getMessageResults().add(messageResult);
						e.printStackTrace();
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
