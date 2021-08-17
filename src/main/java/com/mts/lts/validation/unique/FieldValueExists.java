package com.mts.lts.validation.unique;

public interface FieldValueExists {

    boolean fieldValueExists(Object value, String fieldName) throws UnsupportedOperationException;
}
