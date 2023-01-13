package my.coursework3.services;

import org.springframework.stereotype.Service;
import my.coursework3.repository.*;
import my.coursework3.characteristic.*;

@Service
public class SocksService {

    private final TransactionRepository transactionRepository;

    private final WarehouseRepository warehouseRepository;

    public SocksService(TransactionRepository transactionRepository, WarehouseRepository warehouseRepository) {
        this.transactionRepository = transactionRepository;
        this.warehouseRepository = warehouseRepository;
    }

    public int findByCottonPartLessThan(SocksColor socksColor,
                                        SocksSize socksSize,
                                        int cottonMin) {
        return warehouseRepository.findByCottonPartLessThan(socksColor, socksSize, cottonMin);
    }

    public int findByCottonPartMoreThan(SocksColor socksColor,
                                        SocksSize socksSize,
                                        int cottonMax) {
        return warehouseRepository.findByCottonPartMoreThan(socksColor, socksSize, cottonMax);
    }

    public boolean addToWarehouse(SocksColor socksColor,
                                  SocksSize socksSize,
                                  int cottonPart,
                                  int quantity) {
        transactionRepository.addTransaction(cottonPart,
                socksColor,
                socksSize,
                quantity,
                TransactionsType.ARRIVAL_OF_SOCKS);
        return warehouseRepository.addInWarehouseRepository(cottonPart,
                socksColor,
                socksSize,
                quantity);
    }

    public boolean delete(SocksColor socksColor,
                          SocksSize socksSize,
                          int cottonPart,
                          int quantity) {
        transactionRepository.addTransaction(cottonPart, socksColor, socksSize, quantity, TransactionsType.DELETE);
        return warehouseRepository.delete(cottonPart,
                socksColor,
                socksSize,
                quantity);
    }

    public boolean releaseFromWarehouse(SocksColor socksColor,
                                        SocksSize socksSize,
                                        int cottonPart,
                                        int quantity) {
        transactionRepository.addTransaction(cottonPart, socksColor, socksSize, quantity, TransactionsType.RELEASE_OF_SOCKS);
        return warehouseRepository.outFromWarehouse(cottonPart,
                socksColor,
                socksSize,
                quantity);
    }
}
