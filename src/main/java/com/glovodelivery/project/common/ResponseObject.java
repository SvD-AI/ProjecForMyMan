package com.glovodelivery.project.common;

import com.glovodelivery.project.enums.ResponseStatus;

public record ResponseObject<T> (ResponseStatus status, String message, T data)
{

}
