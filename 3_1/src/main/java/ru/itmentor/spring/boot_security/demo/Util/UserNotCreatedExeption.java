package ru.itmentor.spring.boot_security.demo.Util;

public class UserNotCreatedExeption extends RuntimeException{
    public UserNotCreatedExeption (String msg){
        super(msg);
    }
}
