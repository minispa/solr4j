package com.github.minispa.solr;

import java.util.Collection;
import java.util.List;

public interface SolrOperator {

    <T> boolean addOrUpdate(T object);

    void addOrUpdate(Collection<Object> objects);

    boolean deleteByQuery(String collection, SolrQ solrQ);

    boolean deleteById(String collection, String id);

    Object query(String collection, SolrQ solrQ);

    <T> SolrDocResult<T> query(String collection, SolrQ solrQ, Class<T> tClass);

    <T> T query(String collection, String id, Class<T> tClass);

    <T> List<T> query(String collection, Collection<String> ids, Class<T> tClass);

}
