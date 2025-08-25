package tech.kvothe.proteus.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class ImageFormatNotAvailableException extends  ProteusException {

    private String details;

    public ImageFormatNotAvailableException(String details) {
        this.details = details;
    }

    @Override
    public ProblemDetail toProblemDetail() {
        var problemDetails = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problemDetails.setTitle("Format not available");
        problemDetails.setDetail(details);

        return problemDetails;
    }
}
