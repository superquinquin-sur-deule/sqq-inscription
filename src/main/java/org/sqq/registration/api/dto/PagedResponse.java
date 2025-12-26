package org.sqq.registration.api.dto;

import java.util.List;

public class PagedResponse<T> {
    public List<T> content;
    public long totalElements;
    public int totalPages;
    public int page;
    public int size;
    public boolean first;
    public boolean last;

    public PagedResponse() {
    }

    public PagedResponse(List<T> content, long totalElements, int page, int size) {
        this.content = content;
        this.totalElements = totalElements;
        this.page = page;
        this.size = size;
        this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        this.first = page == 0;
        this.last = page >= totalPages - 1;
    }
}
