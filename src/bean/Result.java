package bean;

import java.util.Collections;
import java.util.List;

public class Result {
	private List<MessageResult> messageResults;
	public int getConsumer() {
		return consumer;
	}

	public void setConsumer(int consumer) {
		this.consumer = consumer;
	}

	private int consumer;
	synchronized public List<MessageResult> getMessageResults() {
		
		return messageResults;
	}

	public void setMessageResults(List<MessageResult> time) {
		this.messageResults = time;
	}
	
	public void orderMessageResults(){
		Collections.sort(messageResults);
	}
}
