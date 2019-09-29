package com.github.minispa.solr;

import lombok.Data;

import java.io.Serializable;

@Data
public class SolrGroup<T> implements Serializable {

    private String groupValue;
    private SolrDocResult<T> docResult;

    public SolrGroup() {}

    public SolrGroup(String groupValue, SolrDocResult<T> docResult) {
        this.groupValue = groupValue;
        this.docResult = docResult;
    }

}
