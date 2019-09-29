package com.github.minispa.solr;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import jdk.internal.org.objectweb.asm.util.TraceClassVisitor;

import java.util.*;

public class SolrResultTransfer {

    public static <T> SolrGroupResult<T> toGroupResult(Class<T> tClass, JSONObject groupedObj) {
        Map<String, SolrGroupMatche<T>> mapGrouped = new HashMap<>();
        for(String field : groupedObj.keySet()) {
            JSONObject jsonObject = groupedObj.getJSONObject(field);
            Integer matches = jsonObject.getInteger("matches");
            JSONArray groupsObj = jsonObject.getJSONArray("groups");
            List<SolrGroup<T>> groups = new ArrayList<>();
            for(int i = 0; i < groupsObj.size(); i++) {
                JSONObject group = groupsObj.getJSONObject(i);
                String groupValue = group.getString("groupValue");
                JSONObject doclist = group.getJSONObject("doclist");
                Integer numFound = doclist.getInteger("numFound");
                Integer start = doclist.getInteger("start");
                JSONArray docs = doclist.getJSONArray("docs");
                SolrDocResult<T> docResult = new SolrDocResult<>(numFound, start, SolrObjectHelper.getBeans(tClass, docs));
                SolrGroup<T> solrGroup = new SolrGroup<>(groupValue, docResult);
                groups.add(solrGroup);
            }
            SolrGroupMatche<T> groupMatche = new SolrGroupMatche<>(matches, groups);
            mapGrouped.put(field, groupMatche);
        }
        return new SolrGroupResult<>(mapGrouped);
    }

    public static <T> SolrHighlightingResult<T> toHighlightingResult(Class<T> tClass, JSONObject obj) {
        JSONObject responseObj = obj.getJSONObject("response");
        SolrDocResult<T> docResult = new SolrDocResult<>(responseObj.getLongValue("numFound"),
                responseObj.getLongValue("start"),
                SolrObjectHelper.getBeans(tClass, responseObj.getJSONArray("docs")));
        JSONObject highlightingObj = obj.getJSONObject("highlighting");
        Map<String, Map<String, List<String>>> highlighting = new HashMap<>();
        for (String id : highlightingObj.keySet()) {
            Map<String, List<String>> fieldValues = new HashMap<>();

            JSONObject fieldObj = highlightingObj.getJSONObject(id);
            for (String field : fieldObj.keySet()) {
                JSONArray valueObjs = fieldObj.getJSONArray(field);
                fieldValues.put(field, Arrays.asList(valueObjs.toArray(new String[0])));
            }

            highlighting.put(id, fieldValues);
        }
        return new SolrHighlightingResult<>(docResult, highlighting);
    }

    private SolrResultTransfer() {}

}
