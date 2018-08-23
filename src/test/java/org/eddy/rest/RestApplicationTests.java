package org.eddy.rest;

import org.eddy.rest.annotation.RestReference;
import org.eddy.rest.sample.RestTestInterface;
import org.eddy.rest.sample.Say;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestApplication.class)
public class RestApplicationTests {

    @Autowired
    private Say say;

    @RestReference(url = "http://localhost:8080")
    private RestTestInterface restTestInterface;

    @Test
    public void contextLoads() {

        System.out.println(say.getRestSayJson());
        System.out.println(restTestInterface.getRestSayJson());
    }

    @Test
    public void test() {
        String result = LOWER_CAMEL.to(LOWER_UNDERSCORE, "toString");
        System.out.println(result);
    }

}
