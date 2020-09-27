package pl.edu.agh.pawicao.studying_squirrels_api.util.exception;

public class ConflictException extends RuntimeException {
  public ConflictException(String subject) {
    super(subject + " already exists.");
  }
}
