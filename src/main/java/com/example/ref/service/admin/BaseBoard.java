package com.example.ref.service.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public abstract class BaseBoard<T, A, L, M> {

    abstract void addBoard(A request) throws IOException;

    abstract T getBoard(Long id);

    abstract Page<T> getBoardList(Pageable pageable, L request);

    abstract void modifyBoard(M request) throws IOException;

    abstract void deleteBoard(Long id) throws JsonProcessingException;

}
