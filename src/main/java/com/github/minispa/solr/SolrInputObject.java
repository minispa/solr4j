package com.github.minispa.solr;


import java.io.Serializable;
import java.util.*;

public class SolrInputObject implements Map<String, SolrInputField>, Iterable<SolrInputField>, Serializable {

    private Map<String, SolrInputField> _fields;
    private List<SolrInputObject> _childDocuments;

    public SolrInputObject(String... fields) {
        _fields = new LinkedHashMap<>();
        assert fields.length % 2 == 0;
        for (int i = 0; i < fields.length; i += 2) {
            addField(fields[i], fields[i + 1]);
        }
    }

    public SolrInputObject(Map<String, SolrInputField> fields) {
        _fields = fields;
    }

    @Override
    public void clear() {
        if (_fields != null) {
            _fields.clear();
        }
        _childDocuments = null;
    }

    ///////////////////////////////////////////////////////////////////
    // Add / Set fields
    ///////////////////////////////////////////////////////////////////

    public void addField(String name, Object value) {
        SolrInputField field = _fields.get(name);
        if (field == null || field.value == null) {
            setField(name, value);
        } else {
            field.addValue(value);
        }
    }

    public Object getFieldValue(String name) {
        SolrInputField field = getField(name);
        Object o = null;
        if (field != null) o = field.getFirstValue();
        return o;
    }

    public Collection<Object> getFieldValues(String name) {
        SolrInputField field = getField(name);
        if (field != null) {
            return field.getValues();
        }
        return null;
    }

    public Collection<String> getFieldNames() {
        return _fields.keySet();
    }

    public void setField(String name, Object value) {
        SolrInputField field = new SolrInputField(name);
        _fields.put(name, field);
        field.setValue(value);
    }

    public SolrInputField removeField(String name) {
        return _fields.remove(name);
    }

    ///////////////////////////////////////////////////////////////////
    // Get the field values
    ///////////////////////////////////////////////////////////////////

    public SolrInputField getField(String field) {
        return _fields.get(field);
    }

    @Override
    public Iterator<SolrInputField> iterator() {
        return _fields.values().iterator();
    }

    @Override
    public String toString() {
        return "SolrInputObject(fields: " + _fields.values()
                + (_childDocuments == null ? "" : (", children: " + _childDocuments))
                + ")";
    }

    public SolrInputObject deepCopy() {
        SolrInputObject clone = new SolrInputObject();
        Set<Map.Entry<String, SolrInputField>> entries = _fields.entrySet();
        for (Map.Entry<String, SolrInputField> fieldEntry : entries) {
            clone._fields.put(fieldEntry.getKey(), fieldEntry.getValue().deepCopy());
        }

        if (_childDocuments != null) {
            clone._childDocuments = new ArrayList<>(_childDocuments.size());
            for (SolrInputObject child : _childDocuments) {
                clone._childDocuments.add(child.deepCopy());
            }
        }

        return clone;
    }

    //---------------------------------------------------
    // MAP interface
    //---------------------------------------------------

    @Override
    public boolean containsKey(Object key) {
        return _fields.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return _fields.containsValue(value);
    }

    @Override
    public Set<Map.Entry<String, SolrInputField>> entrySet() {
        return _fields.entrySet();
    }

    @Override
    public SolrInputField get(Object key) {
        return _fields.get(key);
    }

    @Override
    public boolean isEmpty() {
        return _fields.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return _fields.keySet();
    }

    @Override
    public SolrInputField put(String key, SolrInputField value) {
        return _fields.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends SolrInputField> t) {
        _fields.putAll(t);
    }

    @Override
    public SolrInputField remove(Object key) {
        return _fields.remove(key);
    }

    @Override
    public int size() {
        return _fields.size();
    }

    @Override
    public Collection<SolrInputField> values() {
        return _fields.values();
    }

    public void addChildDocument(SolrInputObject child) {
        if (_childDocuments == null) {
            _childDocuments = new ArrayList<>();
        }
        _childDocuments.add(child);
    }

    public void addChildDocuments(Collection<SolrInputObject> children) {
        for (SolrInputObject child : children) {
            addChildDocument(child);
        }
    }

    /**
     * Returns the list of child documents, or null if none.
     */
    public List<SolrInputObject> getChildDocuments() {
        return _childDocuments;
    }

    public boolean hasChildDocuments() {
        boolean isEmpty = (_childDocuments == null || _childDocuments.isEmpty());
        return !isEmpty;
    }

    public int getChildDocumentCount() {
        return hasChildDocuments() ? _childDocuments.size() : 0;
    }
}
