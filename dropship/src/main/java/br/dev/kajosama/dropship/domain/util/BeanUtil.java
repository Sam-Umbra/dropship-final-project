package br.dev.kajosama.dropship.domain.util;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
/**
 * Utility class to provide static access to Spring-managed beans.
 * This class allows retrieving beans from the Spring ApplicationContext
 * in contexts where dependency injection might not be directly available.
 * @author Sam_Umbra
 */
public class BeanUtil {

    /**
     * The Spring ApplicationContext, used to retrieve beans.
     */
    private static ApplicationContext context;

    /**
     * Constructor for BeanUtil.
     * Spring automatically injects the ApplicationContext when this component is initialized.
     *
     * @param applicationContext The Spring ApplicationContext.
     */
    public BeanUtil(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    /**
     * Retrieves a Spring-managed bean of the specified type.
     *
     * @param <T> The type of the bean to retrieve.
     * @param beanClass The Class object representing the type of the bean.
     * @return An instance of the requested bean.
     */
    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }
}
