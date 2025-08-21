package tech.kvothe.proteus.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class NotAuthorizedImageTransformationException extends ProteusException {

    private String details;

    public NotAuthorizedImageTransformationException (String details) {
        this.details = details;
    }

    @Override
    public ProblemDetail toProblemDetail() {
        var problemDetails = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetails.setTitle("Unauthorized transformation");
        problemDetails.setDetail(details);

        return problemDetails;
    }

}
