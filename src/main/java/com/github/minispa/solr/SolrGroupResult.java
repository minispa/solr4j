package com.github.minispa.solr;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class SolrGroupResult<T> implements Serializable {

    private Map<String, SolrGroupMatche<T>> grouped;

    public SolrGroupResult() {}

    public SolrGroupResult(Map<String, SolrGroupMatche<T>> grouped) {
        this.grouped = grouped;
    }

}
