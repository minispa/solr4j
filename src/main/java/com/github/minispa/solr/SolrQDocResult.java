package com.github.minispa.solr;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SolrQDocResult<T> implements Serializable {

    private long numFound;
    private long start;
    private List<T> docs;

}
