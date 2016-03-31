package com.bestjoy.app.common.qrcode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by bestjoy on 16/3/8.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParsedResultTypeAnnotation {

    /**默认是文本类型*/
    public String parsedResultTypeName() default "TEXT";
}
