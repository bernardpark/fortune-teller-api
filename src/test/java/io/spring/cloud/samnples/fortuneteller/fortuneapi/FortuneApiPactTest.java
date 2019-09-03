/*
 * Copyright 2017-Present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package io.spring.cloud.samnples.fortuneteller.fortuneapi;


import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import au.com.dius.pact.consumer.ConsumerPactBuilder;
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactDslJsonBody;
import au.com.dius.pact.consumer.PactRule;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.model.PactFragment;
import io.spring.cloud.samples.fortuneteller.fortuneapi.fortunes.ApiService;
import io.spring.cloud.samples.fortuneteller.fortuneapi.fortunes.Fortune;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
@ActiveProfiles({"pact"})
@Ignore
public class FortuneApiPactTest {

    @Autowired
    ApiService apiService;

    @Rule
    public PactRule rule = new PactRule("localhost", 8080, this);

    @Pact(state = "FortuneState", provider = "FortuneService", consumer = "FortuneUi")
    public PactFragment createFragment(ConsumerPactBuilder.PactDslWithProvider.PactDslWithState builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json;charset=UTF-8");

        PactDslJsonBody responseBody = new PactDslJsonBody()
                .numberType("id")
                .stringType("text");

        return builder.uponReceiving("a request for a random fortune")
                .path("/random")
                .method("GET")
                .willRespondWith()
                .headers(headers)
                .status(200)
                .body(responseBody).toFragment();
    }

    @Test
    @PactVerification("FortuneState")
    public void runTest() {
        Fortune fortune = apiService.randomFortune();
        assertNotNull(fortune);
        assertThat(fortune.getId(), is(greaterThan(0L)));
        assertThat(fortune.getId(), is(not(equalTo(42L))));
        assertThat(fortune.getText(), not(isEmptyOrNullString()));
        assertThat(fortune.getText(), is(not(equalTo("Your future is unclear."))));
    }
}
