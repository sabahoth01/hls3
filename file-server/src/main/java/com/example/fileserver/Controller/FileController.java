package com.example.fileserver.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.ifmo.fileservice.dto.FileDTO;
import ru.ifmo.fileservice.model.FileDB;
import ru.ifmo.fileservice.service.FileService;

@RestController
@RequestMapping("files")
@RequiredArgsConstructor
public class FileController {

    @Autowired
    private final FileService fileService;

    @Hidden
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<FileDTO>> uploadFile(@RequestPart("file") Mono<FilePart> file, @RequestParam("userID") Long userID) {
        return fileService.store(file, userID)
                .map(fileDTO -> ResponseEntity.ok().body(fileDTO));
    }


    @GetMapping("/download/{id}")
    public ResponseEntity<Flux<DataBuffer>> downloadFile(@PathVariable String id) {
        Mono<FileDB> fileDB = fileService.getFile(id);
        Flux<DataBuffer> file = fileDB
                .flatMapMany(data -> fileService.getFileAsResource(data));

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.block().getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM).body(file);
    }
}
