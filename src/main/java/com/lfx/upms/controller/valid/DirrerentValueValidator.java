package com.lfx.upms.controller.valid;


import org.springframework.util.ReflectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2019-07-31 10:09
 */
public class DirrerentValueValidator implements ConstraintValidator<DifferentValue, Object> {

    private String fieldName;
    private String[] matchFieldNames;

    @Override
    public void initialize(DifferentValue constraintAnnotation) {
        this.fieldName = constraintAnnotation.filed();
        this.matchFieldNames = constraintAnnotation.matchFileds();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (this.fieldName == null || this.matchFieldNames == null || this.matchFieldNames.length == 0) {
            return true;
        }
        Field field = ReflectionUtils.findField(value.getClass(), fieldName);
        ReflectionUtils.makeAccessible(field);
        Object val = ReflectionUtils.getField(field, value);
        if (val == null) {
            return true;
        }
        for (String matchFieldName : matchFieldNames) {
            Field matchField = ReflectionUtils.findField(value.getClass(), fieldName);
            ReflectionUtils.makeAccessible(field);
            Object matchVal = ReflectionUtils.getField(matchField, value);

            if (val.equals(matchVal)) {
                return false;
            }
        }
        return true;
    }
}
