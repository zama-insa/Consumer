package mq;

import java.io.IOException;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;

public class JMSUtils {

	private Connection connection;
	private Session session;
	private MessageConsumer msgConsumer;
	private MessageProducer msgProducer;
	private Topic topic;
	
	private JMSUtils() throws IOException {
		try {
		InitialContext init = new InitialContext();
		ConnectionFactory connectionFactory =
	            (ConnectionFactory) init.lookup("ConnectionFactory");
	    javax.jms.Queue queue = (javax.jms.Queue) init.lookup("TEST.FOO");
	    javax.jms.Topic topic = (javax.jms.Topic) init.lookup("TEST.BAR");
		this.topic = topic;
			connection = connectionFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			msgConsumer = session.createConsumer(topic);
			msgProducer = session.createProducer(queue);
		} catch (Exception e) {
			System.out.println(e.toString());
			throw new IOException(e);
		}
	}
	
	/**
	 * Must be called before any send or read operation
	 * @throws JMSException
	 */
	public void startConnection() throws JMSException {
		connection.start();
	}
	/**
	 * Should be called after local messaging operations are finished to save resources
	 * @throws JMSException
	 */
	public void stopConnection() throws JMSException {
		connection.stop();
	}
	/**
	 * Send a message to queue
	 * @param message
	 * @param topicId
	 * @throws JMSException
	 */
	public void send(String message) throws JMSException {
		TextMessage textMessage = session.createTextMessage(message);
		msgProducer.send(textMessage);
	}
	
	/**
	 * Read a message from registered topic
	 * @param timeout the time in ms after which the read 
	 * @return
	 * @throws JMSException 
	 */
	public String receive(long timeout) throws JMSException {
		TextMessage textMessage = (TextMessage) msgConsumer.receive(timeout);
		return textMessage.getText();
	}
	
	/**
	 *Read a message from registered topic
	 * This method is blocking until new message arrives
	 * @return
	 * @throws JMSException 
	 */
	public String receive() throws JMSException {
		TextMessage textMessage = (TextMessage) msgConsumer.receive();
		return textMessage.getText();
	}

	private static JMSUtils instance;
	public static JMSUtils getInstance() throws IOException {
		if (instance == null) 
			instance = new JMSUtils();
		return instance;
		
	}
}
