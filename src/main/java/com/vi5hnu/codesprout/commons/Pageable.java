package com.vi5hnu.codesprout.commons;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@Data
@Builder
public class Pageable<T> {
    private List<T> data;
    private int pageNo;
    private long totalItems;

    public Pageable(List<T> data, int pageNo, long totalItems){
        if(pageNo<=0) throw new RuntimeException("page number must be >=1");
        this.data=data;
        this.pageNo=pageNo;
        this.totalItems=totalItems;
    }

    static public  <T> Pageable<T> emptyPage() {
        return Pageable.<T>builder()
                .data(Collections.emptyList())
                .pageNo(1)
                .totalItems(0)
                .build();
    }
}