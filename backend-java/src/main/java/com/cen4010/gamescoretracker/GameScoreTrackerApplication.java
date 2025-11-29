package com.cen4010.gamescoretracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class GameScoreTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GameScoreTrackerApplication.class, args);
	}

}
