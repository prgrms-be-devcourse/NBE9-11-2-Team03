package com.example.global.webMvc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // application.yaml에 설정한 경로를 가져옵니다.
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    // 로컬 저장소의 파일을 외부에서 URL로 접근 가능하게 매핑합니다.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. /uploads/** 로 시작하는 URL 요청을 받으면
        registry.addResourceHandler("/uploads/**")
                // 2. file: 접두사와 함께 실제 폴더의 '절대 경로'를 바라보게 합니다.
                .addResourceLocations("file:" + new File(uploadDir).getAbsolutePath() + "/");
    }
}