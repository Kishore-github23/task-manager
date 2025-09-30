// src/app/components/task-form/task-form.component.ts

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { TaskService } from '../../services/task.service';
import { Task, TaskStatus, TaskPriority } from '../../models/task.model';

@Component({
  selector: 'app-task-form',
  templateUrl: './task-form.component.html',
  styleUrls: ['./task-form.component.css']
})
export class TaskFormComponent implements OnInit {
  @Input() task: Task | null = null;
  @Output() taskSaved = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  formData: Task = {
    title: '',
    description: '',
    status: TaskStatus.TODO,
    priority: TaskPriority.MEDIUM,
    dueDate: null
  };

  statuses = Object.values(TaskStatus);
  priorities = Object.values(TaskPriority);
  isSubmitting = false;

  constructor(private taskService: TaskService) { }

  ngOnInit(): void {
    if (this.task) {
      // Edit mode - populate form with existing task data
      this.formData = { ...this.task };
      
      // Convert dueDate string to proper format for datetime-local input
      if (this.task.dueDate) {
        this.formData.dueDate = this.task.dueDate;
      }
    }
  }

  onSubmit(): void {
    // Validation
    if (!this.formData.title || !this.formData.title.trim()) {
      alert('Title is required!');
      return;
    }

    this.isSubmitting = true;

    if (this.task && this.task.id) {
      // Update existing task
      this.taskService.updateTask(this.task.id, this.formData).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.taskSaved.emit();
        },
        error: (error) => {
          console.error('Error updating task:', error);
          alert('Failed to update task!');
          this.isSubmitting = false;
        }
      });
    } else {
      // Create new task
      this.taskService.createTask(this.formData).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.taskSaved.emit();
        },
        error: (error) => {
          console.error('Error creating task:', error);
          alert('Failed to create task!');
          this.isSubmitting = false;
        }
      });
    }
  }

  onCancel(): void {
    this.cancelled.emit();
  }

  // Get display text for status
  getStatusText(status: TaskStatus): string {
    return status.replace('_', ' ');
  }
}