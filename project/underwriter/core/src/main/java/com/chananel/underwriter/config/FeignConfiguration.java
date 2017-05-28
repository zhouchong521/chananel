package com.chananel.underwriter.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.chananel.underwriter")
public class FeignConfiguration {

}
