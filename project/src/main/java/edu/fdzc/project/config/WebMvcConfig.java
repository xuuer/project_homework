package edu.fdzc.project.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.fdzc.project.common.Interceptor;
import edu.fdzc.project.common.JacksonObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import org.zc.net.common.Interceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 跨域配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600)
                .allowedHeaders("*");
    }

    /**
     * 拦截器配置
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new Interceptor())
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns(
                        "/student/login",
                        "/student/register",
                        "/teacher/login",
                        "/teacher/register",
                        "/swagger-ui/**",
                        "/v3/**"
                ); // 排除路径
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new JacksonObjectMapper(); // 替换默认的 ObjectMapper
    }

}