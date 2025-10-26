// src/app/models/user.model.ts
export interface User {
  id: number;
  username: string;
  email: string;
  fullName?: string;
  role: string;
}