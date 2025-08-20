package tech.kvothe.proteus.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class ProteusException extends RuntimeException{

    public ProblemDetail toProblemDetail() {
        var problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Proteus internal server error");

        return problemDetail;
    }
}
