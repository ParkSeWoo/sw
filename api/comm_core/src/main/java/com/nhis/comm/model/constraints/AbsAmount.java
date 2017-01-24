package com.nhis.comm.model.constraints;

import javax.validation.Constraint;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 절대 금액 (필수)을 표현하는 제약 주석.
 */
@Documented
@Constraint(validatedBy = {})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@ReportAsSingleViolation
@NotNull
@Digits(integer = 16, fraction = 4)
@DecimalMin("0.00")
public @interface AbsAmount {
    String message() default "{error.domain.absAmount}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @OverridesAttribute(constraint = Digits.class, name = "integer")
    int integer() default 16;

    @OverridesAttribute(constraint = Digits.class, name = "fraction")
    int fraction() default 4;

    @OverridesAttribute(constraint = DecimalMin.class, name = "value")
    String min() default "0.00";

    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        AbsAmount[] value();
    }
}
