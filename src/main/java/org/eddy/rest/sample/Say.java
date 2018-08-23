package org.eddy.rest.sample;

import org.eddy.rest.annotation.RestReference;

@RestReference(url = "http://localhost:8080")
public interface Say {

    Hello getRestSayJson();
}
