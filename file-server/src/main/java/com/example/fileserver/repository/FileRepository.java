package com.example.fileserver.repository;


import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface FileRepository extends ReactiveCrudRepository<FileDB, String> {
    @Query("select * from files where user_id = :userId")
    Mono<FileDB> findByUserId(Long userId);

    @Query("select * from files where uuid = :uuid")
    Mono<FileDB> findByUuid(String uuid);

    Mono<Void> deleteByUuid(String uuid);
}
