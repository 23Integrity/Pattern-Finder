package com.allegro.pattern.finder.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown on empty file with NO CONTENT response
 */
@ResponseStatus(code = HttpStatus.NO_CONTENT, reason = "File is empty or damaged.")
public class FileEmptyException extends RuntimeException { }
