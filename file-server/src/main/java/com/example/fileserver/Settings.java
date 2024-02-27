package com.example.fileserver;


import java.io.ByteArrayOutputStream;

import org.springframework.http.codec.multipart.FilePart;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Settings {

    public static Mono<byte[]> toBytes(FilePart filePart) {
        return filePart.content()
                .flatMap(dataBuffer -> Flux.just(dataBuffer.asByteBuffer().array()))
                .collectList()
                .map(bytesList -> {
                    // Конкатенация массивов байт
                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                    for (byte[] bytes : bytesList) {
                        byteStream.write(bytes, 0, bytes.length);
                    }
                    return byteStream.toByteArray();
                });
    }
}