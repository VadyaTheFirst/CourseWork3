import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

import org.springframework.stereotype.Repository;


import java.util.HashMap;
import java.util.Set;

@Data
@Repository
public class WarehouseRepository {

    static Long idCounter = 1L;
    private HashMap<Long, Warehouse> warehouseList = new HashMap<>();

    private final SocksFileService socksFileService;

    public WarehouseRepository(SocksFileService socksFileService) {
        this.socksFileService = socksFileService;
    }

    public boolean addInWarehouseRepository(int cottonPart,
                                            SocksColor socksColor,
                                            SocksSize socksSize,
                                            int quantity) {
        if (checkInputValues(cottonPart, quantity)) {
            Warehouse bufferWarehouse = new Warehouse(cottonPart, socksColor, socksSize, quantity);
            if (warehouseList.isEmpty()) {
                warehouseList.put(idCounter, bufferWarehouse);
                idCounter++;
                socksFileService.cleanSocksListJson();
                socksFileService.saveSocksListToJsonFile(jsonFromList());
                return true;
            }
            if (checkContainsInputValues(cottonPart, socksColor, socksSize)) {
                Long currentKey = findKey(cottonPart, socksColor, socksSize);
                int newQuantity = warehouseList.get(currentKey).getQuantity() + bufferWarehouse.getQuantity();
                bufferWarehouse.setQuantity(newQuantity);
                warehouseList.remove(currentKey);
                warehouseList.put(currentKey, bufferWarehouse);
                socksFileService.cleanSocksListJson();
                socksFileService.saveSocksListToJsonFile(jsonFromList());
                return true;
            }
            if (!checkContainsInputValues(cottonPart, socksColor, socksSize)) {
                idCounter = 1L;
                while (warehouseList.containsKey(idCounter)) {
                    idCounter++;
                }
                warehouseList.put(idCounter, bufferWarehouse);
                idCounter++;
                socksFileService.cleanSocksListJson();
                socksFileService.saveSocksListToJsonFile(jsonFromList());
                return true;
            }
        }
        return false;
    }

    public int findByCottonPartLessThan(SocksColor socksColor, SocksSize socksSize, int cottonMin) {
        int quantity = 0;
        Collection<Warehouse> units = warehouseList.values();
        for (Warehouse su : units) {
            if (su.getSocksColor().equals(socksColor) &
                    su.getSocksSize().equals(socksSize) &
                    su.getCottonPart() < cottonMin) {
                quantity += su.getQuantity();
            }
        }
        return quantity;
    }

    public int findByCottonPartMoreThan(SocksColor socksColor, SocksSize socksSize, int cottonMax) {
        int quantity = 0;
        Collection<Warehouse> units = warehouseList.values();
        for (Warehouse su : units) {
            if (su.getSocksColor().equals(socksColor) &
                    su.getSocksSize().equals(socksSize) &
                    su.getCottonPart() > cottonMax) {
                quantity += su.getQuantity();
            }
        }
        return quantity;
    }

    private Long findKey(int cottonPart, SocksColor socksColor, SocksSize socksSize) {
        Long bufferKey = 1L;
        Set<Long> keys = warehouseList.keySet();
        for (Long key : keys) {
            if (checkContainsInputValues(cottonPart,
                    socksColor,
                    socksSize)) {
                bufferKey = key;
            }
        }
        return bufferKey;
    }

    public boolean delete(int cottonPart, SocksColor socksColor, SocksSize socksSize, int quantity) {
        Long bufferKey = findKey(cottonPart, socksColor, socksSize);
        if (warehouseList.containsKey(bufferKey)) {
            warehouseList.remove(bufferKey);
            socksFileService.cleanSocksListJson();
            socksFileService.saveSocksListToJsonFile(jsonFromList());
            return true;
        }
        return false;
    }

    public boolean outFromWarehouse(int cottonPart, SocksColor socksColor, SocksSize socksSize, int quantity) {

        Long bufferKey = findKey(cottonPart, socksColor, socksSize);
        if (warehouseList.containsKey(bufferKey)) {
            Warehouse bufferWarehouse = warehouseList.get(bufferKey);
            int bufferQuantity = bufferWarehouse.getQuantity() - quantity;
            if (bufferQuantity > 0) {
                bufferWarehouse.setQuantity(bufferQuantity);
                warehouseList.remove(bufferKey);
                warehouseList.put(bufferKey, bufferWarehouse);
                socksFileService.cleanSocksListJson();
                socksFileService.saveSocksListToJsonFile(jsonFromList());
                return true;
            }
            if (bufferQuantity == 0) {
                warehouseList.remove(bufferKey);
                socksFileService.cleanSocksListJson();
                socksFileService.saveSocksListToJsonFile(jsonFromList());
                return true;
            }
        }
        return false;
    }

    private HashMap<Long, Warehouse> listFromFile() {
        try {
            String json = socksFileService.readSocksListFromJsonFile();
            if (StringUtils.isNotEmpty(json) || StringUtils.isNotBlank(json)) {
                warehouseList = new ObjectMapper().readValue(json, new TypeReference<>() {
                });
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return warehouseList;
    }

    private String jsonFromList() {
        String json;
        try {
            json = new ObjectMapper().writeValueAsString(warehouseList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }

    private boolean checkInputValues(int cottonPart,
                                     int quantity) {
        return cottonPart > 0 & cottonPart <= 100 & quantity > 0;
    }

    private boolean checkContainsInputValues(int cottonPart, SocksColor socksColor, SocksSize socksSize) {
        for (Warehouse warehouseEntry : warehouseList.values()) {
            if (warehouseEntry.getSocksColor().equals(socksColor) &
                    warehouseEntry.getSocksSize().equals(socksSize) &
                    warehouseEntry.getCottonPart() == cottonPart) {
                return true;
            }
        }
        return false;
    }

    @PostConstruct
    private void init() {
        warehouseList = listFromFile();
    }
}
