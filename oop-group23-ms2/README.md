# 🚀 MotorPH Payroll System – OOP Implementation (MS2)

**Course:** MO-IT03  
**Group:** 23  

---

## 📌 Project Overview

The **MotorPH Employee Management System** is a Java-based application designed to manage employee records and compute payroll information.

This system was developed for the **MotorPH payroll case study** and demonstrates the application of **Object-Oriented Programming (OOP) principles** such as:

- Encapsulation
- Inheritance
- Polymorphism
- Abstraction

The application uses **Java Swing** to provide a graphical user interface and follows a **layered architecture** that separates data, business logic, and presentation.

---

## ✨ Features

1. **View Employee Records**  
   Display employee information in a table.

2. **Add Employee**  
   Insert new employee records into the system.

3. **Update Employee**  
   Modify existing employee details.

4. **Delete Employee**  
   Remove employee records.

5. **Calculate Salary**  
   Generate salary computation for employees.

6. **CSV-Based Data Storage**  
   Employee data is stored using CSV files.

---

## 📂 Project Structure

The project follows a **layered architecture** for better organization and maintainability.

### Source Code

`src/com/motorph/employeeapp`

- **console** – console-based application components  
- **gui** – graphical user interface classes and dialogs  
- **model** – domain models such as the Employee class  
- **repository** – data access layer responsible for CSV handling  
- **service** – business logic and payroll computation  

### Other Folders

- **data** – contains CSV files used by the application  
- **docs** – UML diagrams and documentation  
- **lib** – external libraries used by the project  

---

## 🖥️ Main GUI Classes

| Class | Description |
|------|-------------|
| **EmployeeManagementFrame** | Main application window for employee management |
| **LoginDialog** | Handles user login |
| **AddRecordDialog** | Adds a new employee |
| **UpdateDialog** | Updates employee information |
| **ViewRecordDialog** | Displays employee details |
| **PayslipSplitDialog** | Displays salary computation |

---

## ▶️ How to Run the Application

1. Open the project in **VS Code** or any **Java IDE**.
2. Navigate to: src/com/motorph/employeeapp/gui
3. Run the following file:

**EmployeeManagementFrame.java**

This class launches the main **Employee Management GUI**.

> Note: `EmployeeManagementLauncher.java` exists in the project but the system is designed to run directly from **EmployeeManagementFrame.java**.

---

## 🛠 Technologies Used

- **Java**
- **Java Swing**
- **CSV File Handling**
- **Object-Oriented Programming**

---

## 📖 Notes

- Employee data is stored in CSV format inside the **data** folder.
- The application uses a **layered architecture** to separate the user interface, business logic, and data access layers.

---

## 🎓 Academic Purpose

This project was developed as part of the **MO-IT03 Object-Oriented Programming course** to demonstrate the design and implementation of a payroll-based employee management system.
