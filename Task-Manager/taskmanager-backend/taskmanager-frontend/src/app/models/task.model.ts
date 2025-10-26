// src/app/models/task.model.ts

export interface Task {
  id?: number;
  title: string;
  description: string;
  status: TaskStatus;
  priority: TaskPriority;
  dueDate: Date | null;
  archived?: boolean;          // NEW
  deletedAt?: Date | null;     // NEW
  createdAt?: Date;
  updatedAt?: Date;
}

export enum TaskStatus {
  TODO = 'TODO',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED'
}

export enum TaskPriority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH'
}

// NEW: Pagination interface
export interface PagedTasks {
  content: Task[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}