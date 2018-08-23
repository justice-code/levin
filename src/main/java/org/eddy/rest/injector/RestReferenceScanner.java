package org.eddy.rest.injector;

import org.eddy.rest.annotation.RestReference;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Set;

public class RestReferenceScanner extends ClassPathBeanDefinitionScanner {

    /**
     * Create a new {@code ClassPathBeanDefinitionScanner} for the given bean factory.
     *
     * @param registry the {@code BeanFactory} to load bean definitions into, in the form
     *                 of a {@code BeanDefinitionRegistry}
     */
    public RestReferenceScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    public void registerFilter() {
        addIncludeFilter(new AnnotationTypeFilter(RestReference.class));
    }

    /**
     * Perform a scan within the specified base packages,
     * returning the registered bean definitions.
     * <p>This method does <i>not</i> register an annotation config processor
     * but rather leaves this up to the caller.
     *
     * @param basePackages the packages to check for annotated classes
     * @return set of beans registered if any for tooling registration purposes (never {@code null})
     */
    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitionHolderSet = super.doScan(basePackages);
        return beanDefinitionHolderSet;
    }

    private void registerReference(BeanDefinitionHolder beanDefinitionHolder, BeanDefinitionRegistry registry) {


    }
}
