package com.taskmanager.repository;

import com.taskmanager.model.Task;
import com.taskmanager.model.Task.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // Find tasks by status
    List<Task> findByStatus(TaskStatus status);
    
    // Search tasks by title (case-insensitive)
    List<Task> findByTitleContainingIgnoreCase(String title);
    
    // Get all tasks ordered by creation date (newest first)
    List<Task> findAllByOrderByCreatedAtDesc();
    
    // Get tasks by status ordered by due date
    List<Task> findByStatusOrderByDueDateAsc(TaskStatus status);
    
    // Find tasks by priority
    List<Task> findByPriority(Task.TaskPriority priority);
}
