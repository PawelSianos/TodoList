package io.github.pawels.service;

import io.github.pawels.model.TaskGroup;
import io.github.pawels.model.TaskGroupRepository;
import io.github.pawels.model.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.catchThrowable;
class TaskGroupServiceTest {

    @Test
    @DisplayName("Powinien wyskoczyć przy niewykonanym zadaniu")
    void toggleGroup_undoneTasks_Throws_IllegalStateException() {
        // given
        TaskRepository mockTaskRepository = taskRepositoryReturning(true);
        // system under tests
        var toTest = new TaskGroupService(null, mockTaskRepository);
        // when
        var exception = catchThrowable(() -> toTest.toggleGroup(1));
        // then
        assertThat(exception)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Hola hola!");
    }

    @Test
    @DisplayName("Powinien wyskoczyć przy braku grupy")
    void toggleGroup_groupNotExist_Throws_IllegalArgumentException() {
        // given
        TaskRepository mockTaskRepository = taskRepositoryReturning(false);
        // and
        var mockRepository = mock(TaskGroupRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        // system under tests
        var toTest = new TaskGroupService(mockRepository, mockTaskRepository);
        // when
        var exception = catchThrowable(() -> toTest.toggleGroup(1));
        // then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nie ma takiej grupy");
    }

    @Test
    @DisplayName("Powinien zamknąć grupę")
    void toggleGroup_worksFine() {
        // given
        TaskRepository mockTaskRepository = taskRepositoryReturning(false);
        // and
        var group = new TaskGroup();
        var beforeToggle = group.isDone();
        // and
        var mockRepository = mock(TaskGroupRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.of(group));
        // system under tests
        var toTest = new TaskGroupService(mockRepository, mockTaskRepository);
        // when
        toTest.toggleGroup(0);
        // then
        assertThat(group.isDone()).isEqualTo(!beforeToggle);
    }

    private static TaskRepository taskRepositoryReturning(final boolean result) {
        TaskRepository mockTaskRepository = mock(TaskRepository.class);
        when(mockTaskRepository.existsByDoneIsFalseAndGroup_Id(anyInt())).thenReturn(result);
        return mockTaskRepository;
    }
}