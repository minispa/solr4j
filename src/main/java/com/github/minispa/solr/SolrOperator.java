package com.github.minispa.solr;

import java.util.Collection;
import java.util.List;

public interface SolrOperator {

    <T> void addOrUpdate(T object);

    void addOrUpdate(Collection<Object> objects);

    void deleteByQuery(String collection, String query);

    void deleteById(String collection, List<String> ids);

    Object query(String collection, SolrQ solrQ);

    <T> SolrDocResult<T> query(SolrQ solrQ, Class<T> tClass);

    <T> T queryById(String id, Class<T> tClass);

    <T> List<T> queryById(Collection<String> ids, Class<T> tClass);

    void deltaImport(String collection);

    void fullImport(String collection);

}
