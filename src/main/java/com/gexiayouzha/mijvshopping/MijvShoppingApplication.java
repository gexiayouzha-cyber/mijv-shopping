package com.gexiayouzha.mijvshopping;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.gexiayouzha.mijvshopping.mapper")
public class MijvShoppingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MijvShoppingApplication.class, args);
	}

}
