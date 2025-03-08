package com.tuneup.tuneup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TuneupApplication {

	public static void main(String[] args) {
		SpringApplication.run(TuneupApplication.class, args);
	}

}
