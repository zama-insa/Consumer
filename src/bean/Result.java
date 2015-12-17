package bean;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Result {
	public List<MessageResult> messageResults;
	public int consumer;
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
