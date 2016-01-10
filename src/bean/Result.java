package bean;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public void fillNoData(int totalSize){
		Map<Integer,MessageResult> map = new HashMap<Integer,MessageResult>();
		
		for(MessageResult mr : this.getMessageResults()){
			map.put(mr.id, mr);
		}
		
		for(int j = 0 ; j<totalSize;j++){
			if(!map.containsKey(j)){
				this.getMessageResults().add(new MessageResult(-1,j));
			}
		}
	}
}
