package pl.piomin.services.organization.controller;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.piomin.services.organization.client.DepartmentClient;
import pl.piomin.services.organization.client.EmployeeClient;
import pl.piomin.services.organization.model.Department;
import pl.piomin.services.organization.model.Employee;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@ExtendWith(PactConsumerTestExt.class)
@PactBroker(url = "http://localhost:9080")
public class DepartmentClientContractTests {

    @Pact(provider = "department-service", consumer = "organization-service")
    public RequestResponsePact callFindDepartment(PactDslWithProvider builder) {
        DslPart body = PactDslJsonArray.arrayEachLike()
                .integerType("id")
                .stringType("name")
                .closeObject();
        return builder.given("findByOrganization")
                .uponReceiving("findByOrganization")
                    .path("/departments/organization/1")
                    .method("GET")
                .willRespondWith()
                    .status(200)
                    .body(body).toPact();
    }

    @Test
    @PactTestFor(providerName = "department-service", pactVersion = PactSpecVersion.V3)
    public void verifyFindDepartmentPact(MockServer mockServer) {
        System.out.println(mockServer.getUrl());
        DepartmentClient client = RestClientBuilder.newBuilder()
                .baseUri(URI.create(mockServer.getUrl()))
                .build(DepartmentClient.class);
        List<Department> departments = client.findByOrganization(1L);
        System.out.println(departments);
        assertNotNull(departments);
        assertTrue(departments.size() > 0);
        assertNotNull(departments.get(0).getId());
    }
}
