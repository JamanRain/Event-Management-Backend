package com.campus.EventManagement.Exceptions;

public class BadRequestException extends RuntimeException
{
    public BadRequestException(String message)
    {
        super(message);
    }
}
