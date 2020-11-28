package pl.edu.agh.pawicao.studying_squirrels_api.controller.api;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.agh.pawicao.studying_squirrels_api.model.api.*;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Homework;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.Lesson;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Lesson.LessonStudentDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.projection.Lesson.LessonTutorDTO;
import pl.edu.agh.pawicao.studying_squirrels_api.service.api.LessonService;
import pl.edu.agh.pawicao.studying_squirrels_api.service.api.StorageService;
import pl.edu.agh.pawicao.studying_squirrels_api.util.FileUtils;
import pl.edu.agh.pawicao.studying_squirrels_api.util.Mapper;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LessonController {
  @Autowired
  LessonService lessonService;

  @Autowired
  private StorageService storageService;

  @PostMapping("/lesson")
  ResponseEntity<Lesson> requestLesson(
    @RequestBody LessonRequest lessonRequest
    ) {
    return ResponseEntity.ok(lessonService.requestLesson(lessonRequest));
  }

  @PutMapping("/lesson/{lessonId}/confirm")
  ResponseEntity<Lesson> confirmLesson(
    @PathVariable Long lessonId
  ) {
    return ResponseEntity.ok(lessonService.confirmLesson(lessonId));
  }

  @PutMapping("/lesson/{lessonId}/cancel")
  ResponseEntity<Lesson> cancelLesson(
    @PathVariable Long lessonId
  ) {
    return ResponseEntity.ok(lessonService.cancelLesson(lessonId));
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
    return ResponseEntity.ok(lessonService.getLesson(lessonId));
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

  @PostMapping("lesson/rating")
  ResponseEntity<?> setRating (
    @RequestBody RatingRequest ratingRequest
    ) {
    if(ratingRequest.isStudent())
      return ResponseEntity.ok(Mapper.map(lessonService.setRating(ratingRequest), LessonTutorDTO.class));
    return ResponseEntity.ok(Mapper.map(lessonService.setRating(ratingRequest), LessonStudentDTO.class));
  }

  @PostMapping("lesson/homework")
  ResponseEntity<Homework> setHomework(
    @RequestBody HomeworkRequest homeworkRequest
  ) {
    return ResponseEntity.ok(lessonService.setHomework(homeworkRequest));
  }

  @PutMapping("lesson/homework")
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

  @PostMapping("/lesson/homework/student")
  public ResponseEntity<Homework> addHomework(
    @RequestParam(required = false) MultipartFile file,
    @RequestParam Long id,
    @RequestParam(name = "date") Long dateInMillis,
    @RequestParam String solution
  ) {
    MultipartFile newFile = FileUtils.getNewFile("hmwk-" + id + "-" + file.getOriginalFilename(), file);
    String name = storageService.store(newFile);
    List<String> paths = new ArrayList<>();
    paths.add("/api/attachments/" + name);
    return ResponseEntity.ok(
      lessonService.addHomework(id, dateInMillis, solution, paths)
    );
  }

  @GetMapping("/attachments/{filename:.+}")
  @ResponseBody
  public ResponseEntity<Resource> downloadAttachment(@PathVariable String filename) {
    Resource resource = storageService.loadAsResource(filename);
    return ResponseEntity.ok()
      .header(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=\"" + resource.getFilename() + "\"")
      .body(resource);
  }

  @PostMapping("lesson/homework/student/attachment")
  public ResponseEntity<Homework> addAttachment(
    @RequestParam MultipartFile file,
    @RequestParam Long id
  ) {
    MultipartFile newFile = FileUtils.getNewFile("hmwk-" + id + "-" + file.getOriginalFilename(), file);
    String name = storageService.store(newFile);
    List<String> paths = new ArrayList<>();
    paths.add("/api/attachments/" + name);
    return ResponseEntity.ok(
      lessonService.addAtachments(id, paths)
    );
  }

  @PostMapping("lesson/homework/student/attachment/multiple")
  public ResponseEntity<Homework> addMultipleAttachments(
    @RequestParam MultipartFile[] files,
    @RequestParam Long id
  ) {
    List<String> paths = Arrays.stream(files)
      .map(file -> {
        MultipartFile newFile = FileUtils.getNewFile("hmwk-" + id + "-" + file.getOriginalFilename(), file);
        String name = storageService.store(newFile);
        return "/api/attachments/" + name;
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
        MultipartFile newFile = FileUtils.getNewFile("hmwk-" + id + "-" + file.getOriginalFilename(), file);
        String name = storageService.store(newFile);
        return "/api/attachments/" + name;
      })
      .collect(Collectors.toList());
    return ResponseEntity.ok(
      lessonService.addHomework(id, dateInMillis, solution, paths)
    );
  }

  @PutMapping("lesson/homework/student")
  public ResponseEntity<Homework> editHomeworkSolution(
    @RequestParam Long id,
    @RequestParam String solution,
    @RequestParam(name = "date") Long dateInMillis
  ) {
    return ResponseEntity.ok(lessonService.editHomeworkSolution(id, solution, dateInMillis));
  }

  @DeleteMapping("/lesson/homework/attachment")
  ResponseEntity<Long> deleteAttachment(
    @RequestBody Map<String, String> attachmentName
  ) throws IOException {
    storageService.delete(attachmentName.get("name"));
    return ResponseEntity.ok(
      lessonService.deleteAttachment(attachmentName.get("id"))
    );
  }
}
