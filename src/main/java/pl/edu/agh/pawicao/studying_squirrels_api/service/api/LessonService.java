package pl.edu.agh.pawicao.studying_squirrels_api.service.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.*;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Attachment;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Homework;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Lesson;
import pl.edu.agh.pawicao.studying_squirrels_api.repository.HomeworkRepository;
import pl.edu.agh.pawicao.studying_squirrels_api.repository.LessonRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class LessonService {
  @Autowired
  LessonRepository lessonRepository;
  @Autowired
  HomeworkRepository homeworkRepository;

  public Lesson requestLesson(LessonRequest lessonRequest) {
    return lessonRepository.requestLesson(
      lessonRequest.getOfferId(),
      lessonRequest.getStudentId(),
      lessonRequest.getDateInMillis(),
      lessonRequest.getStudentDescription()
    );
  }

  public Lesson confirmLesson(Long lessonId) {
    Lesson lesson = lessonRepository.findById(lessonId).get();
    lesson.setConfirmed(true);
    return lessonRepository.save(lesson);
  }

  public Lesson cancelLesson(Long lessonId) {
    Lesson lesson = lessonRepository.findById(lessonId).get();
    lesson.setCanceled(true);
    return lessonRepository.save(lesson);
  }

  public Lesson getLesson(Long lessonId) {
    return lessonRepository.findById(lessonId).get();
  }

  public Lesson changeTutorDescription(LessonTutorDescriptionRequest lessonTutorDescriptionRequest) {
    Lesson lesson = lessonRepository.findById(lessonTutorDescriptionRequest.getLessonId()).get();
    lesson.setTutorDescription(lessonTutorDescriptionRequest.getTutorDescription());
    return lessonRepository.save(lesson);
  }

  public List<Lesson> getLessonsForStudent(Long personId, Long dateInMillis, boolean past) {
    return past ? lessonRepository.findAllPastLessonsForStudent(personId, dateInMillis) :
      lessonRepository.findAllFutureLessonsForStudent(personId, dateInMillis);
  }

  public List<Lesson> getLessonsForTutor(Long personId, Long dateInMillis, boolean past) {
    return past ? lessonRepository.findAllPastLessonsForTutor(personId, dateInMillis) :
      lessonRepository.findAllFutureLessonsForTutor(personId, dateInMillis);
  }

  public Homework setHomework(HomeworkRequest homeworkRequest) {
    return homeworkRepository.setHomework(
      homeworkRequest.getLessonId(), homeworkRequest.getDeadline(), homeworkRequest.getTextContent()
    );
  }

  public Homework editHomework(HomeworkEditRequest homeworkRequest) {
    return homeworkRepository.editHomework(
      homeworkRequest.getHomeworkId(), homeworkRequest.getDeadline(), homeworkRequest.getTextContent()
    );
  }

  public Long deleteHomework(Long homeworkId) {
    homeworkRepository.deleteById(homeworkId);
    return homeworkId;
  }

  public Lesson setRating(RatingRequest ratingRequest) {
    if (ratingRequest.isAltering()) {
      return ratingRequest.isStudent() ?
        lessonRepository.alterTutorRating(
          ratingRequest.getLessonId(), ratingRequest.getRating(), ratingRequest.getRatingDescription()
        ) :
        lessonRepository.alterStudentRating(
          ratingRequest.getLessonId(), ratingRequest.getRating(), ratingRequest.getRatingDescription()
        );
    }
    return ratingRequest.isStudent() ?
      lessonRepository.setTutorRating(
        ratingRequest.getLessonId(), ratingRequest.getRating(), ratingRequest.getRatingDescription()
      ) :
      lessonRepository.setStudentRating(
        ratingRequest.getLessonId(), ratingRequest.getRating(), ratingRequest.getRatingDescription()
      );
  }

  public Homework addHomework(Long id, Long dateInMillis, String solution, List<String> attachmentPaths) {
    Homework homework = homeworkRepository.addHomework(id, dateInMillis, solution);
    List<Attachment> attachments = new ArrayList<>();
    for(String path : attachmentPaths) {
      Attachment attachment = new Attachment();
      attachment.setFilePath(path);
      attachment.setHomework(homework);
      attachments.add(attachment);
    }
    homework.setAttachments(attachments);
    return homeworkRepository.save(homework);
  }

  public Homework editHomeworkSolution(Long id, String solution, Long dateInMillis) {
    return homeworkRepository.editHomeworkSolution(id, solution, dateInMillis);
  }

  public Long deleteAttachment(String id) {
    homeworkRepository.deleteAttachment(Long.parseLong(id));
    return Long.parseLong(id);
  }

  public Homework addAtachments(Long id, List<String> attachmentPaths) {
    Homework homework = homeworkRepository.findById(id).get();
    List<Attachment> attachments = new ArrayList<>();
    for(String path : attachmentPaths) {
      Attachment attachment = new Attachment();
      attachment.setFilePath(path);
      attachment.setHomework(homework);
      attachments.add(attachment);
    }
    homework.setAttachments(attachments);
    return homeworkRepository.save(homework);
  }
}
