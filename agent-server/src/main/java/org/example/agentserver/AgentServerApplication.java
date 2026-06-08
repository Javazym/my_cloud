package org.example.agentserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "org.example.commonapi.client", defaultConfiguration = org.example.agentserver.config.GlobalFeignConfig.class)
public class AgentServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgentServerApplication.class, args);
	}

}
