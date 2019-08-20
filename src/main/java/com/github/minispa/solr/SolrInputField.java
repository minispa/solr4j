package com.github.minispa.solr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class SolrInputField implements Iterable<Object>, Serializable {

    String name;
    Object value = null;

    public SolrInputField(String n) {
        this.name = n;
    }

    //---------------------------------------------------------------
    //---------------------------------------------------------------

    public void setValue(Object v) {
        if (v instanceof Object[]) {
            Object[] arr = (Object[]) v;
            Collection<Object> c = new ArrayList<>(arr.length);
            for (Object o : arr) {
                c.add(o);
            }
            value = c;
        } else {
            value = v;
        }
    }

    @SuppressWarnings("unchecked")
    public void addValue(Object v) {
        if (value == null) {
            if (v instanceof Collection) {
                Collection<Object> c = new ArrayList<>(3);
                for (Object o : (Collection<Object>) v) {
                    c.add(o);
                }
                setValue(c);
            } else {
                setValue(v);
            }

            return;
        }

        Collection<Object> vals = null;
        if (value instanceof Collection) {
            vals = (Collection<Object>) value;
        } else {
            vals = new ArrayList<>(3);
            vals.add(value);
            value = vals;
        }

        if (v instanceof Iterable && !(v instanceof SolrInputObject)) {
            for (Object o : (Iterable<Object>) v) {
                vals.add(o);
            }
        } else if (v instanceof Object[]) {
            for (Object o : (Object[]) v) {
                vals.add(o);
            }
        } else {
            vals.add(v);
        }
    }

    //---------------------------------------------------------------
    //---------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public Object getFirstValue() {
        if (value instanceof Collection) {
            Collection c = (Collection<Object>) value;
            if (c.size() > 0) {
                return c.iterator().next();
            }
            return null;
        }
        return value;
    }

    public Object getValue() {
        return value;
    }

    @SuppressWarnings("unchecked")
    public Collection<Object> getValues() {
        if (value instanceof Collection) {
            return (Collection<Object>) value;
        }
        if (value != null) {
            Collection<Object> vals = new ArrayList<>(1);
            vals.add(value);
            return vals;
        }
        return null;
    }

    public int getValueCount() {
        if (value instanceof Collection) {
            return ((Collection) value).size();
        }
        return (value == null) ? 0 : 1;
    }

    //---------------------------------------------------------------
    //---------------------------------------------------------------

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<Object> iterator() {
        if (value instanceof Collection) {
            return ((Collection) value).iterator();
        }
        return new Iterator<Object>() {
            boolean nxt = (value != null);

            @Override
            public boolean hasNext() {
                return nxt;
            }

            @Override
            public Object next() {
                nxt = false;
                return value;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public String toString() {
        return name + "=" + value;
    }

    @SuppressWarnings("unchecked")
    public SolrInputField deepCopy() {
        SolrInputField clone = new SolrInputField(name);
        if (value instanceof Collection) {
            Collection<Object> values = (Collection<Object>) value;
            Collection<Object> cloneValues = new ArrayList<>(values.size());
            cloneValues.addAll(values);
            clone.value = cloneValues;
        } else {
            clone.value = value;
        }
        return clone;
    }
}
