package dev.yooproject.funitems.commands.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandInfo {
    String name();
    String description() default "";
    String permission() default "";
    boolean debug() default false;
    boolean throwException() default true;
}