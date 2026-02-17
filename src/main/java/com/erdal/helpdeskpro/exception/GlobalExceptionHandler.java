
package com.erdal.helpdeskpro.exception;

public class GlobalExceptionHandler {

    public static void handle(Exception exception) {

        if (exception instanceof ResourceNotFoundExeption) {
            System.out.println(" NOT FOUND: " + exception.getMessage());

        } else if (exception instanceof BadRequestExeption) {
            System.out.println("⚠ BAD REQUEST: " + exception.getMessage());

        } else {
            System.out.println(" UNKNOWN ERROR: " + exception.getMessage());
        }
    }
}

