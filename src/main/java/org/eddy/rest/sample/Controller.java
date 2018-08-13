package org.eddy.rest.sample;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest")
public class Controller {

    @RequestMapping("/say.json")
    public Hello say() {
        return new Hello("eddy", "29");
    }
}
