package com.allegro.pattern.finder.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown if provided image has no pattern - returns NO CONTENT response
 */
@ResponseStatus(code = HttpStatus.NO_CONTENT, reason = "Image doesn't provide required pattern.")
public class NoPatternException extends RuntimeException { }
