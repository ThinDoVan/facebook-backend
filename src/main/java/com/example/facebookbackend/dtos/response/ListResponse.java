package com.example.facebookbackend.dtos.response;

import lombok.Data;
import org.springframework.data.domain.PageRequest;

import java.util.List;
@Data
public class ListResponse {
    private List<?> listResult;
    private String message;
    public static List<?> pagingList(List<?> list, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), list.size());
        return list.subList(start, end);
    }
}
