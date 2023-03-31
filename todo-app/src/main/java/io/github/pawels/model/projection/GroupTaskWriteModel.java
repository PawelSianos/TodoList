  package io.github.pawels.model.projection;

  import io.github.pawels.model.Task;
  import io.github.pawels.model.TaskGroup;

  import java.time.LocalDateTime;

  public class GroupTaskWriteModel {

      private String description;
      private LocalDateTime deadline;

      public String getDescription() {
          return description;
      }

      public void setDescription(String description) {
          this.description = description;
      }

      public LocalDateTime getDeadline() {
          return deadline;
      }

      public void setDeadline(LocalDateTime deadline) {
          this.deadline = deadline;
      }

      public Task toTask(TaskGroup result){
            return new Task(description, deadline, result);
      }
  }
