package io.github.pawels.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.lang.reflect.Type;
import java.util.Set;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @NotBlank(message = "Wymagane podanie opisu projektu")
    private String description;
    @OneToMany(mappedBy = "project")
    private Set<TaskGroup> groups;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    private Set<ProjectSteps> steps;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<TaskGroup> getGroups() {
        return groups;
    }

    public void setGroups(Set<TaskGroup> groups) {
        this.groups = groups;
    }

    public Set<ProjectSteps> getSteps() {
        return steps;
    }

    public void setSteps(Set<ProjectSteps> steps) {
        this.steps = steps;
    }
}
