package pl.beone.operaton.operaton_history;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("pl.beone.operaton.operaton_history")
public class OperatonHistoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(OperatonHistoryApplication.class, args);
	}

}
