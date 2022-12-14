package pl.edu.agh.pawicao.studying_squirrels_api.controller.api;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.agh.pawicao.studying_squirrels_api.config.storage.StorageClient;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.*;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Attachment;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Homework;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Lesson;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Lesson.LessonStudentDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Lesson.LessonTutorDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.service.api.LessonService;
import pl.edu.agh.pawicao.studying_squirrels_api.service.api.StorageService;
import pl.edu.agh.pawicao.studying_squirrels_api.util.FileUtils;
import pl.edu.agh.pawicao.studying_squirrels_api.util.Mapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LessonController {

  StorageClient storageClient = StorageClient.getInstance();

  @Autowired
  LessonService lessonService;

  @Autowired
  private StorageService storageService;

  @PostMapping("/lesson")
  ResponseEntity<Lesson> requestLesson(
    @RequestBody LessonRequest lessonRequest
    ) {
    Lesson lesson = lessonService.requestLesson(lessonRequest);
    lesson.getTakenLesson().getStudent().setPhone("");
    lesson.getTakenLesson().getStudent().setEmail("");
    lesson.setPlace(null);
    lesson.getGivenLesson().getTutor().setPhone("");
    lesson.getGivenLesson().getTutor().setEmail("");
    return ResponseEntity.ok(lesson);
  }

  @PutMapping("/lesson/confirm")
  ResponseEntity<Lesson> confirmLesson(
    @RequestBody LessonTutorDescriptionRequest lessonTutorDescriptionRequest
  ) {
    return ResponseEntity.ok(lessonService.confirmLesson(lessonTutorDescriptionRequest));
  }

  @PutMapping("/lesson/{lessonId}/cancel")
  ResponseEntity<Lesson> cancelLesson(
    @PathVariable Long lessonId
  ) {
    Lesson lesson = lessonService.cancelLesson(lessonId);
    lesson.setPlace(null);
    lesson.getTakenLesson().getStudent().setPhone("");
    lesson.getTakenLesson().getStudent().setEmail("");
    lesson.getGivenLesson().getTutor().setPhone("");
    lesson.getGivenLesson().getTutor().setEmail("");
    return ResponseEntity.ok(lesson);
  }

  @PutMapping("/lesson/description")
  ResponseEntity<Lesson> changeTutorDescription(
    @RequestBody LessonTutorDescriptionRequest lessonTutorDescriptionRequest
  ) {
    return ResponseEntity.ok(lessonService.changeTutorDescription(lessonTutorDescriptionRequest));
  }

  @GetMapping("/lesson/{lessonId}")
  ResponseEntity<Lesson> getLesson(
    @PathVariable Long lessonId
  ) {
    Lesson lesson = lessonService.getLesson(lessonId);
    if (!lesson.isConfirmed() || lesson.isCanceled()) {
      lesson.getTakenLesson().getStudent().setPhone("");
      lesson.getTakenLesson().getStudent().setEmail("");
      lesson.getGivenLesson().getTutor().setPhone("");
      lesson.getGivenLesson().getTutor().setEmail("");
      lesson.setPlace(null);
    }
    return ResponseEntity.ok(lesson);
  }

  @GetMapping("/lessons/{personId}")
  ResponseEntity<?> getLessons(
    @PathVariable Long personId,
    @RequestParam boolean student,
    @RequestParam (name = "date") Long dateInMillis,
    @RequestParam boolean past
  ) {
    if(student)
      return ResponseEntity.ok(
        Mapper.mapAll(lessonService.getLessonsForStudent(personId, dateInMillis, past), LessonTutorDTO.class)
      );
    return ResponseEntity.ok(
      Mapper.mapAll(lessonService.getLessonsForTutor(personId, dateInMillis, past), LessonStudentDTO.class)
    );
  }

  @PostMapping("/lesson/rating")
  ResponseEntity<?> setRating (
    @RequestBody RatingRequest ratingRequest
    ) {
    if(ratingRequest.isStudent())
      return ResponseEntity.ok(Mapper.map(lessonService.setRating(ratingRequest), LessonTutorDTO.class));
    return ResponseEntity.ok(Mapper.map(lessonService.setRating(ratingRequest), LessonStudentDTO.class));
  }

  @GetMapping("/lesson/homeworks/{personId}")
  ResponseEntity<List<Homework>> getHomeworks(
    @PathVariable Long personId,
    @RequestParam boolean student
  ) {
    return ResponseEntity.ok(lessonService.getHomeworks(personId, student));
  }

  @PostMapping("/lesson/homework")
  ResponseEntity<Homework> setHomework(
    @RequestBody HomeworkRequest homeworkRequest
  ) {
    return ResponseEntity.ok(lessonService.setHomework(homeworkRequest));
  }

  @PutMapping("/lesson/homework")
  ResponseEntity<Homework> editHomework(
    @RequestBody HomeworkEditRequest homeworkRequest
  ) {
    return ResponseEntity.ok(lessonService.editHomework(homeworkRequest));
  }

  @DeleteMapping("/lesson/homework/{homeworkId}")
  ResponseEntity<Long> deleteHomework(
    @PathVariable Long homeworkId
  ) {
    return ResponseEntity.ok(
      lessonService.deleteHomework(homeworkId)
    );
  }

  @PostMapping("/lesson/homework/student/one")
  public ResponseEntity<Homework> addHomeworkWithAttachment(
    @RequestParam(required = false) MultipartFile file,
    @RequestParam Long id,
    @RequestParam(name = "date") Long dateInMillis,
    @RequestParam String solution
  ) {
    List<String> paths = new ArrayList<>();
    if (file != null) {
      String name = "hmwk-" + id + "-" + file.getOriginalFilename();
      if (storageClient.uploadFile(file, name)) {
        paths.add("/attachments/" + name);
      }
    }
    return ResponseEntity.ok(
      lessonService.addHomework(id, dateInMillis, solution, paths)
    );
  }

  @PostMapping("/lesson/homework/student")
  public ResponseEntity<Homework> addHomework(
    @RequestBody HomeworkSolutionRequest homeworkSolutionRequest
  ) {
    return ResponseEntity.ok(
      lessonService.addHomework(homeworkSolutionRequest.getId(),
        homeworkSolutionRequest.getDate(), homeworkSolutionRequest.getSolution(), new ArrayList<>())
    );
  }

  @RequestMapping(
    value = "/attachments/{filename:.+}",
    method = RequestMethod.GET,
    produces = MediaType.IMAGE_JPEG_VALUE
  )
  @ResponseBody
  public ResponseEntity<byte[]> downloadAttachment(@PathVariable String filename) {
    ByteArrayOutputStream stream = storageClient.getFile(filename);
    return ResponseEntity.ok(stream.toByteArray());
  }

  @PostMapping("/lesson/homework/student/attachment")
  public ResponseEntity<Homework> addAttachment(
    @RequestParam MultipartFile file,
    @RequestParam Long id
  ) {
    String name = "hmwk-" + id + "-" + file.getOriginalFilename();
    if (storageClient.uploadFile(file, name)) {
      List<String> paths = new ArrayList<>();
      paths.add("/attachments/" + name);
      return ResponseEntity.ok(lessonService.addAtachments(id, paths));
    } else {
      return ResponseEntity.status(500).body(null);
    }
  }

  @PostMapping("lesson/homework/student/attachment/multiple")
  public ResponseEntity<Homework> addMultipleAttachments(
    @RequestParam MultipartFile[] files,
    @RequestParam Long id
  ) {
    List<String> paths = Arrays.stream(files)
      .map(file -> {
        String name = "hmwk-" + id + "-" + file.getOriginalFilename();
        if (storageClient.uploadFile(file, name)) {
          return "/attachments/" + name;
        } else {
          return "";
        }
      })
      .collect(Collectors.toList());
    return ResponseEntity.ok(
      lessonService.addAtachments(id, paths)
    );
  }

  @PostMapping("/lesson/homework/student/multiple")
  public ResponseEntity<Homework> addHomeworkWithMultipleAttachments(
    @RequestParam MultipartFile[] files,
    @RequestParam Long id,
    @RequestParam(name = "date") Long dateInMillis,
    @RequestParam String solution
  ) {
    List<String> paths = Arrays.stream(files)
      .map(file -> {
        String name = "hmwk-" + id + "-" + file.getOriginalFilename();
        if (storageClient.uploadFile(file, name)) {
          return "/attachments/" + name;
        } else {
          return "";
        }
      })
      .collect(Collectors.toList());
    return ResponseEntity.ok(
      lessonService.addHomework(id, dateInMillis, solution, paths)
    );
  }

  @PutMapping("lesson/homework/student")
  public ResponseEntity<Homework> editHomeworkSolution(
    @RequestBody HomeworkSolutionRequest homeworkSolutionRequest
  ) {
    return ResponseEntity.ok(lessonService.editHomeworkSolution(homeworkSolutionRequest.getId(),
      homeworkSolutionRequest.getSolution(), homeworkSolutionRequest.getDate()));
  }

  @DeleteMapping("/lesson/homework/attachment/{id}")
  ResponseEntity<Long> deleteAttachment(
    @PathVariable Long id
  ) throws IOException {
    Attachment attachment = lessonService.getAttachment(id);
    try {
      storageClient.deleteFile(attachment.getFilePath().substring(13));
    } catch (Exception e) {
      System.out.println("File wasn't there.");
    }

    return ResponseEntity.ok(
      lessonService.deleteAttachment(id)
    );
  }
}
