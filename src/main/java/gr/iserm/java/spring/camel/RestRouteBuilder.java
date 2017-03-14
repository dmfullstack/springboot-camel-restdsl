package gr.iserm.java.spring.camel;

import com.fasterxml.jackson.databind.DeserializationFeature;
import gr.iserm.java.spring.customer.Customer;
import gr.iserm.java.spring.customer.CustomerTransformationService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class RestRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        JacksonDataFormat df = new JacksonDataFormat(Customer.class);
        df.disableFeature(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        df.disableFeature(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);

        Processor REMOVE_HEADERS_PROCESSOR = new Processor() {
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeaders(Collections.<String, Object>emptyMap());
            }
        };

        restConfiguration()
                .contextPath("/camel")
                .component("servlet")
                .enableCORS(true)
                //requires json processing library
                .dataFormatProperty("prettyPrint", "true")
                .dataFormatProperty("json.in.disableFeatures", "FAIL_ON_EMPTY_BEANS")
                .dataFormatProperty("json.out.disableFeatures", "FAIL_ON_EMPTY_BEANS")
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
                .post("/echo").consumes(MediaType.APPLICATION_JSON_VALUE).type(Customer.class).to("direct:customerEcho")
                .get("/echo/new")
                    .route()
                    .process(REMOVE_HEADERS_PROCESSOR)
                    .to("direct:customerNew")
                    .marshal().json(JsonLibrary.Jackson, Customer.class)
                    .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                    .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .to("http://localhost:5050/camel/customer/echo?bridgeEndpoint=true")
                    .unmarshal().json(JsonLibrary.Jackson, Customer.class)
        ;

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
        from("direct:customerNew")
                .bean(CustomerTransformationService.class, "newCustomer");

    }



}
