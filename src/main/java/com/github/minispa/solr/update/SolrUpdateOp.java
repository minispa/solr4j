package com.github.minispa.solr.update;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class SolrUpdateOp implements Serializable  {
    private String field;
    private Object[] values;
    private Op op;


    public enum Op {
        set, add, remove, inc, removeregix
    }
}
