package org.eddy.rest.injector;

import org.eddy.rest.annotation.RestReference;
import org.eddy.rest.factoryBean.ReferenceFactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
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
        beanDefinitionHolderSet.stream().forEach(beanDefinitionHolder -> postProcessBeanDefinition(beanDefinitionHolder));
        return beanDefinitionHolderSet;
    }

    private void postProcessBeanDefinition(BeanDefinitionHolder beanDefinitionHolder) {
        GenericBeanDefinition genericBeanDefinition = (GenericBeanDefinition) beanDefinitionHolder.getBeanDefinition();
        Class type = resolveType(genericBeanDefinition.getBeanClassName());
        genericBeanDefinition.setBeanClass(ReferenceFactoryBean.class);
        genericBeanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0, type);
        genericBeanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(1, type.getAnnotation(RestReference.class));
        genericBeanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
    }

    private Class resolveType(String beanClassName) {
        try {
            return Class.forName(beanClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Determine whether the given bean definition qualifies as candidate.
     * <p>The default implementation checks whether the class is not an interface
     * and not dependent on an enclosing class.
     * <p>Can be overridden in subclasses.
     *
     * @param beanDefinition the bean definition to check
     * @return whether the bean definition qualifies as a candidate component
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }
}
