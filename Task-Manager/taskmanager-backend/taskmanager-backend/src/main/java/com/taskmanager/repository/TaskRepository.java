package com.taskmanager.repository;

import com.taskmanager.model.Task;
import com.taskmanager.model.Task.TaskStatus;
import com.taskmanager.model.Task.TaskPriority;
import com.taskmanager.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByUserAndDeletedAtIsNull(User user);
    
    Optional<Task> findByIdAndUser(Long id, User user);
    
    List<Task> findByUserAndStatusAndDeletedAtIsNull(User user, TaskStatus status);
    
    List<Task> findByUserAndPriorityAndDeletedAtIsNull(User user, TaskPriority priority);
    
    List<Task> findByUserAndTitleContainingIgnoreCaseAndDeletedAtIsNull(User user, String title);
    
    List<Task> findByUserAndDeletedAtIsNullOrderByCreatedAtDesc(User user);
    
    Page<Task> findByUserAndDeletedAtIsNull(User user, Pageable pageable);
    
    List<Task> findByUserAndArchivedAndDeletedAtIsNull(User user, boolean archived);
    
    List<Task> findByUserAndDeletedAtIsNotNull(User user);
    
    @Query("SELECT t FROM Task t WHERE t.user = :user " +
           "AND t.deletedAt IS NULL " +
           "AND (:status IS NULL OR t.status = :status) " +
           "AND (:priority IS NULL OR t.priority = :priority) " +
           "AND (:archived IS NULL OR t.archived = :archived) " +
           "AND (:keyword IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Task> findByFilters(@Param("user") User user,
                            @Param("status") TaskStatus status,
                            @Param("priority") TaskPriority priority,
                            @Param("archived") Boolean archived,
                            @Param("keyword") String keyword,
                            Pageable pageable);
    
    long countByUserAndStatusAndDeletedAtIsNull(User user, TaskStatus status);
    
    @Query("SELECT t FROM Task t WHERE t.user = :user " +
           "AND t.deletedAt IS NULL " +
           "AND t.status != 'COMPLETED' " +
           "AND t.dueDate < :now")
    List<Task> findOverdueTasks(@Param("user") User user, @Param("now") LocalDateTime now);
}