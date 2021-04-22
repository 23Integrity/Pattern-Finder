package com.allegro.pattern.finder.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * HTTP response when provided image has no required pattern
 */
@ResponseStatus(code = HttpStatus.NO_CONTENT, reason = "Image doesn't provide required pattern.")
public class NoPatternException extends RuntimeException { }
