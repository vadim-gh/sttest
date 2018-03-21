package test.transfer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.transfer.entity.Account;
import test.transfer.entity.Transfer;
import test.transfer.repository.AccountRepository;
import test.transfer.repository.TransferRepository;

import javax.persistence.NoResultException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Service
public class TransferService implements Serializable {

    private AccountRepository accountRepository;
    private TransferRepository transferRepository;

    @Autowired
    public TransferService(final AccountRepository accountRepository, final TransferRepository transferRepository) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
    }

    private Account getAccountForUpdate(final String code) {
        try {
            return accountRepository.findByCodeForUpdate(code);
        } catch (final NoResultException e) {
            throw new IllegalArgumentException("account with code '" + code.trim() + "' not found");
        }
    }

    @Transactional
    public void transfer(final String accountCodeFrom, final String accountCodeTo, final BigDecimal amount) {
        if (accountCodeFrom == null) {
            throw new IllegalArgumentException("accountCodeFrom can't be null");
        }
        if (accountCodeFrom.trim().isEmpty()) {
            throw new IllegalArgumentException("accountCodeFrom can't be empty");
        }
        if (accountCodeTo == null) {
            throw new IllegalArgumentException("accountCodeTo can't be null");
        }
        if (accountCodeTo.trim().isEmpty()) {
            throw new IllegalArgumentException("accountCodeTo can't be empty");
        }
        if (accountCodeFrom.trim().equals(accountCodeTo.trim())) {
            throw new IllegalArgumentException("accountCodeFrom can't be equal to accountCodeTo");
        }
        if (amount == null) {
            throw new IllegalArgumentException("amount can't be null");
        }
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        final Account accountFrom;
        final Account accountTo;
        if (accountCodeFrom.compareTo(accountCodeTo) < 0) {
            accountFrom = getAccountForUpdate(accountCodeFrom);
            accountTo = getAccountForUpdate(accountCodeTo);
        } else {
            accountTo = getAccountForUpdate(accountCodeTo);
            accountFrom = getAccountForUpdate(accountCodeFrom);
        }
        final Transfer transfer = new Transfer();
        transfer.setAccountIdFrom(accountFrom.getId());
        transfer.setAccountCodeFrom(accountFrom.getCode());
        transfer.setAccountIdTo(accountTo.getId());
        transfer.setAccountCodeTo(accountTo.getCode());
        transfer.setTransferDate(new Date());
        transfer.setAmount(amount);
        transferRepository.save(transfer);
        accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
        accountTo.setBalance(accountTo.getBalance().add(amount));
    }
}
