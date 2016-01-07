/**
 * ProducerServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package web;

public class ProducerServiceLocator extends org.apache.axis.client.Service implements web.ProducerService {

	public static String producerName;


    public String getProducerName() {
		return producerName;
	}


	public void setProducerName(String producerName) {
		this.producerName = producerName;
	}
    public ProducerServiceLocator() {
    }


    public ProducerServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ProducerServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for Producer
    private java.lang.String Producer_address = "http://localhost:8280/service/"+producerName+"?wsdl";

    public java.lang.String getProducerAddress() {
        return Producer_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ProducerWSDDServiceName = "Producer";

    public java.lang.String getProducerWSDDServiceName() {
        return ProducerWSDDServiceName;
    }

    public void setProducerWSDDServiceName(java.lang.String name) {
        ProducerWSDDServiceName = name;
    }

    public web.Producer getProducer() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(Producer_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getProducer(endpoint);
    }

    public web.Producer getProducer(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            web.ProducerSoapBindingStub _stub = new web.ProducerSoapBindingStub(portAddress, this);
            _stub.setPortName(getProducerWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setProducerEndpointAddress(java.lang.String address) {
        Producer_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (web.Producer.class.isAssignableFrom(serviceEndpointInterface)) {
                web.ProducerSoapBindingStub _stub = new web.ProducerSoapBindingStub(new java.net.URL(Producer_address), this);
                _stub.setPortName(getProducerWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("Producer".equals(inputPortName)) {
            return getProducer();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://web", "ProducerService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://web", "Producer"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("Producer".equals(portName)) {
            setProducerEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
