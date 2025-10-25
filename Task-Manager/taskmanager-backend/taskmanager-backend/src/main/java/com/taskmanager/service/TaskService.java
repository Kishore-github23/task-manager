package com.taskmanager.service;

import com.taskmanager.model.Task;
import com.taskmanager.model.Task.TaskStatus;
import com.taskmanager.model.Task.TaskPriority;
import com.taskmanager.model.User;
import com.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional

public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    // Get all tasks for a user
    public List<Task> getAllTasks(User user) {
        return taskRepository.findByUserAndDeletedAtIsNullOrderByCreatedAtDesc(user);
    }
    
    // Get all tasks with pagination
    public Page<Task> getAllTasksPaginated(User user, Pageable pageable) {
        return taskRepository.findByUserAndDeletedAtIsNull(user, pageable);
    }
    
    // Get task by ID
    public Optional<Task> getTaskById(Long id, User user) {
        return taskRepository.findByIdAndUser(id, user);
    }
    
    // Get tasks by status
    public List<Task> getTasksByStatus(User user, TaskStatus status) {
        return taskRepository.findByUserAndStatusAndDeletedAtIsNull(user, status);
    }
    
    // Get tasks by priority
    public List<Task> getTasksByPriority(User user, TaskPriority priority) {
        return taskRepository.findByUserAndPriorityAndDeletedAtIsNull(user, priority);
    }
    
    // Search tasks
    public List<Task> searchTasks(User user, String keyword) {
        return taskRepository.findByUserAndTitleContainingIgnoreCaseAndDeletedAtIsNull(user, keyword);
    }
    
    // Advanced filtering
    public Page<Task> filterTasks(User user, TaskStatus status, TaskPriority priority, 
                                  Boolean archived, String keyword, Pageable pageable) {
        return taskRepository.findByFilters(user, status, priority, archived, keyword, pageable);
    }
    
    // Create task
    public Task createTask(Task task, User user) {
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.TODO);
        }
        if (task.getPriority() == null) {
            task.setPriority(TaskPriority.MEDIUM);
        }
        task.setUser(user);
        return taskRepository.save(task);
    }
    
    // Update task
    public Task updateTask(Long id, Task taskDetails, User user) {
        Task task = taskRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        task.setPriority(taskDetails.getPriority());
        task.setDueDate(taskDetails.getDueDate());
        
        return taskRepository.save(task);
    }
    
    // Update task status
    public Task updateTaskStatus(Long id, TaskStatus status, User user) {
        Task task = taskRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        task.setStatus(status);
        return taskRepository.save(task);
    }
    
    // Archive task
    public Task archiveTask(Long id, User user) {
        Task task = taskRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        task.setArchived(true);
        return taskRepository.save(task);
    }
    
    // Unarchive task
    public Task unarchiveTask(Long id, User user) {
        Task task = taskRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        task.setArchived(false);
        return taskRepository.save(task);
    }
    
    // Get archived tasks
    public List<Task> getArchivedTasks(User user) {
        return taskRepository.findByUserAndArchivedAndDeletedAtIsNull(user, true);
    }
    
    // Soft delete
    public void deleteTask(Long id, User user) {
        Task task = taskRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        task.setDeletedAt(LocalDateTime.now());
        taskRepository.save(task);
    }
    
    // Get deleted tasks
    public List<Task> getDeletedTasks(User user) {
        return taskRepository.findByUserAndDeletedAtIsNotNull(user);
    }
    
    // Restore task
    public Task restoreTask(Long id, User user) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        
        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        
        if (task.getDeletedAt() == null) {
            throw new RuntimeException("Task is not deleted");
        }
        
        task.setDeletedAt(null);
        return taskRepository.save(task);
    }
    
    // Permanent delete
    public void permanentlyDeleteTask(Long id, User user) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        
        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        
        taskRepository.delete(task);
    }
    
    // Delete all tasks
    public void deleteAllTasks(User user) {
        List<Task> tasks = taskRepository.findByUserAndDeletedAtIsNull(user);
        LocalDateTime now = LocalDateTime.now();
        tasks.forEach(task -> task.setDeletedAt(now));
        taskRepository.saveAll(tasks);
    }
}