package io.github.blog.validation;

import javax.validation.*;

import org.springframework.web.multipart.MultipartFile;

public class NotEmptyValidator implements ConstraintValidator<NotEmpty, MultipartFile> {

	@Override
	public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        return value != null && !value.isEmpty();
	}
}
