package com.mts.lts.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.sql.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    private Set<User> users;


    @Column
    private String title;

    @Column
    @Lob
    private String description;

    @Column
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @Column
    private Date updatedAt;

    @NotBlank(message = "Course author has to be filled")
    private String author; //заменить на id

    @ManyToOne
    @JoinColumn(name = "updated_by_id")
    private User updatedBy;

    @Column
    private Date deletedAt;

    @ManyToOne
    @JoinColumn(name = "deleted_by_id")
    private User deletedBy;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    List<Module> modules;

    @OneToOne
    private Image coverImage;

    @ManyToMany(mappedBy = "courses")
    private Set<Category> categories;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Course course = (Course) o;
        return Objects.equals(id, course.id);
    }



    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
