package bean;

public class MessageResult implements Comparable<MessageResult>{
	public long time;
	public int id;
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public int getId() {
		return id;
	}
	public MessageResult(long time, int id) {
		this.time = time;
		this.id = id;
	}
	public MessageResult(){
		
	}
	public void setId(int id) {
		this.id = id;
	}
	@Override
	public int compareTo(MessageResult arg0) {
		// TODO Auto-generated method stub
		int result;
		if(arg0.getId()<this.id){
			result = 1;
		}else if(arg0.getId()==this.id){
			result = 0;
		}else{
			result =-1;
		}
		return result;
	}
	@Override
	public String toString() {
		return "["+ id + ", "+ time + "]";
	}
	
}
