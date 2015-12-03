package consumer;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

import web.Producer;
import web.ProducerProxy;

public class Consumer {

	
	private static final String EXCHANGE_NAME = "scenario";
	
	public static void main(String[] args){
		
		Connection connection = null;

		 Channel channel = null;

		 try {

		 ConnectionFactory factory = new ConnectionFactory();

		 factory.setHost("localhost");

		 connection = factory.newConnection();

		 channel = connection.createChannel();

		 channel.exchangeDeclare(EXCHANGE_NAME, "topic");

		 String queueName = channel.queueDeclare().getQueue();
		 String bindingKey = "consumer.*";

		 channel.queueBind(queueName, EXCHANGE_NAME, bindingKey);

		 System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		 QueueingConsumer consumer = new QueueingConsumer(channel);

		 channel.basicConsume(queueName, true, consumer);
		 int processTime;
		 while (true) {
			 QueueingConsumer.Delivery delivery = consumer.nextDelivery();
	
			 String message = new String(delivery.getBody(), "UTF-8");
	
			 String routingKey = delivery.getEnvelope().getRoutingKey();
	
			 System.out.println(" [x] Received "+"-" 
	
			 + routingKey + "':'" + message + "'");
			 
			 processTime = Integer.parseInt(message);
			 
			 ProducerProxy proxy = new ProducerProxy();
			 Producer producer = proxy.getProducer();
	
			 System.out.println(producer.pingpong(processTime));

		 }



		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ShutdownSignalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConsumerCancelledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
