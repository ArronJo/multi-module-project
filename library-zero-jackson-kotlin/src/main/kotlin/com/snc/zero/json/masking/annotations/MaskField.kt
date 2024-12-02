package com.snc.zero.json.masking.annotations

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.CLASS)
@JacksonAnnotationsInside
annotation class MaskField
