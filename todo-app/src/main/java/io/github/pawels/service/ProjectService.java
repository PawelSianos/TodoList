package io.github.pawels.service;

import io.github.pawels.TaskConfigurationProperties;
import io.github.pawels.model.*;
import io.github.pawels.model.projection.GroupReadModel;
import io.github.pawels.model.projection.GroupTaskWriteModel;
import io.github.pawels.model.projection.GroupWriteModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    private ProjectRepository repository;
    private TaskGroupRepository taskGroupRepository;
    private TaskConfigurationProperties configuration;
    private TaskGroupService service;

    public ProjectService(final ProjectRepository repository,final TaskGroupRepository taskGroupRepository,final TaskConfigurationProperties configuration, final TaskGroupService service) {
        this.repository = repository;
        this.taskGroupRepository = taskGroupRepository;
        this.configuration = configuration;
        this.service = service;
    }

    public List<Project> readAll(){
        return repository.findAll();
    }

    public Project save(Project toSave){
        return repository.save(toSave);
    }

    public GroupReadModel createGroup(LocalDateTime deadline, int projectId){
        if(!configuration.getTemplate().isAllowMultipleTasks() && taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId)){
            throw new IllegalStateException("Dozwolona jest tylko jedna niezrobiona grupa w ramach projektu");
        }
        var result = repository.findById(projectId)
                .map(project -> {
                    var targetGroup = new GroupWriteModel();
                    targetGroup.setDescription(project.getDescription());
                    targetGroup.setTasks(
                            project.getSteps().stream()
                                .map(projectStep -> {
                                    var task = new GroupTaskWriteModel();
                                    task.setDescription(projectStep.getDescription());
                                    task.setDeadline(deadline.plusDays(projectStep.getDaysToDeadline()));
                                    return task;
                                })
                            .collect(Collectors.toSet())
                    );
                   return service.createGroup(targetGroup);
                }).orElseThrow(() -> new IllegalArgumentException("Projekt z podanym id nie istnieje"));
        return result;
    }
}
