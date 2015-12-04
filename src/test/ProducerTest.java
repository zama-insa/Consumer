package test;

import java.rmi.RemoteException;

import web.Producer;
import web.ProducerProxy;

public class ProducerTest {
	public static void main(String[] args) throws RemoteException {
		ProducerProxy proxy = new ProducerProxy();
		Producer producer = proxy.getProducer();
		for (int i = 0; i < 100; i++)
		System.out.println(producer.pingpong(2));
	}
}
