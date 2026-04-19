// đường dẫn: src/main/java/com/api/bkhouse/BKHouseApplication.java
package com.api.bkhouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BKHouseApplication {

	public static void main(String[] args) {
		SpringApplication.run(BKHouseApplication.class, args);
	}

}
