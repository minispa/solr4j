package com.github.minispa.solr.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class SolrUpdateObject implements Serializable {
    private String collection;
    private String id;
    private String value;
    private List<SolrUpdateOp> ops;
}
