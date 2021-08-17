package com.mts.lts.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "avatar_images")
public class AvatarImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String contentType;

    @Column(unique = true)
    private String filename;

    @OneToOne
    private User user;

    @OneToOne
    private Course course;

    public AvatarImage() {
    }

    public AvatarImage(
            String contentType,
            String filename,
            Course course
    ) {
        this(contentType, filename);
        this.course = course;
    }

    public AvatarImage(
            String contentType,
            String filename,
            User user
    ) {
        this(contentType, filename);
        this.user = user;
    }

    private AvatarImage(
            String contentType,
            String filename
    ) {
        this.contentType = contentType;
        this.filename = filename;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AvatarImage that = (AvatarImage) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
