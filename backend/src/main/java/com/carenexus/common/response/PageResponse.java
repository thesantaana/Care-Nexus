package com.carenexus.common.response;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.ArrayList;
import java.util.List;

public class PageResponse<T> {

    private List<T> records = new ArrayList<>();
    private long pageNo;
    private long pageSize;
    private long total;
    private long pages;

    public PageResponse() {
    }

    public PageResponse(List<T> records, long pageNo, long pageSize, long total, long pages) {
        this.records = records;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.total = total;
        this.pages = pages;
    }

    public static <T> PageResponse<T> from(Page<?> page, List<T> records) {
        return new PageResponse<>(records, page.getCurrent(), page.getSize(), page.getTotal(), page.getPages());
    }

    public static <T> PageResponse<T> empty(long pageNo, long pageSize) {
        return new PageResponse<>(new ArrayList<T>(), pageNo, pageSize, 0, 0);
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public long getPageNo() {
        return pageNo;
    }

    public void setPageNo(long pageNo) {
        this.pageNo = pageNo;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }
}
