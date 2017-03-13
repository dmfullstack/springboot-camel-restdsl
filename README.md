# springboot-camel-restdsl


mvn spring-boot:run

curl localhost:8080/camel/customer

curl -X POST -H "Content-Type: applicatijson" -d '{"id" : "086129d9-b925-4bfc-b9c2-9002f2d9fe1c", "name" : "Giannis"}' http://localhost:8080/camel/customer/echo
