package com.kindlesstory.www.data.rest;

public abstract class Rest
{
    public static final String OK = "OK";
    public static final String SERVICE_UNVAILABLE = "Service Unvailable";
    public static final String UNPROCESSABLE_ENTITY = "Unprocessable Entity";
    public static final String IM_TEAPOT = "Request reject";
    public static final String BAD_REQUEST = "Bad request";
    public static final String UNAVAILABLE_FOR_LEGAL_REASONS = "Unavailable For Legal Reasons";
    public static final int STATE_OK = 200;
    public static final int STATE_CREATED = 201;
    public static final int STATE_SERVICE_UNVAILABLE = 503;
    public static final int STATE_IM_TEAPOT = 418;
    public static final int STATE_BAD_REQUEST = 400;
    public static final int STATE_UNPROCESSABLE_ENTITY = 422;
    public static final int STATE_UNAVAILABLE_FOR_LEGAL_REASONS = 451;
}