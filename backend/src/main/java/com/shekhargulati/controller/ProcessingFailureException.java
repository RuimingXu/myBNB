package com.shekhargulati.controller;

public class ProcessingFailureException extends Exception {

    public String getMessage() {
        return "Error occur during the process of requests starting with the tag \"/control\".";
    }

}
