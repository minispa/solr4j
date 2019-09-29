package com.github.minispa.solr;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class SolrHighlightingResult<T> implements Serializable {

    private SolrDocResult<T> docResult;
    private Map<String, Map<String, List<String>>> highlighting;

    public SolrHighlightingResult() {}

    public SolrHighlightingResult(SolrDocResult<T> docResult, Map<String, Map<String, List<String>>> highlighting) {
        this.docResult = docResult;
        this.highlighting = highlighting;
    }

}
