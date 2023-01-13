package my.coursework3.repository;
import my.coursework3.services.*;
import my.coursework3.characteristic.*;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;
import java.util.TreeSet;


@Data
@Repository
public class TransactionRepository {

    private final TransactionsFileService transactionsFileService;

    public TransactionRepository(TransactionsFileService transactionsFileService) {
        this.transactionsFileService = transactionsFileService;
    }

    static Long idCounter = 1L;
    TreeMap<Long, TransactionSocks> transactionList = new TreeMap<>();

    public void addTransaction(int cottonPart,
                               SocksColor socksColor,
                               SocksSize socksSize,
                               int quantity,
                               TransactionsType transactionsType
    ) {
        idCounter = 1L;
        while (transactionList.containsKey(idCounter)) {
            idCounter++;
        }
        String fullDateCreateTransaction = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy"));
        String onlyDate = fullDateCreateTransaction.substring(6, 16);
        String onlyTime = fullDateCreateTransaction.substring(0, 5);

        transactionList.put(idCounter, new TransactionSocks(cottonPart,
                socksColor,
                socksSize,
                quantity,
                onlyDate,
                onlyTime,
                transactionsType));
        transactionsFileService.saveTransactionsListToJsonFile(jsonFromList());
        transactionsFileService.saveTransactionsToTxtFile(viewAllTransactions());
    }

    public String viewAllTransactions() {
        String result = "";
        int counter = 0;
        String transactionList = "Список транзакций: ";
        String arrivalOfSocks = "Поступление на склад: ";
        String releaseOfSocks = "Выдача со склада: ";
        String delete = "Списание брака со склада: ";
        String arrivalOfSocks2 = "";
        String releaseOfSocks2 = "";
        String delete2 = "";

        TreeSet<String> sortedDates = new TreeSet<>();
        for (TransactionSocks transaction : getTransactionList().values()) {
            sortedDates.add(transaction.getDateCreateTransaction());
        }

        for (String date: sortedDates){
            result += "дата - " + date + "\n";
            for (TransactionSocks transaction : getTransactionList().values()) {
                if (transaction.getDateCreateTransaction().equals(date)){
                    if (transaction.getTransactionsType().equals(TransactionsType.ARRIVAL_OF_SOCKS)){
                        arrivalOfSocks2 += transaction.getTimeCreateTransaction() + " " +
                                "цвет " + transaction.getSocksColor() + ", " +
                                "размер " + transaction.getSocksSize() + ", " +
                                "содержание хлопка " + transaction.getCottonPart() + "%, " +
                                "количество " + transaction.getQuantity() + " пар.\n";
                    }
                    if (transaction.getTransactionsType().equals(TransactionsType.RELEASE_OF_SOCKS)){
                        releaseOfSocks2 += transaction.getTimeCreateTransaction() + " " +
                                "цвет " + transaction.getSocksColor() + ", " +
                                "размер " + transaction.getSocksSize() + ", " +
                                "содержание хлопка " + transaction.getCottonPart() + "%, " +
                                "количество " + transaction.getQuantity() + " пар.\n";
                    }
                    if (transaction.getTransactionsType().equals(TransactionsType.DELETE)){
                        delete2 += transaction.getTimeCreateTransaction() + " " +
                                "цвет " + transaction.getSocksColor() + ", " +
                                "размер " + transaction.getSocksSize() + ", " +
                                "содержание хлопка " + transaction.getCottonPart() + "%, " +
                                "количество " + transaction.getQuantity() + " пар.\n";
                    }
                }
            }
            result += arrivalOfSocks + "\n" +
                    arrivalOfSocks2 + "\n" +
                    releaseOfSocks + "\n" +
                    releaseOfSocks2 + "\n" +
                    delete + "\n" +
                    delete2 + "\n";
        }
        return result;
    }

    private String jsonFromList() {
        String json;
        try {
            json = new ObjectMapper().writeValueAsString(transactionList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }

    private TreeMap<Long, TransactionSocks> listFromFile() {
        try {
            String json = transactionsFileService.readTransactionsListFromJsonFile();
            if (StringUtils.isNotEmpty(json) || StringUtils.isNotBlank(json)) {
                transactionList = new ObjectMapper().readValue(json, new TypeReference<>() {
                });
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return transactionList;
    }

    @PostConstruct
    private void init() {
        transactionList = listFromFile();
    }
}

