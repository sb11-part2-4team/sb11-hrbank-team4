package com.sb11.hr_bank.domain.file.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "files")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String name;

  @Column(nullable = false, length = 30)
  private String contentType;

  @Column(nullable = false)
  private Long size;

  @Column(nullable = false, length = 500)
  private String savedPath;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  public FileEntity(String name, String contentType, Long size, String savedPath) {
    this.name = name;
    this.contentType = contentType;
    this.size = size;
    this.savedPath = savedPath;
    this.createdAt = Instant.now();
  }
}
