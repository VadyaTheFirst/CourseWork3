package my.coursework3.controllers;

import my.coursework3.services.*;
import my.coursework3.characteristic.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/socks")
@Tag(name = "Носки", description = "CRUD-операции для работы с товаром 'Носки'")
public class SocksController {
    private final SocksService socksService;

    public SocksController(SocksService socksService) {
        this.socksService = socksService;
    }

    @PostMapping("/arrivalOfSocks/{socksColor}/{socksSize}/{cottonPart}/{quantity}")
    @Operation(summary = "Приход носков на склад")
    public ResponseEntity<String> incoming(@PathVariable("socksColor") SocksColor socksColor,
                                           @PathVariable("socksSize") SocksSize socksSize,
                                           @PathVariable("cottonPart") int cottonPart,
                                           @PathVariable("quantity") int quantity) {
        {
            if (socksService.addToWarehouse(socksColor, socksSize, cottonPart, quantity)) {
                return ResponseEntity.status(HttpStatus.OK).body("Носки добавлены на склад");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Носки НЕ добавлены. (Ошибка 400)");
            }
        }
    }

    @PutMapping("/releaseOfSocks/{socksColor}/{socksSize}/{cottonPart}/{quantity}")
    @Operation(summary = "Отпуск носков со склада")
    public ResponseEntity<String> outgoing(@PathVariable("socksColor") SocksColor socksColor,
                                           @PathVariable("socksSize") SocksSize socksSize,
                                           @PathVariable("cottonPart") int cottonPart,
                                           @PathVariable("quantity") int quantity) {
        if (socksService.releaseFromWarehouse(socksColor, socksSize, cottonPart, quantity)) {
            return ResponseEntity.ok().body("Носки со склада выданы");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Носки со склада НЕ выданы. Нет нужного количества. (Ошибка 400)");
    }

    @GetMapping("/requestQuantityCottonMin/{socksColor}/{socksSize}/{cottonMin}")
    @Operation(summary = "Запросить информацию о количестве носков на складе по содержанию хлопка меньше указанного")
    public ResponseEntity<String> findCottonPartMin(@PathVariable("socksColor") SocksColor socksColor,
                                                    @PathVariable("socksSize") SocksSize socksSize,
                                                    @PathVariable("cottonMin") int cottonMin) {
        int quantity = socksService.findByCottonPartLessThan(socksColor, socksSize, cottonMin);
        if (quantity > 0) {
            return ResponseEntity.ok().body("По запросу найдено " + quantity + " шт. носков");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ничего не найдено");
        }
    }

    @GetMapping("/requestQuantityCottonMax/{socksColor}/{socksSize}/{cottonMax}")
    @Operation(summary = "Запросить информацию о количестве носков на складе по содержанию хлопка больше указанного")
    public ResponseEntity<String> findCottonPartMax(@PathVariable("socksColor") SocksColor socksColor,
                                                    @PathVariable("socksSize") SocksSize socksSize,
                                                    @PathVariable("cottonMax") int cottonMax) {
        int quantity = socksService.findByCottonPartMoreThan(socksColor, socksSize, cottonMax);
        if (quantity > 0) {
            return ResponseEntity.ok().body("По запросу найдено " + quantity + " шт. носков");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ничего не найдено");
        }
    }

    @DeleteMapping("/delete/{socksColor}/{socksSize}/{cottonPart}/{quantity}")
    @Operation(summary = "Списание испорченных (бракованных) носков")
    public ResponseEntity<String> cancellation(@PathVariable("socksColor") SocksColor socksColor,
                                               @PathVariable("socksSize") SocksSize socksSize,
                                               @PathVariable("cottonPart") int cottonPart,
                                               @PathVariable("quantity") int quantity) {
        if (socksService.delete(socksColor, socksSize, cottonPart, quantity)) {
            return ResponseEntity.ok().body("Брак списан со склада.");
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Носки не найдены. (Ошибка 404)");
        }
    }

}