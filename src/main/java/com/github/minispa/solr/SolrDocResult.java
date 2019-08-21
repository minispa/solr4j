package com.github.minispa.solr;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SolrDocResult<T> implements Serializable {

    private long numFound;
    private long start;
    private List<T> docs;

    public SolrDocResult() {}

    public SolrDocResult(long numFound, long start, List<T> docs) {
        this.numFound = numFound;
        this.start = start;
        this.docs = docs;
    }
}
