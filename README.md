# NotePass - Notes and Password Manager

NotePass is a Java-based application that helps you manage your notes and passwords securely. It provides a simple and intuitive interface for storing and organizing your important information.

## Features

- User authentication with secure password hashing
- Notes management with title and content
- Password management with title, username, password, and notes
- SQLite database for data persistence
- Modern JavaFX user interface

## Requirements

- Java 11 or higher
- Maven 3.6 or higher

## Setup (for non windows devices)

1. Clone the repository:
```bash
git clone https://github.com/yourusername/NotePass.git
cd NotePass
```

2. Build the project:
```bash
mvn clean package
```

3. Run the application:
```bash
mvn javafx:run
```

## Usage

1. **Registration**
   - Launch the application
   - Click "Register" to create a new account
   - Enter your desired username and password
   - Click "Register" to create the account

2. **Login**
   - Enter your username and password
   - Click "Login" to access your account

3. **Managing Notes**
   - Enter a title for your note
   - Write your note content
   - Click "Add Note" to save
   - View your notes in the list below

4. **Managing Passwords**
   - Enter the title, username, password, and any additional notes
   - Click "Save Password" to store the information
   - View your saved passwords in the list below

5. **Logout**
   - Click the "Logout" button to securely exit the application

## Security Features

- Passwords are hashed using BCrypt
- User authentication required for access

**Issues so Far**
Not able to access saved notes and passwords after they're stored

**Upcoming Features**
   - Dark mode button
   - Better UI interface
   - Better security using an encrypted database (currently using partially encrypted xml file to store data and login info) 

## Contributing

Feel free to submit issues and enhancement requests! 