package io.spring.cloud.samples.fortuneteller.fortuneapi.fortunes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;


@Service
@EnableConfigurationProperties(FortuneProperties.class)
public class ApiService {

    @Autowired
    FortuneProperties fortuneProperties;

    @Autowired
    RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "fallbackFortune")
    public Fortune randomFortune() {
        String randomFortuneURL = fortuneProperties.getServiceURL().concat("/random");
        return restTemplate.getForObject(randomFortuneURL, Fortune.class);
    }

    private Fortune fallbackFortune() {
        return new Fortune(42L, fortuneProperties.getFallbackFortune());
    }

}
