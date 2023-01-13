package my.coursework3.controllers;
import my.coursework3.services.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;

@RestController()
@RequestMapping("/files")
@Tag(name = "Импорт и экспорт файлов",
        description = "Операции для отправки и получения (сохранения) файлов")
public class FileTransferController {

    private final SocksFileService socksFileService;
    private final TransactionsFileService transactionsFileService;

    public FileTransferController(SocksFileService socksFileService, TransactionsFileService transactionsFileService) {
        this.socksFileService = socksFileService;
        this.transactionsFileService = transactionsFileService;
    }

    @GetMapping("/socksListExport")
    @Operation(summary = "Сохранение файла со списком имеющихся на складе носков на компьютер пользователя в формате json")
    public ResponseEntity<InputStreamResource> downloadSocksListAsJson() throws FileNotFoundException {
        File file = socksFileService.getSocksListJson();
        if (file.exists()) {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok().
                    contentLength(file.length()).
                    contentType(MediaType.APPLICATION_JSON).
                    header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"socksList.json\"").
                    body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/transactionsListExport")
    @Operation(summary = "Сохранение файла со списком транзакций на компьютер пользователя в формате json")
    public ResponseEntity<InputStreamResource> downloadTransactionsListAsJson() throws FileNotFoundException {
        File file = transactionsFileService.getTransactionsListJson();
        if (file.exists()) {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok().
                    contentLength(file.length()).
                    contentType(MediaType.APPLICATION_JSON).
                    header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"transactionsList.json\"").
                    body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    //

    @PostMapping(value = "/socksListImport", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузка сохраненного файла со списком имеющихся на складе носков с компьютера пользователя")
    public ResponseEntity<Void> uploadSocksListFile(@RequestParam MultipartFile inputFile) {
        File file = socksFileService.getSocksListJson();
        if (!(file.length() == 0)) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                IOUtils.copy(inputFile.getInputStream(), fos);
                return ResponseEntity.ok().build();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.internalServerError().build();
    }

    @PostMapping(value = "/transactionsListImport", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузка сохраненного файла со списком транзакций с компьютера пользователя")
    public ResponseEntity<Void> uploadTransactionsListFile(@RequestParam MultipartFile inputFile) {
        File file = transactionsFileService.getTransactionsListJson();
        if (!(file.length() == 0)) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                IOUtils.copy(inputFile.getInputStream(), fos);
                return ResponseEntity.ok().build();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/saveTransactionsAsTxt")
    @Operation(summary = "Сохранение файла с транзакциями товара(носков) на компьютер пользователя в формате txt")
    public ResponseEntity<InputStreamResource> downloadRecipesAsTxt() throws FileNotFoundException {
        File file = transactionsFileService.getTxtFile();
        if (file.exists()) {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok().
                    contentLength(file.length()).
                    contentType(MediaType.TEXT_PLAIN).
                    header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"transactions.txt\"" + LocalDateTime.now()).
                    body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}