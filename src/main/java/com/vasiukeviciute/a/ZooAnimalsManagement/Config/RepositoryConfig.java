package com.vasiukeviciute.a.ZooAnimalsManagement.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.vasiukeviciute.a.ZooAnimalsManagement")
public class RepositoryConfig {
}
