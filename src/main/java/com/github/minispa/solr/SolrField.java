package com.github.minispa.solr;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SolrField {

    boolean child() default false;

    String value() default SolrDocField.DEFAULT;

}

