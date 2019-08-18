package com.github.minispa.solr;

public interface SolrOperator {

    <T> boolean addOrUpdate(T object);

    boolean deleteByQuery(String collection, SolrQ solrQ);

    boolean deleteById(String collection, String id);

    Object query(String collection, SolrQ solrQ);

    <T> SolrQDocResult<T> query(String collection, SolrQ solrQ, Class<T> tClass);

}
