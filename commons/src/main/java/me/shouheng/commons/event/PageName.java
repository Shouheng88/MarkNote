package me.shouheng.commons.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation used to identify the page name.
 *
 * Created by WngShhng on 2018/12/7.
 * Contact me: shouheng2015@gmail.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PageName {
    String name();
}
