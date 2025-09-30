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
  isLoading = false;

  // Make enums available to template
  TaskStatus = TaskStatus;
  TaskPriority = TaskPriority;

  constructor(private taskService: TaskService) { }

  ngOnInit(): void {
    this.loadTasks();
  }

  // Load all tasks from backend
  loadTasks(): void {
    this.isLoading = true;
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

  // Apply filters (status and search)
  applyFilter(): void {
    if (this.filterStatus === 'ALL') {
      this.filteredTasks = this.tasks;
    } else {
      this.filteredTasks = this.tasks.filter(task => task.status === this.filterStatus);
    }

    if (this.searchKeyword.trim()) {
      this.filteredTasks = this.filteredTasks.filter(task =>
        task.title.toLowerCase().includes(this.searchKeyword.toLowerCase()) ||
        (task.description && task.description.toLowerCase().includes(this.searchKeyword.toLowerCase()))
      );
    }
  }

  // Filter change handler
  onFilterChange(): void {
    this.applyFilter();
  }

  // Search handler
  onSearch(): void {
    this.applyFilter();
  }

  // Open form for creating or editing task
  openForm(task?: Task): void {
    this.selectedTask = task || null;
    this.showForm = true;
  }

  // Close form
  closeForm(): void {
    this.showForm = false;
    this.selectedTask = null;
  }

  // Handle task saved event
  onTaskSaved(): void {
    this.loadTasks();
    this.closeForm();
  }

  // Update task status
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

  // Delete task
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

  // Get CSS class for priority badge
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

  // Get CSS class for status badge
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

  // Format status display text
  getStatusText(status: TaskStatus): string {
    return status.replace('_', ' ');
  }
}