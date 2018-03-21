package test.transfer.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import test.transfer.config.TestContext;
import test.transfer.config.WebAppContext;
import test.transfer.entity.Account;
import test.transfer.entity.Transfer;
import test.transfer.repository.AccountRepository;
import test.transfer.repository.TransferRepository;
import test.transfer.service.TransferService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static test.transfer.rest.TransferRestController.AMOUNT_FORMAT;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestContext.class, WebAppContext.class})
@WebAppConfiguration
public class TransferRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccountRepository accountRepositoryMock;

    @Mock
    private TransferRepository transferRepositoryMock;

    @Autowired
    @InjectMocks
    private TransferService transferServiceMock;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void init() {
        reset(transferServiceMock);
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void transfer() throws Exception {
        final BigDecimal transferAmount = new BigDecimal(500);
        final String transferAmountStr = "500";
        final String badTransferAmountStr = "bad";
        final Account account1 = new Account();
        account1.setId(1L);
        account1.setCode("00000000000000000001");
        account1.setBalance(new BigDecimal(1000));
        final Account account2 = new Account();
        account2.setId(2L);
        account2.setCode("00000000000000000002");
        account2.setBalance(new BigDecimal(2000));
        final List<Transfer> transfers = new ArrayList<>();

        when(accountRepositoryMock.findByCodeForUpdate(account1.getCode())).thenReturn(account1);
        when(accountRepositoryMock.findByCodeForUpdate(account2.getCode())).thenReturn(account2);
        when(transferRepositoryMock.save(any(Transfer.class))).thenAnswer((Answer<Transfer>) invocationOnMock -> {
            final Transfer transfer = invocationOnMock.getArgument(0);
            transfers.add(transfer);
            return transfer;
        });
        doCallRealMethod().when(transferServiceMock).transfer(any(String.class), any(String.class),
                any(BigDecimal.class));
        doCallRealMethod().when(transferServiceMock).transfer(isNull(), isNull(), isNull());
        doCallRealMethod().when(transferServiceMock).transfer(any(String.class), isNull(), isNull());
        doCallRealMethod().when(transferServiceMock).transfer(any(String.class), any(String.class), isNull());

        mockMvc.perform(get("/api/transfer"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("accountCodeFrom can't be null"));

        assertTrue(account1.getBalance().equals(new BigDecimal(1000)));
        assertTrue(account2.getBalance().equals(new BigDecimal(2000)));
        assertTrue(transfers.isEmpty());

        mockMvc.perform(get("/api/transfer")
                .param("accountCodeFrom", ""))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("accountCodeFrom can't be empty"));

        assertTrue(account1.getBalance().equals(new BigDecimal(1000)));
        assertTrue(account2.getBalance().equals(new BigDecimal(2000)));
        assertTrue(transfers.isEmpty());

        mockMvc.perform(get("/api/transfer")
                .param("accountCodeFrom", account1.getCode()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("accountCodeTo can't be null"));

        assertTrue(account1.getBalance().equals(new BigDecimal(1000)));
        assertTrue(account2.getBalance().equals(new BigDecimal(2000)));
        assertTrue(transfers.isEmpty());

        mockMvc.perform(get("/api/transfer")
                .param("accountCodeFrom", account1.getCode())
                .param("accountCodeTo", ""))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("accountCodeTo can't be empty"));

        assertTrue(account1.getBalance().equals(new BigDecimal(1000)));
        assertTrue(account2.getBalance().equals(new BigDecimal(2000)));
        assertTrue(transfers.isEmpty());

        mockMvc.perform(get("/api/transfer")
                .param("accountCodeFrom", account1.getCode())
                .param("accountCodeTo", account2.getCode()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("amount can't be null"));

        assertTrue(account1.getBalance().equals(new BigDecimal(1000)));
        assertTrue(account2.getBalance().equals(new BigDecimal(2000)));
        assertTrue(transfers.isEmpty());

        mockMvc.perform(get("/api/transfer")
                .param("accountCodeFrom", account1.getCode())
                .param("accountCodeTo", account1.getCode()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("accountCodeFrom can't be equal to accountCodeTo"));

        assertTrue(account1.getBalance().equals(new BigDecimal(1000)));
        assertTrue(account2.getBalance().equals(new BigDecimal(2000)));
        assertTrue(transfers.isEmpty());

        mockMvc.perform(get("/api/transfer")
                .param("accountCodeFrom", account1.getCode())
                .param("accountCodeTo", account2.getCode())
                .param("amount", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("amount must be positive"));

        assertTrue(account1.getBalance().equals(new BigDecimal(1000)));
        assertTrue(account2.getBalance().equals(new BigDecimal(2000)));
        assertTrue(transfers.isEmpty());

        mockMvc.perform(get("/api/transfer")
                .param("accountCodeFrom", account1.getCode())
                .param("accountCodeTo", account2.getCode())
                .param("amount", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("amount must be positive"));

        assertTrue(account1.getBalance().equals(new BigDecimal(1000)));
        assertTrue(account2.getBalance().equals(new BigDecimal(2000)));
        assertTrue(transfers.isEmpty());

        mockMvc.perform(get("/api/transfer")
                .param("accountCodeFrom", account1.getCode())
                .param("accountCodeTo", account2.getCode())
                .param("amount", badTransferAmountStr))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("amount must match format '" + AMOUNT_FORMAT + "'"));

        assertTrue(account1.getBalance().equals(new BigDecimal(1000)));
        assertTrue(account2.getBalance().equals(new BigDecimal(2000)));
        assertTrue(transfers.isEmpty());

        mockMvc.perform(get("/api/transfer")
                .param("accountCodeFrom", account1.getCode())
                .param("accountCodeTo", account2.getCode())
                .param("amount", transferAmountStr))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));

        assertTrue(account1.getBalance().equals(new BigDecimal(500)));
        assertTrue(account2.getBalance().equals(new BigDecimal(2500)));
        assertTrue(transfers.size() == 1);

        final Transfer transfer = transfers.get(0);
        assertTrue(transfer.getAccountCodeFrom().equals(account1.getCode()));
        assertTrue(transfer.getAccountIdFrom().equals(account1.getId()));
        assertTrue(transfer.getAccountCodeTo().equals(account2.getCode()));
        assertTrue(transfer.getAccountIdTo().equals(account2.getId()));
        assertTrue(transfer.getAmount().equals(transferAmount));
    }
}