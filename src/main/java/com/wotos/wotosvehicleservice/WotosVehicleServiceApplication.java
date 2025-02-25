package com.wotos.wotosvehicleservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient(autoRegister = false)
public class WotosVehicleServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WotosVehicleServiceApplication.class, args);
	}

}
