package darak.community.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@TestConfiguration
public class TestConfig {
    // 테스트용 설정들을 여기에 추가할 수 있습니다
} 