package com.kute.hystrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan(basePackages = {"com.kute.hystrix.filter"})
@SpringBootApplication
public class PurehystrixtestApplication {

	public static void main(String[] args) {
		SpringApplication.run(PurehystrixtestApplication.class, args);
	}

}
