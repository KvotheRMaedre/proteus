package tech.kvothe.proteus.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class EmailAlreadyLinkedException extends  ProteusException {

    @Override
    public ProblemDetail toProblemDetail() {
        var problemDetails = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problemDetails.setTitle("E-mail unavailable");
        problemDetails.setDetail("E-mail already linked to an account.");

        return problemDetails;
    }
}
