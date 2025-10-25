package com.taskmanager.controller;

import com.taskmanager.model.Task;
import com.taskmanager.model.Task.TaskStatus;
import com.taskmanager.model.Task.TaskPriority;
import com.taskmanager.model.User;
import com.taskmanager.security.UserDetailsImpl;
import com.taskmanager.service.TaskService;
import com.taskmanager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:4200")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private UserService userService;
    
    private User getCurrentUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
            .getContext().getAuthentication().getPrincipal();
        return userService.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    // GET: All tasks
    @GetMapping
    public ResponseEntity<?> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "false") boolean paginate) {
        
        User currentUser = getCurrentUser();
        
        if (paginate) {
            Sort sort = sortDir.equalsIgnoreCase("asc") ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Task> tasks = taskService.getAllTasksPaginated(currentUser, pageable);
            return ResponseEntity.ok(tasks);
        } else {
            List<Task> tasks = taskService.getAllTasks(currentUser);
            return ResponseEntity.ok(tasks);
        }
    }
    
    // GET: Task by ID
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        return taskService.getTaskById(id, currentUser)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    // GET: Tasks by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable TaskStatus status) {
        User currentUser = getCurrentUser();
        List<Task> tasks = taskService.getTasksByStatus(currentUser, status);
        return ResponseEntity.ok(tasks);
    }
    
    // GET: Tasks by priority
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<Task>> getTasksByPriority(@PathVariable TaskPriority priority) {
        User currentUser = getCurrentUser();
        List<Task> tasks = taskService.getTasksByPriority(currentUser, priority);
        return ResponseEntity.ok(tasks);
    }
    
    // GET: Search tasks
    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasks(@RequestParam String keyword) {
        User currentUser = getCurrentUser();
        List<Task> tasks = taskService.searchTasks(currentUser, keyword);
        return ResponseEntity.ok(tasks);
    }
    
    // GET: Filter tasks
    @GetMapping("/filter")
    public ResponseEntity<Page<Task>> filterTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) Boolean archived,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        User currentUser = getCurrentUser();
        Sort sort = sortDir.equalsIgnoreCase("asc") ? 
            Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Task> tasks = taskService.filterTasks(currentUser, status, priority, archived, keyword, pageable);
        return ResponseEntity.ok(tasks);
    }
    
    // GET: Archived tasks
    @GetMapping("/archived")
    public ResponseEntity<List<Task>> getArchivedTasks() {
        User currentUser = getCurrentUser();
        List<Task> tasks = taskService.getArchivedTasks(currentUser);
        return ResponseEntity.ok(tasks);
    }
    
    // GET: Deleted tasks
    @GetMapping("/deleted")
    public ResponseEntity<List<Task>> getDeletedTasks() {
        User currentUser = getCurrentUser();
        List<Task> tasks = taskService.getDeletedTasks(currentUser);
        return ResponseEntity.ok(tasks);
    }
    
    // POST: Create task
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        User currentUser = getCurrentUser();
        Task createdTask = taskService.createTask(task, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }
    
    // PUT: Update task
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @Valid @RequestBody Task task) {
        try {
            User currentUser = getCurrentUser();
            Task updatedTask = taskService.updateTask(id, task, currentUser);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // PATCH: Update status
    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long id, @RequestBody Map<String, String> statusMap) {
        try {
            User currentUser = getCurrentUser();
            TaskStatus status = TaskStatus.valueOf(statusMap.get("status"));
            Task updatedTask = taskService.updateTaskStatus(id, status, currentUser);
            return ResponseEntity.ok(updatedTask);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // PATCH: Archive task
    @PatchMapping("/{id}/archive")
    public ResponseEntity<Task> archiveTask(@PathVariable Long id) {
        try {
            User currentUser = getCurrentUser();
            Task archivedTask = taskService.archiveTask(id, currentUser);
            return ResponseEntity.ok(archivedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // PATCH: Unarchive task
    @PatchMapping("/{id}/unarchive")
    public ResponseEntity<Task> unarchiveTask(@PathVariable Long id) {
        try {
            User currentUser = getCurrentUser();
            Task unarchivedTask = taskService.unarchiveTask(id, currentUser);
            return ResponseEntity.ok(unarchivedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // PATCH: Restore task
    @PatchMapping("/{id}/restore")
    public ResponseEntity<Task> restoreTask(@PathVariable Long id) {
        try {
            User currentUser = getCurrentUser();
            Task restoredTask = taskService.restoreTask(id, currentUser);
            return ResponseEntity.ok(restoredTask);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // DELETE: Soft delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        try {
            User currentUser = getCurrentUser();
            taskService.deleteTask(id, currentUser);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // DELETE: Permanent delete
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> permanentlyDeleteTask(@PathVariable Long id) {
        try {
            User currentUser = getCurrentUser();
            taskService.permanentlyDeleteTask(id, currentUser);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // DELETE: Delete all
    @DeleteMapping
    public ResponseEntity<Void> deleteAllTasks() {
        User currentUser = getCurrentUser();
        taskService.deleteAllTasks(currentUser);
        return ResponseEntity.noContent().build();
    }
}