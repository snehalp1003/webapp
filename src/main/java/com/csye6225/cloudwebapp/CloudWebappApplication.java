package com.csye6225.cloudwebapp;

import static springfox.documentation.builders.PathSelectors.regex;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class CloudWebappApplication {

    private static final Logger log = LoggerFactory.getLogger(CloudWebappApplication.class);

    public static void main(String[] args) throws Exception {
        log.info(
                "***************************** APPLICATION START INITITATED *******************************************************");
        SpringApplication.run(CloudWebappApplication.class, args);
        log.info(
                "***************************** APPLICATION STARTED *******************************************************");
    }
        
    @Bean
    public ServletRegistrationBean dispatcherRegistration() {
        return new ServletRegistrationBean(dispatcherServlet());
    }
    
    @Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
    public DispatcherServlet dispatcherServlet() {
        return new DispatcherServlet();
    }

    /**
     * @return Docket : A builder which is intended to be the primary interface
     *         into the swagger-springmvc framework. Provides sensible defaults
     *         and convenience methods for configuration.
     */
//    @Bean
//    public Docket webappAPI() {
//        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).useDefaultResponseMessages(false)
//                .directModelSubstitute(Object.class, java.lang.Void.class).select().paths(regex("/add*")).build();
//    }
    
    @Bean
    public Docket webappAPI() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.csye6225.cloudwebapp.api.rest"))
                .paths(regex("/v1.*"))
                .build();
    }

    /**
     * @return ApiInfo
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Network Structures & Cloud Computing - Webapp API").description(
                "APIs for Cloud Assignments").build();
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            log.debug("Beans provided by spring :");
            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                log.debug(beanName);
            }

        };
    }

}
