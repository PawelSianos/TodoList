package io.github.pawels.model;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;


@Entity
@Table(name = "tasks")
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank(message = "Wymagane podanie opisu")
    private String description;
    private boolean done;
    private LocalDateTime deadline;
    @Embedded
    private Audit audit = new Audit();
    @ManyToOne
    @JoinColumn(name = "task_group_id")
    private TaskGroup group;

    public Task(String description, LocalDateTime deadline) {
        this(description, deadline, null);
    }
    public Task(String description, LocalDateTime deadline, TaskGroup group){
        this.description = description;
        this.deadline = deadline;
        if (group != null){
            this.group = group;
        }

    }
    public int getId() {
        return id;
    }
    void setId(final int id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public TaskGroup getGroup() {
        return group;
    }

    public void setGroup(TaskGroup group) {
        this.group = group;
    }

    public void updateFrom(Task source) {
        description = source.description;
        done = source.done;
        deadline = source.deadline;
        group = source.group;
    }


}
