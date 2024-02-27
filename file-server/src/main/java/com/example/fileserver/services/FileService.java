package com.example.fileserver.services;


import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class FileService {

    @Autowired
    private final FileRepository fileRepository;

    @Transactional
    public Mono<FileDTO> store(Mono<FilePart> file, Long userId) {
        return file.flatMap(filePart -> {
            String fileName = StringUtils.cleanPath(filePart.filename());
            return FilePartUtils.toBytes(filePart)
                    .flatMap(bytes -> {
                        FileDB fileDB = FileDB.builder()
                                .uuid(UUID.randomUUID().toString())
                                .data(bytes)
                                .filename(fileName)
                                .userId(userId)
                                .build();

                        return fileRepository.findByUserId(userId)
                                .flatMap(existingFile -> fileRepository.deleteByUuid(existingFile.getUuid())
                                        .then(fileRepository.save(fileDB)))
                                .switchIfEmpty(fileRepository.save(fileDB))
                                .map(savedFile -> new FileDTO(savedFile.getUuid()));
                    });
        });
    }


    public Mono<FileDB> getFile(String id) {
        return fileRepository.findByUuid(id)
                .switchIfEmpty(Mono.error(new StorageException("File not found")));
    }

    public Flux<DataBuffer> getFileAsResource(FileDB file) {
        return Flux.defer(() -> Flux.just(new DefaultDataBufferFactory().wrap(file.getData())));
    }
}
