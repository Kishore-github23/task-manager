// src/app/components/task-list/task-list.component.ts

import { Component, OnInit } from '@angular/core';
import { TaskService } from '../../services/task.service';
import { Task, TaskStatus, TaskPriority } from '../../models/task.model';

@Component({
  selector: 'app-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.css']
})
export class TaskListComponent implements OnInit {
  tasks: Task[] = [];
  filteredTasks: Task[] = [];
  selectedTask: Task | null = null;
  showForm = false;
  searchKeyword = '';
  filterStatus: TaskStatus | 'ALL' = 'ALL';
  filterPriority: TaskPriority | 'ALL' = 'ALL';  // NEW
  isLoading = false;

  // NEW: View modes
  viewMode: 'active' | 'archived' | 'deleted' = 'active';

  // Make enums available to template
  TaskStatus = TaskStatus;
  TaskPriority = TaskPriority;

  constructor(private taskService: TaskService) { }

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.isLoading = true;
    
    if (this.viewMode === 'archived') {
      this.taskService.getArchivedTasks().subscribe({
        next: (data) => {
          this.tasks = data;
          this.applyFilter();
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading archived tasks:', error);
          alert('Failed to load archived tasks!');
          this.isLoading = false;
        }
      });
    } else if (this.viewMode === 'deleted') {
      this.taskService.getDeletedTasks().subscribe({
        next: (data) => {
          this.tasks = data;
          this.applyFilter();
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading deleted tasks:', error);
          alert('Failed to load deleted tasks!');
          this.isLoading = false;
        }
      });
    } else {
      // Active tasks (default)
      this.taskService.getAllTasks().subscribe({
        next: (data) => {
          this.tasks = data;
          this.applyFilter();
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading tasks:', error);
          alert('Failed to load tasks. Make sure backend is running on port 8080!');
          this.isLoading = false;
        }
      });
    }
  }

  // Apply filters (status, priority, and search)
  applyFilter(): void {
    let filtered = [...this.tasks];

    // Filter by status
    if (this.filterStatus !== 'ALL') {
      filtered = filtered.filter(task => task.status === this.filterStatus);
    }

    // NEW: Filter by priority
    if (this.filterPriority !== 'ALL') {
      filtered = filtered.filter(task => task.priority === this.filterPriority);
    }

    // Filter by search keyword
    if (this.searchKeyword.trim()) {
      const keyword = this.searchKeyword.toLowerCase();
      filtered = filtered.filter(task =>
        task.title.toLowerCase().includes(keyword) ||
        (task.description && task.description.toLowerCase().includes(keyword))
      );
    }

    this.filteredTasks = filtered;
  }

  onFilterChange(): void {
    this.applyFilter();
  }

  onSearch(): void {
    this.applyFilter();
  }

  // NEW: Change view mode
  changeViewMode(mode: 'active' | 'archived' | 'deleted'): void {
    this.viewMode = mode;
    this.loadTasks();
  }

  openForm(task?: Task): void {
    this.selectedTask = task || null;
    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false;
    this.selectedTask = null;
  }

  onTaskSaved(): void {
    this.loadTasks();
    this.closeForm();
  }

  updateStatus(task: Task, status: TaskStatus): void {
    if (task.id) {
      this.taskService.updateTaskStatus(task.id, status).subscribe({
        next: () => {
          this.loadTasks();
        },
        error: (error) => {
          console.error('Error updating status:', error);
          alert('Failed to update task status!');
        }
      });
    }
  }

  // Delete task (soft delete)
  deleteTask(id: number | undefined): void {
    if (id && confirm('Are you sure you want to delete this task?')) {
      this.taskService.deleteTask(id).subscribe({
        next: () => {
          this.loadTasks();
        },
        error: (error) => {
          console.error('Error deleting task:', error);
          alert('Failed to delete task!');
        }
      });
    }
  }

  // NEW: Archive task
  archiveTask(id: number | undefined): void {
    if (id && confirm('Are you sure you want to archive this task?')) {
      this.taskService.archiveTask(id).subscribe({
        next: () => {
          this.loadTasks();
        },
        error: (error) => {
          console.error('Error archiving task:', error);
          alert('Failed to archive task!');
        }
      });
    }
  }

  // NEW: Unarchive task
  unarchiveTask(id: number | undefined): void {
    if (id && confirm('Do you want to restore this task?')) {
      this.taskService.unarchiveTask(id).subscribe({
        next: () => {
          this.loadTasks();
        },
        error: (error) => {
          console.error('Error unarchiving task:', error);
          alert('Failed to unarchive task!');
        }
      });
    }
  }

  // NEW: Restore deleted task
  restoreTask(id: number | undefined): void {
    if (id && confirm('Do you want to restore this task?')) {
      this.taskService.restoreTask(id).subscribe({
        next: () => {
          this.loadTasks();
        },
        error: (error) => {
          console.error('Error restoring task:', error);
          alert('Failed to restore task!');
        }
      });
    }
  }

  // NEW: Permanent delete
  permanentlyDeleteTask(id: number | undefined): void {
    if (id && confirm('⚠️ This will PERMANENTLY delete the task. This action cannot be undone. Are you sure?')) {
      this.taskService.permanentlyDeleteTask(id).subscribe({
        next: () => {
          this.loadTasks();
        },
        error: (error) => {
          console.error('Error permanently deleting task:', error);
          alert('Failed to permanently delete task!');
        }
      });
    }
  }

  getPriorityClass(priority: TaskPriority): string {
    switch (priority) {
      case TaskPriority.HIGH:
        return 'badge bg-danger';
      case TaskPriority.MEDIUM:
        return 'badge bg-warning text-dark';
      case TaskPriority.LOW:
        return 'badge bg-success';
      default:
        return 'badge bg-secondary';
    }
  }

  getStatusClass(status: TaskStatus): string {
    switch (status) {
      case TaskStatus.TODO:
        return 'badge bg-secondary';
      case TaskStatus.IN_PROGRESS:
        return 'badge bg-primary';
      case TaskStatus.COMPLETED:
        return 'badge bg-success';
      default:
        return 'badge bg-secondary';
    }
  }

  getStatusText(status: TaskStatus): string {
    return status.replace('_', ' ');
  }
}