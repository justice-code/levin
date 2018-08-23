package org.eddy.rest.factoryBean;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.eddy.rest.annotation.RestReference;
import org.eddy.rest.resolver.UrlResolver;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.util.Arrays;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;

@RequiredArgsConstructor
@Getter @Setter
public class ReferenceFactoryBean implements FactoryBean {

    @NonNull
    private Class type;
    @Autowired
    private RestTemplate restTemplate;
    private RestReference restReference;
    @Autowired
    private UrlResolver resolver;

    public ReferenceFactoryBean() {
    }

    public ReferenceFactoryBean(Class type, RestReference restReference) {
        this.type = type;
        this.restReference = restReference;
    }

    @Override
    public Object getObject() throws Exception {
        return Proxy.newProxyInstance(ReferenceFactoryBean.class.getClassLoader(), new Class[]{type}, new ReferenceInvocationHandler());
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }

    private class ReferenceInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String[] command = LOWER_CAMEL.to(LOWER_UNDERSCORE, method.getName()).split("_");
            return RequestMethod.valueOf(command[0]).call(resolver.resolve(restReference.url(), Arrays.copyOfRange(command, 1, command.length - 1), command[command.length - 1]), method.getReturnType(), restTemplate);
        }
    }

    private enum RequestMethod {

        post {
            @Override
            public <T> T call(String url, Class<T> type, RestTemplate restTemplate) {
                return null;
            }
        },
        get {
            @Override
            public <T> T call(String url, Class<T> type, RestTemplate restTemplate) {
                return restTemplate.getForObject(url, type);
            }
        };

        public <T> T call(String url, Class<T> type, RestTemplate restTemplate) {
            return null;
        }
    }
}
