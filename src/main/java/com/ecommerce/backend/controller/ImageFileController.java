package com.ecommerce.backend.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/images")
public class ImageFileController {

    @Value("${file.upload-dir:uploads/images}")
    private String uploadDir;

    /**
     * GET /images/{filename}
     * Retorna a imagem armazenada no disco
     */
    @GetMapping("/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        try {
            // Validar que o arquivo está dentro do diretório permitido (prevenir path traversal)
            String safePath = filename.replaceAll("\\.\\.[\\\\/]", "");
            Path imagePath = Paths.get(uploadDir, safePath);
            
            // Verificar se o arquivo existe
            if (!Files.exists(imagePath)) {
                log.warn("Imagem not found: {}", filename);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Ler o arquivo
            byte[] imageData = Files.readAllBytes(imagePath);

            // Determinar tipo de conteúdo
            String contentType = Files.probeContentType(imagePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(imageData.length))
                    .body(imageData);

        } catch (IOException e) {
            log.error("Erro ao ler imagem: {}", filename, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
