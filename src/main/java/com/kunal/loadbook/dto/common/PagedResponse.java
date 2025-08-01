package com.kunal.loadbook.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Paginated response wrapper")
public class PagedResponse<T> {
    
    @Schema(description = "List of items in current page")
    private List<T> content;
    
    @Schema(description = "Current page number (0-indexed)")
    private int page;
    
    @Schema(description = "Size of each page")
    private int size;
    
    @Schema(description = "Total number of elements")
    private long totalElements;
    
    @Schema(description = "Total number of pages")
    private int totalPages;
    
    @Schema(description = "Is this the first page")
    private boolean first;
    
    @Schema(description = "Is this the last page")
    private boolean last;
    
    @Schema(description = "Is there a next page")
    private boolean hasNext;
    
    @Schema(description = "Is there a previous page")
    private boolean hasPrevious;
    
    // Default constructor
    public PagedResponse() {}
    
    // Constructor
    public PagedResponse(List<T> content, int page, int size, long totalElements, int totalPages,
                        boolean first, boolean last, boolean hasNext, boolean hasPrevious) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }
    
    // Getters and Setters
    public List<T> getContent() {
        return content;
    }
    
    public void setContent(List<T> content) {
        this.content = content;
    }
    
    public int getPage() {
        return page;
    }
    
    public void setPage(int page) {
        this.page = page;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    public long getTotalElements() {
        return totalElements;
    }
    
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    
    public boolean isFirst() {
        return first;
    }
    
    public void setFirst(boolean first) {
        this.first = first;
    }
    
    public boolean isLast() {
        return last;
    }
    
    public void setLast(boolean last) {
        this.last = last;
    }
    
    public boolean isHasNext() {
        return hasNext;
    }
    
    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }
    
    public boolean isHasPrevious() {
        return hasPrevious;
    }
    
    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }
}
