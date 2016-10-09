package org.bs.jnonogram.wui.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ApiEntityName {
    String singular();
    String plural();
}
