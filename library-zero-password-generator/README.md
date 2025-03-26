# 비밀번호 생성기, 검증기

- 소스 출처 : https://www.passay.org/
- 비밀번호 강도 체크 : https://github.com/nulab/zxcvbn4j

## Spring 에서 비밀번호 검증 하는 참고 소스

```kotlin
    // ConstraintValidator : Spring 클래스
    class PasswordConstraintsValidator : ConstraintValidator<Password?, String?> {
        override fun isValid(password: String?, context: ConstraintValidatorContext): Boolean {
            val passwordValidator = PasswordValidator(
                listOf( // 비밀번호 길이가 8~20 사이여야 한다.
                    LengthRule(8, 20), // 적어도 하나의 대문자가 있어야 한다.
                    CharacterRule(EnglishCharacterData.UpperCase, 1), // 적어도 하나의 소문자가 있어야 한다.
                    CharacterRule(EnglishCharacterData.LowerCase, 1), // 적어도 하나의 숫자가 있어야 한다.
                    CharacterRule(EnglishCharacterData.Digit, 1),  // 적어도 하나의 특수문자가 있어야 한다.
                    CharacterRule(EnglishCharacterData.Special, 1), // 공백문자는 허용하지 않는다.
                    WhitespaceRule()
                )
            )
            val result = passwordValidator.validate(PasswordData(password))
            if (result.isValid) {
                return true
            }

            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(
                java.lang.String.join(
                    ",",
                    passwordValidator.getMessages(result)
                )
            )
                .addConstraintViolation()
            return false
        }
    }
```
