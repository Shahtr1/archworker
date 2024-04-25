package com.archworker.coreapplication.util.common;

import java.util.Collections;
import java.util.List;

public class CommonMethods {
    public static List<String> getSingletonListFromKeyAndValue(String key, String value) {
        return Collections.singletonList(key + ": " + value);
    }
}
