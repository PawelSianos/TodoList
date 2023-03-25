package io.github.pawels.service;

import io.github.pawels.TaskConfigurationProperties;
import io.github.pawels.model.ProjectRepository;
import io.github.pawels.model.TaskGroup;
import io.github.pawels.model.TaskGroupRepository;
import io.github.pawels.model.projection.GroupReadModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.constraints.NotBlank;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.OPTIONAL;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectServiceTest {

    @Test
    @DisplayName("Powinien rzucić illegalStateException")
    void createGroup_noMultipleGroupsConfig_And_undoneGroups_throwsIllegalStateException() {
        // given
        var mockGroupRepository = groupRepositoryReturning(true);
        // system under test
        TaskConfigurationProperties mockConfig = configurationReturning(false);
        var toTest = new ProjectService(null, mockGroupRepository, mockConfig);
        // when
        var exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 0));
        // then
        assertThat(exception)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(" jedna niezrobiona grupa");
    }
    @Test
    @DisplayName("Powinien wyrzucić illegalArgumentException gdy konfiguracja jest ok ale nie ma projektu dla danego id")
    void createGroup_configurationOk_And_noProjects_throwsIllegalArgumentException() {
        // given
        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        TaskConfigurationProperties mockConfig = configurationReturning(true);
        // system under test
        var toTest = new ProjectService(mockRepository, null, mockConfig);
        // when
        var exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 0));
        // then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("podanym id nie istnieje");
    }

    @Test
    @DisplayName("Powinien wyrzucić illegalArgumentException gdy konfiguracja jest ok ale nie ma grupy ani projektu dla danego id")
    void createGroup_noMultipleGroupsConfig_and_noUndoneGroupExist_noProjects_throwsIllegalArgumentException() {
        // given
        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        // and
        TaskGroupRepository taskGroupRepository = groupRepositoryReturning(false);
        // and
        TaskConfigurationProperties mockConfig = configurationReturning(true);
        // system under test
        var toTest = new ProjectService(mockRepository, taskGroupRepository, mockConfig);
        // when
        var exception = catchThrowable(() -> toTest.createGroup(LocalDateTime.now(), 0));
        // then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("podanym id nie istnieje");
    }
    @Test
    @DisplayName("Powinien stworzyć nową grupę z projektu")
    void createGroup_configurationOk_existingProject_createsAndSavesGroup() {
        // given
        var today = LocalDate.now().atStartOfDay();
        TaskConfigurationProperties mockConfig = configurationReturning(true);
        // and
        TaskGroupRepository inMemoryGroupRepo = inMemoryGroupRepository();
        // and
        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        var toTest = new ProjectService(mockRepository, inMemoryGroupRepo, mockConfig);
        // when
        GroupReadModel result = toTest.createGroup(today, 1);


    }



    private static TaskGroupRepository groupRepositoryReturning(boolean param) {
        var mockGroupRepository = mock(TaskGroupRepository.class);
        when(mockGroupRepository.existsByDoneIsFalseAndProject_Id(anyInt())).thenReturn(param);
        return mockGroupRepository;
    }

    private static TaskConfigurationProperties configurationReturning(final boolean result) {
        var mockTemplate = mock(TaskConfigurationProperties.Template.class);
        when(mockTemplate.isAllowMultipleTasks()).thenReturn(result);
        var mockConfig = mock(TaskConfigurationProperties.class);
        when(mockConfig.getTemplate()).thenReturn(mockTemplate);
        return mockConfig;
    }
    private TaskGroupRepository inMemoryGroupRepository() {
        return new TaskGroupRepository() {
            private int index = 0;
            private Map<Integer, TaskGroup> map = new HashMap<>();
            @Override
            public List<TaskGroup> findAll() {
                return new ArrayList<>(map.values());
            }

            @Override
            public Optional<TaskGroup> findById(Integer id) {
                return Optional.ofNullable(map.get(id));
            }

            @Override
            public TaskGroup save(TaskGroup entity) {
                if(entity.getId() != 0){
                    map.put(entity.getId(), entity);
                }else {
                    map.put(++index, entity);
                }
                return entity;
            }

            @Override
            public boolean existsByDoneIsFalseAndProject_Id(Integer projectId) {
                return map.values().stream()
                        .filter(group -> !group.isDone())
                        .allMatch(group -> group.getProject() != null && group.getProject().getId() == projectId);

            }
        };
    }
}