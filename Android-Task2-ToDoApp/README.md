# To-Do App with Login

## OASIS INFOBYTE Internship — Android App Development Task 2

A simple Android To-Do application with user authentication and user-specific task management.

## Project Overview

This project was developed as part of the OASIS INFOBYTE internship under the Android App Development track.

The application allows users to:

- Create a new account
- Log in securely
- Store passwords as SHA-256 hashes
- Maintain a personal task list
- Add new tasks
- Mark tasks as completed
- Unmark completed tasks
- Delete tasks
- View an empty-state message when there are no tasks
- Stay logged in using a local session
- Log out securely

Each user's tasks are stored separately using their unique user ID.

## Features

### User Authentication

- User registration
- Email validation
- Password confirmation
- Minimum 6-character password requirement
- SHA-256 password hashing
- Login authentication
- Logout functionality

### Task Management

- Add tasks
- Display tasks in a RecyclerView
- Mark tasks as completed
- Strike-through appearance for completed tasks
- Unmark tasks
- Delete tasks
- Empty task state
- Tasks are associated with the logged-in user

### Session Management

The application uses SharedPreferences to maintain the user's login session.

When a logged-in user reopens the application, the session is restored automatically.

Logging out clears the saved session.

## Technologies Used

- Java
- XML
- Android Studio
- SQLite
- SharedPreferences
- RecyclerView
- SHA-256
- Android SDK

## Database

The application uses SQLite for local data storage.

### Users Table

| Column | Description |
|---|---|
| id | Unique user ID |
| name | User name |
| email | User email |
| password | SHA-256 hashed password |

### Tasks Table

| Column | Description |
|---|---|
| id | Unique task ID |
| user_id | ID of the task owner |
| task | Task description |
| is_completed | Completion status |

## Application Flow

```text
Sign Up
   ↓
User Account Created
   ↓
Login
   ↓
To-Do Dashboard
   ↓
Add / Complete / Delete Tasks
   ↓
Logout