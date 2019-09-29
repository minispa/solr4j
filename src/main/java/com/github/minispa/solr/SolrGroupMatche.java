package com.github.minispa.solr;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SolrGroupMatche<T> implements Serializable {

    private Integer matches;
    private List<SolrGroup<T>> groups;

    public SolrGroupMatche() {}

    public SolrGroupMatche(Integer matches, List<SolrGroup<T>> groups) {
        this.matches = matches;
        this.groups = groups;
    }
}
