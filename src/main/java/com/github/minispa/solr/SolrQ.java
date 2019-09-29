package com.github.minispa.solr;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class SolrQ implements Serializable {

    private Map<String, String[]> vals = new LinkedHashMap<>();

    // ===============================================================================
    // constructor
    // ===============================================================================
    public SolrQ() {
        set(Q, "*:*");
    }

    public SolrQ(String q) {
        this();
        set(Q, q);
    }

    public SolrQ(String name, String val, String... params) {
        this();
        assert params.length % 2 == 0;
        set(name, val);
        for (int i = 0; i < params.length; i += 2) {
            set(params[i], params[i + 1]);
        }
    }

    public SolrQ(Map<String, String[]> QMap) {
        this();
        vals.putAll(QMap);
    }

    // ===============================================================================
    // param
    // ===============================================================================
    public SolrQ setParam(String name, String... values) {
        set(name, values);
        return this;
    }

    public SolrQ setParam(String name, boolean value) {
        set(name, value);
        return this;
    }

    public String[] getParams(String name) {
        return vals.get(name);
    }

    public String getParam(String name) {
        return get(name);
    }

    public Map<String, String[]> getParams() {
        return vals;
    }

    public SolrQ addParams(Map<String, String[]> params) {
        vals.putAll(params);
        return this;
    }

    // ===============================================================================
    // q
    // ===============================================================================
    public SolrQ setQuery(String q) {
        set(Q, q);
        return this;
    }

    public String getQuery() {
        return get(Q);
    }

    // ===============================================================================
    // qt: request handler
    // ===============================================================================
    public SolrQ setRequestHandler(String qt) {
        set(QT, qt);
        return this;
    }

    public String getRequestHandler() {
        return get(QT);
    }

    // ===============================================================================
    // fq: filter query
    // ===============================================================================
    public SolrQ addFilterQuery(String... fq) {
        add(FQ, fq);
        return this;
    }

    public SolrQ setFilterQueries(String... fq) {
        set(FQ, fq);
        return this;
    }

    public String[] getFilterQueries() {
        return getParams(FQ);
    }

    public boolean removeFilterQuery(String fq) {
        return remove(FQ, fq);
    }

    // ===============================================================================
    // rows
    // ===============================================================================
    public SolrQ setRows(Integer rows) {
        if (rows == null) {
            remove(ROWS);
        } else {
            set(ROWS, rows);
        }
        return this;
    }

    public Integer getRows() {
        String v = get(ROWS);
        if (v == null || "".equals(v)) {
            return null;
        }
        return Integer.valueOf(v);
    }

    // ===============================================================================
    // start
    // ===============================================================================
    public SolrQ setStart(Integer start) {
        if (start == null) {
            remove(START);
        } else {
            set(START, start);
        }
        return this;
    }

    public Integer getStart() {
        String v = get(START);
        if (v == null || "".equals(v)) {
            return null;
        }
        return Integer.valueOf(v);
    }

    // ===============================================================================
    // sort
    // ===============================================================================
    public SolrQ setSortQueries(String... sortQueries) {
        if(sortQueries == null || sortQueries.length == 0) {
            remove(SORT);
        }
        set(SORT, join(",", sortQueries));
        return this;
    }

    public SolrQ addSort(String field, ORDER order) {
        String sortQ = get(SORT);
        if (sortQ == null || "".equals(sortQ)) {
            setSortQueries(String.format("%s %s", field, order.name()));
        } else {
            setSortQueries(String.format("%s,%s %s", sortQ, field, order.name()));
        }
        return this;
    }

    public SolrQ setSort(String field, ORDER order) {
        setSortQueries(String.format("%s %s", field, order.name()));
        return this;
    }

    public SolrQ setSort(String field, ORDER order, String fnext, ORDER onext) {
        setSortQueries(String.format("%s %s,%s %s", field, order.name(), fnext, onext.name()));
        return this;
    }

    public SolrQ setSort(String field, ORDER order, String fnext, ORDER onext, String flast, ORDER olast) {
        setSortQueries(String.format("%s %s,%s %s,%s %s", field, order.name(), fnext, onext.name(), flast, olast.name()));
        return this;
    }

    // ===============================================================================
    // fl: filter field
    // ===============================================================================

    public SolrQ setFields(String... fields) {
        if (fields == null || fields.length == 0) {
            remove(FL);
            return this;
        }
        set(FL, join(",", fields));
        return this;
    }

    public SolrQ addField(String field) {
        String fieldQ = get(FL);
        if(fieldQ == null || "".equals(fieldQ)) {
            set(FL, field);
        } else {
            set(FL, String.format("%s,%s", fieldQ, field));
        }
        return this;
    }

    public SolrQ setFieldQueries(String fl) {
        if(fl == null || "".equals(fl)) {
            remove(FL);
        } else {
            set(FL, fl);
        }
        return this;
    }


    // ===============================================================================
    // hl
    // ===============================================================================
    public SolrQ setHighlight(boolean b) {
        if (b) {
            set(HIGHLIGHT, true);
        } else {
            remove(HIGHLIGHT);
            remove(FIELD_MATCH);
            remove(FIELDS);
            remove(FORMATTER);
            remove(FRAGSIZE);
            remove(SIMPLE_POST);
            remove(SIMPLE_PRE);
            remove(SNIPPETS);
        }
        return this;
    }

    public SolrQ addHighlightField(String field) {
        add(FIELDS, field);
        set(HIGHLIGHT, true);
        return this;
    }

    public SolrQ setHighlightSnippets(int num) {
        set(SNIPPETS, num);
        return this;
    }

    public SolrQ setHighlightFragsize(int num) {
        set(FRAGSIZE, num);
        return this;
    }

    public SolrQ setHighlightRequireFieldMatch(boolean flag) {
        set(FIELD_MATCH, flag);
        return this;
    }

    public SolrQ setHighlightSimplePre(String f) {
        set(SIMPLE_PRE, f);
        return this;
    }

    public SolrQ setHighlightSimplePost(String f) {
        set(SIMPLE_POST, f);
        return this;
    }

    // ===============================================================================
    // distrib
    // ===============================================================================
    public SolrQ setDistrib(boolean val) {
        set(DISTRIB, String.valueOf(val));
        return this;
    }


    // ===============================================================================
    // ===============================================================================
    // ===============================================================================
    // ===============================================================================

    public static final String DISTRIB = "distrib";
    public static final String START = "start";
    public static final String ROWS = "rows";
    public static final String Q = "q";
    public static final String QT = "qt";
    public static final String FQ = "fq";
    public static final String FL = "fl";
    public static final String SORT = "sort";

    public static final String HIGHLIGHT = "hl";
    public static final String FIELD_MATCH = "hl.requireFieldMatch";
    public static final String FIELDS = "hl.fl";
    public static final String FORMATTER = "hl.formatter";
    public static final String FRAGSIZE = "hl.fragsize";
    public static final String SIMPLE_POST = "hl.simple.post";
    public static final String SIMPLE_PRE = "hl.simple.pre";
    public static final String SNIPPETS = "hl.snippets";

    public static final String SCORE = "score";


    // ===============================================================================
    // ===============================================================================
    // ===============================================================================
    // ===============================================================================

    private SolrQ set(String name, String... vals) {
        if (vals == null || (vals.length == 1 && vals[0] == null)) {
            this.vals.remove(name);
        } else {
            this.vals.put(name, vals);
        }
        return this;
    }

    private SolrQ set(String name, int val) {
        set(name, String.valueOf(val));
        return this;
    }

    private SolrQ set(String name, boolean val) {
        set(name, String.valueOf(val));
        return this;
    }

    private String get(String param) {
        String[] v = vals.get(param);
        if (v != null && v.length > 0) {
            return v[0];
        }
        return null;
    }

    private SolrQ add(String name, String... val) {
        String[] old = vals.put(name, val);
        if (old != null) {
            if (val == null || val.length < 1) {
                String[] both = new String[old.length + 1];
                System.arraycopy(old, 0, both, 0, old.length);
                both[old.length] = null;
                vals.put(name, both);
            } else {
                String[] both = new String[old.length + val.length];
                System.arraycopy(old, 0, both, 0, old.length);
                System.arraycopy(val, 0, both, old.length, val.length);
                vals.put(name, both);
            }
        }
        return this;
    }

    private String[] remove(String name) {
        return this.vals.remove(name);
    }

    private boolean remove(String name, String value) {
        String[] tmp = vals.get(name);
        if (tmp == null) return false;
        for (int i = 0; i < tmp.length; i++) {
            if (tmp[i].equals(value)) {
                String[] tmp2 = new String[tmp.length - 1];
                if (tmp2.length == 0) {
                    tmp2 = null;
                    remove(name);
                } else {
                    System.arraycopy(tmp, 0, tmp2, 0, i);
                    System.arraycopy(tmp, i + 1, tmp2, i, tmp.length - i - 1);
                    set(name, tmp2);
                }
                return true;
            }
        }
        return false;
    }

    public void clear() {
        vals.clear();
    }

    private static String join(CharSequence delimiter, String... vals) {
        if(vals == null || vals.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(vals[0]);
        for (int i = 1; i < vals.length; i++) {
            sb.append(delimiter);
            sb.append(vals[i]);
        }
        return sb.toString();
    }

    public enum ORDER {
        asc, desc
    }

    public String toQueryString() {
        try {
            final String charset = StandardCharsets.UTF_8.name();
            final StringBuilder sb = new StringBuilder(128);
            boolean first = true;
            for (final Iterator<String> it = getParameterNamesIterator(); it.hasNext();) {
                final String name = it.next(), nameEnc = URLEncoder.encode(name, charset);
                for (String val : getParams(name)) {
                    sb.append(first ? '?' : '&').append(nameEnc).append('=').append(URLEncoder.encode(val, charset));
                    first = false;
                }
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    public Iterator<String> getParameterNamesIterator() {
        return vals.keySet().iterator();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(128);
        boolean first = true;
        for (final Iterator<String> it = getParameterNamesIterator(); it.hasNext();) {
            final String name = it.next();
            for (String val : getParams(name)) {
                sb.append(first ? "" : '&').append(name).append('=').append(val);
                first = false;
            }
        }
        return sb.toString();
    }

}
