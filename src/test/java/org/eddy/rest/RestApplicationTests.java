package org.eddy.rest;

import org.eddy.rest.annotation.RestReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestApplication.class)
public class RestApplicationTests {

    @Autowired
    private RestTemplate restTemplate;

    @RestReference(url = "https://ynuf.alipay.com/service/um.json")
    private Say say;

    @Test
    public void contextLoads() {

        System.out.println(say.get());
    }

    @Test
    public void test() {
        String result = LOWER_CAMEL.to(LOWER_UNDERSCORE, "toString");
        System.out.println(result);
    }

}
