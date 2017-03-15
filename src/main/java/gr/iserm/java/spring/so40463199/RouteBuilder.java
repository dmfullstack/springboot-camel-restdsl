package gr.iserm.java.spring.so40463199;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;

@Component
public class RouteBuilder extends org.apache.camel.builder.RouteBuilder {
    public void configure() throws Exception {

        restConfiguration()
                .contextPath("/camel")
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .dataFormatProperty("json.in.disableFeatures", "FAIL_ON_EMPTY_BEANS")
                .apiProperty("cors", "true");

        rest("/car").description("Cars rest service")
                .consumes("application/json").produces("application/json")
                .get("/{id}").description("Find car by id")
                .param().name("id").type(RestParamType.path)
                .description("The id of the car to get")
                .dataType("int").endParam()
                .route()
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setBody(new CarDTO(Long.parseLong(exchange.getIn().getHeader("id", String.class))));
                    }
                })
                .marshal().json(JsonLibrary.Jackson, CarDTO.class)
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .to("http://localhost:8081?bridgeEndpoint=true");
    }
}
