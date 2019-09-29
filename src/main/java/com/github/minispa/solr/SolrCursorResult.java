package com.github.minispa.solr;

import lombok.Data;

import java.io.Serializable;

@Data
public class SolrCursorResult<T> implements Serializable {

    private SolrDocResult<T> docResult;

    private String nextCursorMark;

    public SolrCursorResult() {}

    public SolrCursorResult(SolrDocResult<T> docResult, String nextCursorMark) {
        this.docResult = docResult;
        this.nextCursorMark = nextCursorMark;
    }

}
