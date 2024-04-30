package org.test.gitb;

import com.gitb.tr.ObjectFactory;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;

/**
 * Configuration class responsible for creating the Spring beans required by the service.
 */
@Configuration
public class ServiceConfig {

    /**
     * The CXF endpoint that will serve messaging service calls.
     *
     * @return The endpoint.
     */
    @Bean
    public EndpointImpl messagingService(Bus cxfBus, MessagingServiceImpl messagingServiceImplementation) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, messagingServiceImplementation);
        endpoint.setServiceName(new QName("http://www.gitb.com/ms/v1/", "MessagingServiceService"));
        endpoint.setEndpointName(new QName("http://www.gitb.com/ms/v1/", "MessagingServicePort"));
        endpoint.publish("/messaging");
        return endpoint;
    }

    /**
     * The ObjectFactory used to construct GITB classes.
     *
     * @return The factory.
     */
    @Bean
    public ObjectFactory objectFactory() {
        return new ObjectFactory();
    }

}
