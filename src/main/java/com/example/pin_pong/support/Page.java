package com.example.pin_pong.support;

import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
public class Page<T> {

    private final int pageSize;
    private final int pageNumber;
    private final int totalPageCount;
    private final Long totalCount;
    private final List<T> data;

    public Page(final org.springframework.data.domain.Page<T> source) {
        final Pageable pageable = source.getPageable();
        this.pageSize = pageable.getPageSize();
        this.pageNumber = pageable.getPageNumber();
        this.totalPageCount = source.getTotalPages();
        this.totalCount = source.getTotalElements();
        this.data = source.getContent();
    }
}