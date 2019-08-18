package com.github.minispa.solr;

import java.io.Serializable;

public class SolrObject<T> implements Serializable {

    private String collection;

    private T object;

    public SolrObject() {}

    public SolrObject(String collection, T object) {
        this.collection = collection;
        this.object = object;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }
}
