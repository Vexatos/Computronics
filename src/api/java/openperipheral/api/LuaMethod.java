package openperipheral.api;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LuaMethod {
	public static final String USE_METHOD_NAME = "[none set]";

	boolean onTick() default true;

	String name() default USE_METHOD_NAME;

	String description() default "";

	LuaType returnType() default LuaType.VOID;

	Arg[] args() default {};
}