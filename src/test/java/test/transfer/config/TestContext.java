package test.transfer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import test.transfer.service.TransferService;

import static org.mockito.Mockito.mock;

@Configuration
public class TestContext {

    @Bean
    public TransferService transferService() {
        return mock(TransferService.class);
    }
}
