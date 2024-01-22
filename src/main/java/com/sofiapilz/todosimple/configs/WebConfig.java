package com.sofiapilz.todosimple.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }
    // permite q qualquer requisição q vier d fora assim, será liberada a partir dessa rota ali ("/**")
    // se n a API vai bloquear qualquer tipo de requisição q caia em qualquer controller

}
