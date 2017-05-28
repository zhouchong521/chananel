package com.chananel.thirdpart.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.chananel.thirdpart")
public class FeignConfiguration {

}
