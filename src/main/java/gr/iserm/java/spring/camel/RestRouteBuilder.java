package gr.iserm.java.spring.camel;

import gr.iserm.java.spring.customer.Customer;
import gr.iserm.java.spring.customer.CustomerTransformationService;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class RestRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        restConfiguration()
                .contextPath("/camel")
                .component("servlet")
                .enableCORS(true)
                //requires json processing library
                .dataFormatProperty("prettyPrint", "true")
                .bindingMode(RestBindingMode.json)
                //only for swagger
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "Camel REST API")
                .apiProperty("api.version", "1.0")
                .apiProperty("cors", "true")
        ;

        rest("/say")
                .get("/hello").to("direct:hello")
                .get("/bye").consumes("application/json").to("direct:bye")
                .post("/bye").to("mock:update")
                .get("/data").produces(MediaType.APPLICATION_JSON_VALUE).to("direct:data");

        rest("/customer")
                .get("/").to("direct:customerAll")
                .post("/echo").consumes(MediaType.APPLICATION_JSON_VALUE).type(Customer.class).to("direct:customerEcho");

        from("direct:hello")
                .transform().constant("Hello World");
        from("direct:bye")
                .transform().constant("Bye World");
        from("direct:data")
                .transform().constant(Collections.singletonMap("key", "value"));
        from("direct:customerAll")
                .bean(CustomerTransformationService.class, "allCustomers");
        from("direct:customerEcho")
                .bean(CustomerTransformationService.class, "echoCustomer");


    }

}
