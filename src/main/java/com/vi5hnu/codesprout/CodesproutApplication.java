package com.vi5hnu.codesprout;

import com.vi5hnu.codesprout.configuration.AwsS3Properties;
import com.vi5hnu.codesprout.configuration.OAuthClientProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableConfigurationProperties({AwsS3Properties.class, OAuthClientProperties.class})
@EnableAsync
public class CodesproutApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodesproutApplication.class, args);
	}

}
