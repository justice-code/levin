package org.eddy.rest.injector;

import org.eddy.rest.annotation.RestReference;
import org.eddy.rest.factoryBean.ReferenceFactoryBean;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.core.annotation.AnnotationUtils.getAnnotation;

public class RestReferenceInjector extends InstantiationAwareBeanPostProcessorAdapter implements ApplicationContextAware {

    @Autowired
    private RestTemplate restTemplate;

    private final ConcurrentMap<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<String, InjectionMetadata>(256);

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
        InjectionMetadata injectionMetadata = findReferenceMetadata(beanName, bean.getClass(), pvs);
        try {
            injectionMetadata.inject(bean, beanName, pvs);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return pvs;
    }

    private InjectionMetadata findReferenceMetadata(String beanName, Class<?> clazz, PropertyValues pvs) {
        // Fall back to class name as cache key, for backwards compatibility with custom callers.
        String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
        // Quick check on the concurrent map first, with minimal locking.
        InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    if (metadata != null) {
                        metadata.clear(pvs);
                    }
                    try {
                        metadata = buildReferenceMetadata(clazz);
                        this.injectionMetadataCache.put(cacheKey, metadata);
                    } catch (NoClassDefFoundError err) {
                        throw new IllegalStateException("Failed to introspect bean class [" + clazz.getName() +
                                "] for reference metadata: could not find class that it depends on", err);
                    }
                }
            }
        }
        return metadata;
    }

    private InjectionMetadata buildReferenceMetadata(final Class<?> beanClass) {
        final List<InjectionMetadata.InjectedElement> elements = new LinkedList<InjectionMetadata.InjectedElement>();

        ReflectionUtils.doWithFields(beanClass, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {

                RestReference reference = getAnnotation(field, RestReference.class);

                if (reference != null) {

                    if (Modifier.isStatic(field.getModifiers())) {
                        return;
                    }

                    elements.add(new ReferenceInjectedElement(field, null, reference, field.getType()));
                }

            }
        });

        ReflectionUtils.doWithLocalMethods(beanClass, new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
                if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
                    return;
                }
                RestReference reference = findAnnotation(bridgedMethod, RestReference.class);
                if (reference != null && method.equals(ClassUtils.getMostSpecificMethod(method, beanClass))) {
                    if (Modifier.isStatic(method.getModifiers())) {
                        return;
                    }
                    PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, beanClass);
                    elements.add(new ReferenceInjectedElement(method, pd, reference, method.getReturnType()));
                }
            }
        });



        return new InjectionMetadata(beanClass, elements);

    }

    private class ReferenceInjectedElement extends InjectionMetadata.InjectedElement {

        private RestReference reference;
        private Class type;

        protected ReferenceInjectedElement(Member member, PropertyDescriptor pd, RestReference reference, Class type) {
            super(member, pd);
            this.reference = reference;
            this.type = type;
        }

        @Override
        protected Object getResourceToInject(Object target, String requestingBeanName) {
            try {
                ReferenceFactoryBean factoryBean = new ReferenceFactoryBean(type);
                factoryBean.setRestTemplate(restTemplate);
                factoryBean.setRestReference(reference);
                return factoryBean.getObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private Class findInterfaceClass() {
            if (super.isField) {
                return ((Field)super.member).getType();
            } else {
                return super.pd.getPropertyType();
            }
        }
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
