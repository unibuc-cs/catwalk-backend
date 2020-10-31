package com.catwalk.publicapicatwalk.exception;

import com.catwalk.publicapicatwalk.controller.web.ErrorCode;

public class NotFoundException extends GenericException {
    public NotFoundException() {
        super(ErrorCode.GENERAL_NOT_FOUND_ERROR);
    }
}
