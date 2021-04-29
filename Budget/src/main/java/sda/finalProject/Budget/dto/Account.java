package sda.finalProject.Budget.dto;

import lombok.Data;
import sda.finalProject.Budget.entity.AccountEntity;
import sda.finalProject.Budget.entity.TransactionEntity;
import sda.finalProject.Budget.repository.AccountRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
@Data
public class Account {

    private final AccountRepository accountRepository;

    public Account(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    private Long id;
    private Long userId;
    private BigDecimal balance;
    private Set<TransactionEntity> transactionEntityList;

    public Account(AccountRepository accountRepository, Long id, Long userId, BigDecimal balance, Set<TransactionEntity> transactionEntityList) {
        this.accountRepository = accountRepository;
        this.id = id;
        this.userId = userId;
        this.balance = balance;
        this.transactionEntityList = transactionEntityList;
    }
    public void addTransaction (NewTransactionDTO newTransactionDTO){
        if(newTransactionDTO.getCategory() != null && newTransactionDTO.getDescription() != null && newTransactionDTO.getId() != null && newTransactionDTO.getTransactionDate() != null)
        {
        TransactionEntity transactionEntity = new TransactionEntity(newTransactionDTO.getDescription(),
                newTransactionDTO.getValue(),
                newTransactionDTO.getTransactionDate(),
                newTransactionDTO.getCategory());
        transactionEntityList.add(transactionEntity);
    }
        throw new RuntimeException("xxx");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(accountRepository, account.accountRepository) && Objects.equals(id, account.id) && Objects.equals(userId, account.userId) && Objects.equals(balance, account.balance) && Objects.equals(transactionEntityList, account.transactionEntityList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountRepository, id, userId, balance, transactionEntityList);
    }

    public Set<TransactionEntity> getReportByMonth(Long userId, LocalDateTime localDate){
        if(userId != null && localDate != null) {
            int monthValue = localDate.getMonthValue();
            Optional<AccountEntity> accountByUserId = accountRepository.findByUserId(userId);
            Set<TransactionEntity> transactionEntityList = accountByUserId.get().getTransactionEntityList();
            return transactionEntityList.stream()
                    .filter(t -> t.getTransactionDate().getMonthValue() == monthValue)
                    .collect(Collectors.toSet());
        }
            return null;
    }

    public Set<TransactionEntity> getReportSinceAccountCreated(Long userId){
        Optional<AccountEntity> byUserId = accountRepository.findByUserId(userId);
        return byUserId.get().getTransactionEntityList();
    }
    public TransactionEntity getTransactionById(Long id, Set<TransactionEntity> transactionEntities){
        if(id != null && transactionEntities != null) {
            return transactionEntities.stream().filter(t -> t.getId() == id).findAny().get();
        }
        return null;
    }
    public Set<TransactionEntity> getReportOfIncome(Long userId){
        Optional<AccountEntity> byUserId = accountRepository.findByUserId(userId);
        Set<TransactionEntity> transactionEntityList = byUserId.get().getTransactionEntityList();
        return transactionEntityList.stream().filter(t -> t.getValue().compareTo(BigDecimal.valueOf(0)) == 1 )
                .collect(Collectors.toSet());
    }
    public Set<TransactionEntity> getReportOfMoneySpend(Long userId){
        Optional<AccountEntity> byUserId = accountRepository.findByUserId(userId);
        Set<TransactionEntity> transactionEntityList = byUserId.get().getTransactionEntityList();
        return transactionEntityList.stream().filter(t -> t.getValue().compareTo(BigDecimal.valueOf(0)) == -1 )
                .collect(Collectors.toSet());
    }
    public BigDecimal getBalance(Long userId){
        Set<TransactionEntity> transactionEntityList = accountRepository.findByUserId(userId).get().getTransactionEntityList();
        BigDecimal balance = BigDecimal.valueOf(0);
        transactionEntityList.forEach(t -> balance.add(t.getValue()));
        return balance;
    }
}
