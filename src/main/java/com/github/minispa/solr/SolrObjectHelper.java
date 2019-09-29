package com.github.minispa.solr;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.*;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SolrObjectHelper {

    private final static Map<Class, List<SolrDocField>> infoCache = new ConcurrentHashMap<>();


    public static <T> List<T> getBeans(Class<T> clazz, JSONArray solrDocList) {
        List<SolrDocField> fields = getDocFields(clazz);
        List<T> result = new ArrayList<>(solrDocList.size());
        for (int i = 0; i < solrDocList.size(); i++) {
            JSONObject sdoc = solrDocList.getJSONObject(i);
            result.add(getBean(clazz, fields, sdoc));
        }
        return result;
    }

    public static <T> T getBean(Class<T> clazz, JSONObject solrDoc) {
        return getBean(clazz, null, solrDoc);
    }

    private static <T> T getBean(Class<T> clazz, List<SolrDocField> fields, JSONObject solrDoc) {
        if (fields == null) {
            fields = getDocFields(clazz);
        }

        try {
            T obj = clazz.newInstance();
            for (SolrDocField docField : fields) {
                inject(docField, obj, solrDoc);
            }
            return obj;
        } catch (Exception e) {
            throw new SolrBindingException("Could not instantiate object of " + clazz, e);
        }
    }

    public static List<SolrDocField> getDocFields(Class clazz) {
        List<SolrDocField> fields = infoCache.get(clazz);
        if(fields == null) {
            synchronized(infoCache) {
                fields = infoCache.get(clazz);
                if(fields != null) {
                    return fields;
                }
                infoCache.put(clazz, fields = collectInfo(clazz));
            }
        }
        return fields;
    }

    public static String getCollection(Class<?> clazz) {
        SolrCollection solrCollection = clazz.getDeclaredAnnotation(SolrCollection.class);
        return solrCollection.value();
    }

    public static <T> SolrObject<SolrInputObject> toSolrObject(T object) {
        return new SolrObject<>(getCollection(object.getClass()), toSolrInputObject(object));
    }

    public static SolrInputObject toSolrInputObject(Object obj) {
        List<SolrDocField> fields = getDocFields(obj.getClass());
        if (fields.isEmpty()) {
            throw new SolrBindingException("class: " + obj.getClass() + " does not define any fields.");
        }

        SolrInputObject doc = new SolrInputObject();
        for (SolrDocField field : fields) {
            if (field.getDynamicFieldNamePatternMatcher() != null &&
                    field.get(obj) != null &&
                    field.isContainedInMap()) {
                Map<String, Object> mapValue = (Map<String, Object>) field.get(obj);

                for (Map.Entry<String, Object> e : mapValue.entrySet()) {
                    doc.setField(e.getKey(), e.getValue());
                }
            } else {
                if (field.getChild() != null) {
                    addChild(obj, field, doc);
                } else {
                    doc.setField(field.getName(), field.get(obj));
                }
            }
        }
        return doc;
    }

    private static void addChild(Object obj, SolrDocField field, SolrInputObject doc) {
        Object val = field.get(obj);
        if (val == null) return;
        if (val instanceof Collection) {
            Collection collection = (Collection) val;
            for (Object o : collection) {
                SolrInputObject child = toSolrInputObject(o);
                doc.addChildDocument(child);
            }
        } else if (val.getClass().isArray()) {
            Object[] objs = (Object[]) val;
            for (Object o : objs) doc.addChildDocument(toSolrInputObject(o));
        } else {
            doc.addChildDocument(toSolrInputObject(val));
        }
    }


    private static List<SolrDocField> collectInfo(Class clazz) {
        List<SolrDocField> fields = new ArrayList<>();
        Class superClazz = clazz;
        List<AccessibleObject> members = new ArrayList<>();

        while (superClazz != null && superClazz != Object.class) {
            members.addAll(Arrays.asList(superClazz.getDeclaredFields()));
            members.addAll(Arrays.asList(superClazz.getDeclaredMethods()));
            superClazz = superClazz.getSuperclass();
        }
        boolean childFieldFound = false;
        for (AccessibleObject member : members) {
            if (member.isAnnotationPresent(SolrField.class)) {
                AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
                    member.setAccessible(true);
                    return null;
                });
                SolrDocField df = new SolrDocField(member);
                if (df.getChild() != null) {
                    if (childFieldFound)
                        throw new SolrBindingException(clazz.getName() + " cannot have more than one Field with child=true");
                    childFieldFound = true;
                }
                fields.add(df);
            }
        }
        return fields;
    }

    private static Object getFieldValue(SolrDocField docField, JSONObject solrDocument) {
        if (docField.getChild() != null) {
            JSONArray children = solrDocument.getJSONArray(docField.getName());
            if (children == null || children.isEmpty()) return null;
            if (docField.isList()) {
                ArrayList list = new ArrayList(children.size());
                for (int i = 0; i < children.size(); i++) {
                    JSONObject c = children.getJSONObject(i);
                    list.add(getBean(docField.getType(), docField.getChild(), c));
                }
                return list;
            } else if (docField.isArray()) {
                Object[] arr = (Object[]) Array.newInstance(docField.getType(), children.size());
                for (int i = 0; i < children.size(); i++) {
                    arr[i] = getBean(docField.getType(), docField.getChild(), children.getJSONObject(i));
                }
                return arr;

            } else {
                return getBean(docField.getType(), docField.getChild(), children.getJSONObject(0));
            }
        }
        Object fieldValue = solrDocument.get(docField.getName());
        if (fieldValue != null) {
            //this is not a dynamic field. so return the value
            return fieldValue;
        }

        if (docField.getDynamicFieldNamePatternMatcher() == null) {
            return null;
        }

        //reading dynamic field values
        Map<String, Object> allValuesMap = null;
        List allValuesList = null;
        if (docField.isContainedInMap()) {
            allValuesMap = new HashMap<>();
        } else {
            allValuesList = new ArrayList();
        }

        for (String field : solrDocument.keySet()) {
            if (docField.getDynamicFieldNamePatternMatcher().matcher(field).find()) {
                Object val = solrDocument.get(field);
                if (val == null) {
                    continue;
                }

                if (docField.isContainedInMap()) {
                    if (docField.isList()) {
                        if (!(val instanceof List)) {
                            List al = new ArrayList();
                            al.add(val);
                            val = al;
                        }
                    } else if (docField.isArray()) {
                        if (!(val instanceof List)) {
                            Object[] arr = (Object[]) Array.newInstance(docField.getType(), 1);
                            arr[0] = val;
                            val = arr;
                        } else {
                            val = Array.newInstance(docField.getType(), ((List) val).size());
                        }
                    }
                    allValuesMap.put(field, val);
                } else {
                    if (val instanceof Collection) {
                        allValuesList.addAll((Collection) val);
                    } else {
                        allValuesList.add(val);
                    }
                }
            }
        }
        if (docField.isContainedInMap()) {
            return allValuesMap.isEmpty() ? null : allValuesMap;
        } else {
            return allValuesList.isEmpty() ? null : allValuesList;
        }
    }

    private static <T> void inject(SolrDocField docField, T obj, JSONObject sdoc) {
        Object val = getFieldValue(docField, sdoc);
        if (val == null) {
            return;
        }

        if (docField.isArray() && !docField.isContainedInMap()) {
            List list;
            if (val.getClass().isArray()) {
                set(docField, obj, val);
                return;
            } else if (val instanceof List) {
                list = (List) val;
            } else {
                list = new ArrayList();
                list.add(val);
            }
            set(docField, obj, list.toArray((Object[]) Array.newInstance(docField.getType(), list.size())));
        } else if (docField.isList() && !docField.isContainedInMap()) {
            if (!(val instanceof List)) {
                List list = new ArrayList();
                list.add(val);
                val = list;
            }
            set(docField, obj, val);
        } else if (docField.isContainedInMap()) {
            if (val instanceof Map) {
                set(docField, obj, val);
            }
        } else {
            set(docField, obj, val);
        }

    }

    static void set(SolrDocField docField, Object obj, Object v) {
        if (v != null && docField.getType() == ByteBuffer.class && v.getClass() == byte[].class) {
            v = ByteBuffer.wrap((byte[]) v);
        }
        try {
            if (docField.getField() != null) {
                try {
                    docField.getField().set(obj, v);
                } catch (Exception e) {
                    // Oh, no!!! But, don't worry...
                }
            }
            if (docField.getSetter() != null) {
                docField.getSetter().invoke(obj, v);
            }
        } catch (Exception e) {
            throw new SolrBindingException("Exception while setting value : " + v + " on " + (docField.getField() != null ? docField.getField() : docField.getSetter()), e);
        }
    }

}
