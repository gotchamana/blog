package io.github.blog.validation;

import java.lang.annotation.*;

import javax.validation.*;

@Documented
@Constraint(validatedBy = NotEmptyValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEmpty {
    String message() default "Can't a empty file";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
