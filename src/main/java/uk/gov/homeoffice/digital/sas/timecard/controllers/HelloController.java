package uk.gov.homeoffice.digital.sas.timecard.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class HelloController {

  @GetMapping
  public ResponseEntity<String> hello() {
    return ResponseEntity.status(HttpStatus.OK).body("Hello World eahw 2492 pr");
  }
}