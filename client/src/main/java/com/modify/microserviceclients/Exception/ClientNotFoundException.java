package com.modify.microserviceclients.Exception;

public class ClientNotFoundException extends  RuntimeException{

    public ClientNotFoundException(String message){
        super(message);

    }
    public ClientNotFoundException(String message , Throwable cause){
        super(message,cause);
    }

}
