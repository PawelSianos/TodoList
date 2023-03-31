package io.github.pawels.service;

import io.github.pawels.model.TaskGroup;
import io.github.pawels.model.TaskGroupRepository;
import io.github.pawels.model.TaskRepository;
import io.github.pawels.model.projection.GroupReadModel;
import io.github.pawels.model.projection.GroupWriteModel;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequestScope
public class TaskGroupService {
    private TaskGroupRepository repository;
    private TaskRepository taskRepository;


    public TaskGroupService(TaskGroupRepository repository, TaskRepository taskRepository) {
        this.repository = repository;
        this.taskRepository = taskRepository;
    }

    public GroupReadModel createGroup(GroupWriteModel source){
        TaskGroup result = repository.save(source.toGroup());
        return new GroupReadModel(result);
    }

    public List<GroupReadModel> readAll() {
        return repository.findAll().stream()
                .map(GroupReadModel::new)
                .collect(Collectors.toList());
    }

    public void toggleGroup(int groupId) {
       if(taskRepository.existsByDoneIsFalseAndGroup_Id(groupId)){
            throw new IllegalStateException("Hola hola! nie wszystkie taski zostały wykonane");
        }
       TaskGroup result = repository.findById(groupId)
               .orElseThrow(() -> new IllegalArgumentException("nie ma takiej grupy"));
       result.setDone(!result.isDone());
       repository.save(result);
    }
}
