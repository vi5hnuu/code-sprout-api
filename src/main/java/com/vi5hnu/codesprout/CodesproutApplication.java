package com.vi5hnu.codesprout;

import com.vi5hnu.codesprout.config.AwsS3Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AwsS3Properties.class)
public class CodesproutApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodesproutApplication.class, args);
	}

}
