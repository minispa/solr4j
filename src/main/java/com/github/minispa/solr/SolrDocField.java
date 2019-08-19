package com.github.minispa.solr;

import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Pattern;

@Data
public class SolrDocField implements Serializable {

    public static final String DEFAULT = "#default";

    private SolrField annotation;
    private String name;
    private Field field;
    private Method setter;
    private Method getter;
    private Class type;
    private boolean isArray;
    private boolean isList;
    private List<SolrDocField> child;

    private boolean isContainedInMap;
    private Pattern dynamicFieldNamePatternMatcher;

    public SolrDocField(AccessibleObject member) {
        if (member instanceof Field) {
            field = (Field) member;
        } else {
            setter = (Method) member;
        }
        annotation = member.getAnnotation(SolrField.class);
        storeName(annotation);
        storeType();

        if (setter != null) {
            String gname = setter.getName();
            if (gname.startsWith("set")) {
                gname = "get" + gname.substring(3);
                try {
                    getter = setter.getDeclaringClass().getMethod(gname, (Class[]) null);
                } catch (Exception e) {
                    if (type == Boolean.class) {
                        gname = "is" + setter.getName().substring(3);
                        try {
                            getter = setter.getDeclaringClass().getMethod(gname, (Class[]) null);
                        } catch (Exception ex) {
                        }
                    }
                }
            }
        }
    }

    private void storeName(SolrField annotation) {
        if (annotation.value().equals(DEFAULT)) {
            if (field != null) {
                name = field.getName();
            } else {
                String setterName = setter.getName();
                if (setterName.startsWith("set") && setterName.length() > 3) {
                    name = setterName.substring(3, 4).toLowerCase(Locale.ROOT) + setterName.substring(4);
                } else {
                    name = setter.getName();
                }
            }
        } else if (annotation.value().indexOf('*') >= 0) {
            name = annotation.value().replaceFirst("\\*", "\\.*");
            dynamicFieldNamePatternMatcher = Pattern.compile("^" + name + "$");
        } else {
            name = annotation.value();
        }
    }

    private void storeType() {
        if (field != null) {
            type = field.getType();
        } else {
            Class[] params = setter.getParameterTypes();
            if (params.length != 1) {
                throw new SolrBindingException("Invalid setter method. Must have one and only one parameter");
            }
            type = params[0];
        }

        if (type == Collection.class || type == List.class || type == ArrayList.class) {
            isList = true;
            if (annotation.child()) {
                populateChild(field.getGenericType());
            } else {
                type = Object.class;
            }
        } else if (type == byte[].class) {
            //no op
        } else if (type.isArray()) {
            isArray = true;
            if (annotation.child()) {
                populateChild(type.getComponentType());
            } else {
                type = type.getComponentType();
            }
        } else if (type == Map.class || type == HashMap.class) {
            if (annotation.child())
                throw new SolrBindingException("Map should is not a valid type for a child document");
            isContainedInMap = true;
            type = Object.class;
            if (field != null) {
                if (field.getGenericType() instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                    Type[] types = parameterizedType.getActualTypeArguments();
                    if (types != null && types.length == 2 && types[0] == String.class) {
                        if (types[1] instanceof Class) {
                            if (types[1] == Collection.class || types[1] == List.class || types[1] == ArrayList.class) {
                                type = Object.class;
                                isList = true;
                            } else {
                                type = (Class) types[1];
                            }
                        } else if (types[1] instanceof ParameterizedType) {
                            Type rawType = ((ParameterizedType) types[1]).getRawType();
                            if (rawType == Collection.class || rawType == List.class || rawType == ArrayList.class) {
                                type = Object.class;
                                isList = true;
                            }
                        } else if (types[1] instanceof GenericArrayType) {
                            type = (Class) ((GenericArrayType) types[1]).getGenericComponentType();
                            isArray = true;
                        } else {
                            throw new SolrBindingException("Allowed type for values of mapping a dynamicField are : Object, Object[] and List");
                        }
                    }
                }
            }
        } else {
            if (annotation.child()) {
                populateChild(type);
            }
        }
    }

    private void populateChild(Type typ) {
        if (typ == null) {
            throw new RuntimeException("no type information available for" + (field == null ? setter : field));
        }
        if (typ.getClass() == Class.class) {//of type class
            type = (Class) typ;
        } else if (typ instanceof ParameterizedType) {
            try {
                type = Class.forName(((ParameterizedType) typ).getActualTypeArguments()[0].getTypeName());
            } catch (ClassNotFoundException e) {
                throw new SolrBindingException("Invalid type information available for" + (field == null ? setter : field));
            }
        } else {
            throw new SolrBindingException("Invalid type information available for" + (field == null ? setter : field));

        }
        child = SolrObjectHelper.getDocFields(type);
    }


    public Object get(final Object obj) {
        if (field != null) {
            try {
                return field.get(obj);
            } catch (Exception e) {
                throw new SolrBindingException("Exception while getting value: " + field, e);
            }
        } else if (getter == null) {
            throw new SolrBindingException("Missing getter for field: " + name + " -- You can only call the 'get' for fields that have a field of 'get' method");
        }

        try {
            return getter.invoke(obj, (Object[]) null);
        } catch (Exception e) {
            throw new SolrBindingException("Exception while getting value: " + getter, e);
        }
    }

}
