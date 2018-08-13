package org.eddy.rest.resolver;

public interface UrlResolver {

    default String resolve(String root, String[] paths, String suffix) {
        return root + "/" + String.join("/", paths) + "." + suffix;
    }
}
