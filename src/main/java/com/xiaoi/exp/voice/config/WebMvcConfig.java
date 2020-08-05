package com.xiaoi.exp.voice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.io.File;

/**
 * @Author: alan.peng
 * @Date: 2020/3/31 18:45
 * @Description:
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

//    @Override
//    protected void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/").setViewName("forward:/index");
//        registry.addViewController("/index.html").setViewName("redirect:/index");
//        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
//        super.addViewControllers(registry);
//    }

    /**
     * 静态资源映射器，需要注意的是：这种方式为在全局的基础上配置，如果要自定义
     * 类去继承该类，别忘了要打上@configuration注解
     *
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }


}
