package org.eddy.rest.injector;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.List;

public class RestImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, BeanFactoryAware {

    private BeanFactory beanFactory;
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 处理field
        registry.registerBeanDefinition("dubboReferenceInjector", BeanDefinitionBuilder.rootBeanDefinition(RestReferenceInjector.class).setRole(BeanDefinition.ROLE_INFRASTRUCTURE).getBeanDefinition());

        // 处理接口
        List<String> pkgs = AutoConfigurationPackages.get(beanFactory);
        RestReferenceScanner scanner = new RestReferenceScanner(registry);
        scanner.registerFilter();
        scanner.doScan(StringUtils.toStringArray(pkgs));
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
