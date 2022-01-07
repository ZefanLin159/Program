package com.lzf.config;

import com.lzf.pojo.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

//@Configuration这个也会被spring容器托管，注册到容器中，因为他本来就是一个Component
// @Configuration这是一个配置类，和我们之前看的bean.xml一样
@Configuration
@ComponentScan("com.lzf.pojo")
public class LinConfig {

//    注册一个bean就相当于我们之前写的一个bean标签
//    这个方法的名字，就相当于bean标签中的id属性
//    这个方法的返回值，就相当于bean标签中的class属性
    @Bean
    public User getUser(){
        return new User();//就是返回要注入到bean的对象！
    }
}
