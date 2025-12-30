package dev.yooproject.funitems.commands.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Arg {
    String name();
    boolean optional() default false;
    String defaultValue() default "";
    Class<?> type() default String.class;
}
