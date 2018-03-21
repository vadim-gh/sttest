package test.transfer.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "transfers")
public class Transfer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, insertable = false, updatable = false)
    private Long id;

    @Column(name = "account_id_from", nullable = false, updatable = false)
    private Long accountIdFrom;

    @Column(name = "account_code_from", nullable = false, length = 20, updatable = false)
    private String accountCodeFrom;

    @Column(name = "account_id_to", nullable = false, updatable = false)
    private Long accountIdTo;

    @Column(name = "account_code_to", nullable = false, length = 20, updatable = false)
    private String accountCodeTo;

    @Column(name = "transfer_date", nullable = false, updatable = false)
    private Date transferDate;

    @Column(name = "amount", nullable = false, updatable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getAccountIdFrom() {
        return accountIdFrom;
    }

    public void setAccountIdFrom(final Long accountIdFrom) {
        this.accountIdFrom = accountIdFrom;
    }

    public String getAccountCodeFrom() {
        return accountCodeFrom;
    }

    public void setAccountCodeFrom(final String accountCodeFrom) {
        this.accountCodeFrom = accountCodeFrom;
    }

    public Long getAccountIdTo() {
        return accountIdTo;
    }

    public void setAccountIdTo(final Long accountIdTo) {
        this.accountIdTo = accountIdTo;
    }

    public String getAccountCodeTo() {
        return accountCodeTo;
    }

    public void setAccountCodeTo(final String accountCodeTo) {
        this.accountCodeTo = accountCodeTo;
    }

    public Date getTransferDate() {
        return (transferDate == null) ? null : (Date) transferDate.clone();
    }

    public void setTransferDate(final Date transferDate) {
        this.transferDate = (transferDate == null) ? null : (Date) transferDate.clone();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }
}
