package com.github.minispa.solr;

import com.alibaba.fastjson.JSONObject;
import com.github.minispa.solr.update.SolrUpdateObject;

import java.util.Collection;
import java.util.List;

public interface SolrOperator {

    <T> void addObject(T object);

    void addObjects(Collection<Object> objects);

    void deleteByQuery(String collection, String query);

    void deleteById(String collection, String...ids);

    <T> SolrResult<T> query(String collection, SolrQ solrQ, Class<T> tClass);

    JSONObject query(String collection, SolrQ solrQ);

    <T> SolrDocResult<T> query(SolrQ solrQ, Class<T> tClass);

    <T> T queryById(String id, Class<T> tClass);

    <T> List<T> queryById(Collection<String> ids, Class<T> tClass);

    void deltaImport(String collection);

    void fullImport(String collection);

    void update(SolrUpdateObject atomicUpdateObject);
}
