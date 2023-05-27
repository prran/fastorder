package com.kindlesstory.www.data.rest;

public abstract class Rest
{
    public static final String OK = "OK",
    		SERVICE_UNVAILABLE = "Service Unvailable",
    		UNPROCESSABLE_ENTITY = "Unprocessable Entity",
    		IM_TEAPOT = "Request reject",
    		BAD_REQUEST = "Bad request",
    		UNAVAILABLE_FOR_LEGAL_REASONS = "Unavailable For Legal Reasons";
    public static final int STATE_OK = 200,
    		STATE_CREATED = 201,
    		STATE_SERVICE_UNVAILABLE = 503,
    		STATE_IM_TEAPOT = 418,
    		STATE_BAD_REQUEST = 400,
    		STATE_UNPROCESSABLE_ENTITY = 422,
    		STATE_UNAVAILABLE_FOR_LEGAL_REASONS = 451;
}