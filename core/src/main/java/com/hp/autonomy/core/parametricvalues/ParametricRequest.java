package com.hp.autonomy.core.parametricvalues;

import java.io.Serializable;
import java.util.Set;

public interface ParametricRequest<S extends Serializable> extends Serializable {
    Set<S> getDatabases();

    Set<String> getFieldNames();

    String getQueryText();

    String getFieldText();
}
