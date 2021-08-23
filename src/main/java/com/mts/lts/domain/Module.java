package com.mts.lts.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.sql.Date;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "modules", indexes = @Index(columnList = "id"))
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Course course;

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

    @ManyToOne
    @JoinColumn(name = "updated_by_id")
    private User updatedBy;

    @Column
    private Date deletedAt;

    @ManyToOne
    @JoinColumn(name = "deleted_by_id")
    private User deletedBy;


    private String author; //заменить на id

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL)
    List<Topic> topics;

    @OneToOne
    private Image coverImage;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Module module = (Module) o;
        return Objects.equals(id, module.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
// COURSE!!!!
//    @Id
//    @Column(name = "course_id")
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column
//    private String title;
//
//    @Column
//    @Lob
//    private String description;
//
//    @Column
//    private Date createdAt;
//
//    @ManyToOne
//    @JoinColumn(name = "created_by_id")
//    private User createdBy;
//
//    @Column
//    private Date updatedAt;
//
//    @ManyToOne
//    @JoinColumn(name = "updated_by_id")
//    private User updatedBy;
//
//    @Column
//    private Date deletedAt;
//
//    @ManyToOne
//    @JoinColumn(name = "deleted_by_id")
//    private User deletedBy;
//
//    @Column
//    private Double averageRating;
//
//    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
//    private List<ModuleOld> moduleOlds;
//
//    @ManyToMany
//    private Set<User> users;
//
//    @OneToOne
//    private Image coverImage;
//
//    @ManyToMany(mappedBy = "courses")
//    private Set<Category> categories;
//
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (o == null || getClass() != o.getClass()) {
//            return false;
//        }
//        Module module = (Module) o;
//        return Objects.equals(id, module.id);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id);
//    }
}
