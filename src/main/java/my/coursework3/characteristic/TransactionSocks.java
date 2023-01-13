package my.coursework3.characteristic;

import lombok.Data;

@Data
public class TransactionSocks {

    private int cottonPart;
    private SocksColor socksColor;
    private SocksSize socksSize;
    private int quantity;
    private String timeCreateTransaction;
    private String dateCreateTransaction;
    private TransactionsType transactionsType;


    public TransactionSocks(int cottonPart,
                            SocksColor socksColor,
                            SocksSize socksSize,
                            int quantity,
                            String dateCreateTransaction,
                            String timeCreateTransaction,
                            TransactionsType transactionsType) {
        this.cottonPart = cottonPart;
        this.socksColor = socksColor;
        this.socksSize = socksSize;
        if (quantity > 0) {
            this.quantity = quantity;
        } else {
            throw new IllegalArgumentException("Введено неправильное количество = " + quantity);
        }
        this.timeCreateTransaction = timeCreateTransaction;
        this.dateCreateTransaction = dateCreateTransaction;
        this.transactionsType = transactionsType;
    }
}
