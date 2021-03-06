package bean;

public class Flow {
	private String consumer;
	private String producer;
	private double frequency;
	// in bits
	private int messageLoad;
	
	// producer processing time - simulated time it would take for producer to answer  
	private double processTime;
	private int start;
	private int stop;
	public String getConsumer() {
		return consumer;
	}
	public void setConsumer(String consumer) {
		this.consumer = consumer;
	}
	public String getProducer() {
		return producer;
	}
	public void setProducer(String producer) {
		this.producer = producer;
	}
	public double getFrequency() {
		return frequency;
	}
	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}
	public int getMessageLoad() {
		return messageLoad;
	}
	public void setMessageLoad(int messageLoad) {
		this.messageLoad = messageLoad;
	}
	public double getProcessTime() {
		return processTime;
	}
	public void setProcessTime(double processTime) {
		this.processTime = processTime;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getStop() {
		return stop;
	}
	public void setStop(int stop) {
		this.stop = stop;
	}
	public Flow(){
		
	}
	public Flow(String consumer, String producer, double frequency,
			int messageLoad, double processTime, int start, int stop) {
		super();
		this.consumer = consumer;
		this.producer = producer;
		this.frequency = frequency;
		this.messageLoad = messageLoad;
		this.processTime = processTime;
		this.start = start;
		this.stop = stop;
	}
	
}
