package com.cashcardspring.cashCardSpring;

import com.cashcardspring.cashCardSpring.mappers.CashCard;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

@Component
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CashCardControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void shouldReturnACashCardWhenDataIsSaved() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/cashcard/99", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        //Test that if the response contains the correct values.
        //In this specific cas the ID
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(12);

        //Lets test the response contains the correct amount value
        Double amount = documentContext.read("$.amount");
        assertThat(amount).isEqualTo(23.2);
    }

    @Test
    public void shouldNotReturnACashCardWithAnUnknownId(){
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcard/91", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();

    }

    @Test
    public void shouldCreateANewCashCard(){
        CashCard newCashCard = new CashCard(null, 250.00);
        ResponseEntity<Void> createResponse =
                restTemplate.postForEntity("/cashcard/create", newCashCard, void.class);

        //This line provide a response with the 201
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //The following code contain location header field that provide the Id of the
        //new object created
        URI locationOfNewCashCard = createResponse.getHeaders().getLocation();
        ResponseEntity<String> getResponse =
                restTemplate.getForEntity(locationOfNewCashCard, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    }
}
