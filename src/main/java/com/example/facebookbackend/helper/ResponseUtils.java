package com.example.facebookbackend.helper;

import org.springframework.data.domain.PageRequest;

import java.util.List;

public class ResponseUtils {
    public static List<?> pagingList(List<?> list, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), list.size());
        return list.subList(start, end);
    }
}
