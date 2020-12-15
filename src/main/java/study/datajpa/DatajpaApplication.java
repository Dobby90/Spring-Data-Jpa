package study.datajpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DatajpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatajpaApplication.class, args);
	}

	@Bean
	public AuditorAware<String> auditorProvider() {
		return () -> Optional.of("SYSTEM");	// 실무에선 login Id or 사번 등으로 활용
	}

}
