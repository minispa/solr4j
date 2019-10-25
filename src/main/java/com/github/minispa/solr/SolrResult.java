package com.github.minispa.solr;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class SolrResult<T> implements Serializable {

    private Map<String, Object> header;

    private SolrDocResult<T> docResult;

    private String nextCursorMark;

    private Map<String, Map<String, List<String>>> highlighting;

    private Map<String, SolrGroupMatche<T>> grouped;

}
