package com.mrshish.messenger.json;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class JsonValidator {
    private static final Validator validator;
    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public static  <T> T validate(final T entity){
        Set<ConstraintViolation<T>> violations = validator.validate(entity);
        if (!violations.isEmpty()) {
            Set<ViolationMessage> errors = violations.stream().map(v ->
                new ViolationMessage(
                    cleanPropertyName(v.getPropertyPath()),
                    v.getMessage()
                )
            ).collect(Collectors.toSet());

            throw new JsonValidatinException(errors);
        }
        return entity;
    }

    /**
     * This method extracts the name of PROPERTY element of the path
     * @param path
     * @return
     */
    private static String cleanPropertyName(Path path) {
        return StreamSupport
            .stream(path.spliterator(), false)
            .filter(node -> ElementKind.PROPERTY == node.getKind())
            .map(Path.Node::toString)
            .collect(Collectors.joining("."));
    }
}
