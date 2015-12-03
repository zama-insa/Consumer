package web;

public class ProducerProxy implements web.Producer {
  private String _endpoint = null;
  private web.Producer producer = null;
  
  public ProducerProxy() {
    _initProducerProxy();
  }
  
  public ProducerProxy(String endpoint) {
    _endpoint = endpoint;
    _initProducerProxy();
  }
  
  private void _initProducerProxy() {
    try {
      producer = (new web.ProducerServiceLocator()).getProducer();
      if (producer != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)producer)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)producer)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (producer != null)
      ((javax.xml.rpc.Stub)producer)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public web.Producer getProducer() {
    if (producer == null)
      _initProducerProxy();
    return producer;
  }
  
  public java.lang.String pingpong(int processTime) throws java.rmi.RemoteException{
    if (producer == null)
      _initProducerProxy();
    return producer.pingpong(processTime);
  }
  
  
}