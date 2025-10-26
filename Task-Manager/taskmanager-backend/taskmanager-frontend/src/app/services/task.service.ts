// src/app/services/task.service.ts

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PagedTasks, Task, TaskPriority, TaskStatus } from '../models/task.model';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private apiUrl = 'http://localhost:8080/api/tasks';

  constructor(private http: HttpClient) { }

  // Get all tasks
  getAllTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(this.apiUrl);
  }

  // Get task by ID
  getTaskById(id: number): Observable<Task> {
    return this.http.get<Task>(`${this.apiUrl}/${id}`);
  }

  // Get tasks by status
  getTasksByStatus(status: TaskStatus): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/status/${status}`);
  }

  // Search tasks by keyword
  searchTasks(keyword: string): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/search?keyword=${keyword}`);
  }

  // Create new task
  createTask(task: Task): Observable<Task> {
    return this.http.post<Task>(this.apiUrl, task);
  }

  // Update existing task
  updateTask(id: number, task: Task): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/${id}`, task);
  }

  // Update task status only
  updateTaskStatus(id: number, status: TaskStatus): Observable<Task> {
    return this.http.patch<Task>(`${this.apiUrl}/${id}/status`, { status });
  }

  // Delete task
  deleteTask(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // ===== NEW METHODS FOR PHASE 1 =====

  // Get tasks with pagination
  getTasksPaginated(page: number = 0, size: number = 10, sortBy: string = 'createdAt', sortDir: string = 'desc'): Observable<PagedTasks> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDir', sortDir)
      .set('paginate', 'true');
    
    return this.http.get<PagedTasks>(this.apiUrl, { params });
  }

  // Advanced filtering
  filterTasks(
    status?: TaskStatus,
    priority?: TaskPriority,
    archived?: boolean,
    keyword?: string,
    page: number = 0,
    size: number = 10
  ): Observable<PagedTasks> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (status) params = params.set('status', status);
    if (priority) params = params.set('priority', priority);
    if (archived !== undefined) params = params.set('archived', archived.toString());
    if (keyword) params = params.set('keyword', keyword);

    return this.http.get<PagedTasks>(`${this.apiUrl}/filter`, { params });
  }

  // Get archived tasks
  getArchivedTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/archived`);
  }

  // Get deleted tasks
  getDeletedTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/deleted`);
  }

  // Archive task
  archiveTask(id: number): Observable<Task> {
    return this.http.patch<Task>(`${this.apiUrl}/${id}/archive`, {});
  }

  // Unarchive task
  unarchiveTask(id: number): Observable<Task> {
    return this.http.patch<Task>(`${this.apiUrl}/${id}/unarchive`, {});
  }

  // Restore deleted task
  restoreTask(id: number): Observable<Task> {
    return this.http.patch<Task>(`${this.apiUrl}/${id}/restore`, {});
  }

  // Permanent delete
  permanentlyDeleteTask(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}/permanent`);
  }

  // Get tasks by priority
  getTasksByPriority(priority: TaskPriority): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/priority/${priority}`);
  }
}