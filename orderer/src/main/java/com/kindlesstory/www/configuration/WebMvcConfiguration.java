package com.kindlesstory.www.configuration;

import com.kindlesstory.www.module.Crypt;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import com.kindlesstory.www.controller.AdminController;
import com.kindlesstory.www.controller.RejectController;
import com.kindlesstory.www.controller.PrototypeController;
import com.kindlesstory.www.controller.OrderViewController;
import com.kindlesstory.www.controller.LoginController;
import org.springframework.context.annotation.Bean;
import com.kindlesstory.www.controller.MainViewController;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import com.kindlesstory.www.interceptor.ReferrerCheckInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import java.util.List;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ImportResource("classpath:/config/configuration.xml")
@ComponentScan(basePackages = { "com.kindlesstory.www.service", "com.kindlesstory.www.repository", "com.kindlesstory.www.aop", "com.kindlesstory.www.validator", "com.kindlesstory.www.scheduler" })
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class WebMvcConfiguration implements WebMvcConfigurer
{
    public void extendMessageConverters(final List<HttpMessageConverter<?>> converters) {
    }
    
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new ReferrerCheckInterceptor());
    }
    
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins(new String[] { "http://localhost:3000", "http://127.0.0.1:3000" }).allowedMethods(new String[] { "GET", "POST" }).allowCredentials(true).maxAge(3000L);
    }
    
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/security/rsa").setViewName("security/rsa/rsa");
        registry.addViewController("/security/aes").setViewName("security/aes/aes");
    }
    
    public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
    
    public void configureViewResolvers(final ViewResolverRegistry registry) {
        registry.jsp("/view/", ".jsp");
    }
    
    @Bean
    public MainViewController mainViewController() {
        return new MainViewController();
    }
    
    @Bean
    public LoginController loginContoller() {
        return new LoginController();
    }
    
    @Bean
    public OrderViewController orderViewController() {
        return new OrderViewController();
    }
    
    @Bean
    public PrototypeController prototypeController() {
        return new PrototypeController();
    }
    
    @Bean
    public RejectController rejectController() {
        return new RejectController();
    }
    
    @Bean
    public AdminController adminController() {
        return new AdminController();
    }
    
    @Bean
    public KeyPair keypair() {
        KeyPair keyPair = null;
        try {
            final KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024, new SecureRandom());
            keyPair = generator.genKeyPair();
        }
        catch (NoSuchAlgorithmException ex) {}
        return keyPair;
    }
    
    @Bean
    public Crypt crypt() {
        return new Crypt();
    }
}