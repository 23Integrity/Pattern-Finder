package com.allegro.pattern.finder.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NO_CONTENT, reason = "File is empty or damaged.")
public class FileEmptyException extends RuntimeException { }
