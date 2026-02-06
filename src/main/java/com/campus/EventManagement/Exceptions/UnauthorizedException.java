package com.campus.EventManagement.Exceptions;

public class UnauthorizedException extends RuntimeException
{
    public UnauthorizedException(String message)
    {
        super(message);
    }
}
