    package com.polaris.police.dogsapi.model.request.validator;

    import jakarta.validation.ConstraintValidator;
    import jakarta.validation.ConstraintValidatorContext;
    import org.springframework.context.MessageSource;
    import org.springframework.context.i18n.LocaleContextHolder;
    import java.util.Arrays;

    public class EnumValueValidator implements ConstraintValidator<ValidEnum, Enum<?>> {

        private final MessageSource messageSource;
        private Enum<?>[] allowedValues;
        private String fieldName;

        public EnumValueValidator(MessageSource messageSource) {
            this.messageSource = messageSource;
        }

        @Override
        public void initialize(ValidEnum constraintAnnotation) {
            this.allowedValues = constraintAnnotation.enumClass().getEnumConstants();
            this.fieldName = constraintAnnotation.fieldName();
        }

        @Override
        public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
            String invalidValue = (value == null) ? "null" : value.toString();
            boolean valid = value != null && Arrays.asList(allowedValues).contains(value);

            if (!valid) {
                String allowed = Arrays.stream(allowedValues)
                        .map(Enum::name)
                        .reduce((s1, s2) -> s1 + ", " + s2)
                        .orElse("");

                String message = messageSource.getMessage(
                        "error.invalid.enum", new Object[]{invalidValue, fieldName, allowed}, LocaleContextHolder.getLocale());

                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                        .addConstraintViolation();
            }

            return valid;
        }
    }
