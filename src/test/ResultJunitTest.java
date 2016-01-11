package test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

import bean.MessageResult;
import bean.Result;

public class ResultJunitTest {
	@Test
	public void testFillNoData(){
		List<MessageResult> messageResults=new ArrayList<MessageResult>();
		for(int i=0;i<5;i++){
			messageResults.add(new MessageResult(10,i));
		}
		messageResults.add(new MessageResult(10,7));
		//filled with 0 1 2 3 4 7
		Result result=new Result();
		result.setMessageResults(messageResults);
		int totalSize=8;
		result.fillNoData(totalSize);
		for(int i=0;i<totalSize;i++){
			MessageResult messageResult=messageResults.get(i);
			if(messageResult.getId()>=5 && messageResult.getId()<7)
				assertEquals(messageResult.getTime(),-1);
			else
				assertEquals(messageResult.getTime(),10);
		}
	}
	
	@Test
	public void testOrderMessageResults(){
		List<MessageResult> messageResults=new ArrayList<MessageResult>();
		for(int i=0;i<5;i++){
			messageResults.add(new MessageResult(10,4-i));
		}
		//filled with 4 3 2 1 0
		Result result=new Result();
		result.setMessageResults(messageResults);
		result.orderMessageResults();
		for(int i=0;i<4;i++){
			assertTrue(messageResults.get(i).getId()<messageResults.get(i+1).getId());
		}
	}
}
