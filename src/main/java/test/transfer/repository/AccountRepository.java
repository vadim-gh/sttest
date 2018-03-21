package test.transfer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import test.transfer.entity.Account;

import javax.persistence.LockModeType;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT a FROM Account a WHERE a.code = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Account findByCodeForUpdate(final String code);
}
