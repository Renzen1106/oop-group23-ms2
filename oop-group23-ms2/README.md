MotorPH Payroll System – OOP Terminal Assessment

Course: MO-IT03
Group: 23

📌 Project Overview

The MotorPH Employee Management System is a Java-based application designed to manage employee records, payroll processing, attendance tracking, and leave requests.

This system was developed for the MotorPH payroll case study and demonstrates the application of Object-Oriented Programming (OOP) principles such as:

Encapsulation
Inheritance
Polymorphism
Abstraction

The application uses Java Swing for the graphical user interface and follows a layered architecture that separates data, business logic, and presentation.

✨ Features
Employee Management (CRUD)
View, add, update, and delete employee records.
Payroll & Payslip Generation
Compute employee salary and display detailed payslip.
Role-Based Access Control (RBAC)
HR → Full access
IT → Manage employees + attendance + leave
Finance → View payslip + leave only
Attendance Management
Record Time In / Time Out and view attendance history.
Leave Request Management
Submit, approve, reject, and track leave requests.
Input Validation & Error Handling
Prevents invalid inputs and improves system reliability.
CSV-Based Data Storage
All system data is stored using CSV files.

🏗 Project Structure

The project follows a layered architecture for better organization and maintainability.

 Source Code

src/com/motorph/employeeapp

gui – graphical user interface (dialogs and main frame)
model – domain models (Employee, AttendanceRecord, LeaveRequest, etc.)
repository – CSV-based data access layer
service – business logic (PayrollService, AttendanceService, LeaveService)

 Other Folders
data – CSV files used by the system
docs – UML diagrams and documentation
lib – external libraries

Main GUI Classes

EmployeeManagementFrame – Main application window
LoginDialog – Handles authentication
AddRecordDialog – Adds new employee
UpdateDialog – Updates employee data
PayslipSplitDialog – Displays salary computation
AttendanceDialog – Handles attendance recording
LeaveDialog – Handles leave requests

 How to Run the Application
Open the project in VS Code or any Java IDE
Navigate to:
src/com/motorph/employeeapp/gui
Run:
EmployeeManagementLauncher.java


🛠 Technologies Used
Java
Java Swing
CSV File Handling
Object-Oriented Programming (OOP)

Notes
All data is stored in CSV files inside the data folder.
The system uses a layered architecture (Model, DAO, Service, GUI).
RBAC is enforced both in the UI and backend logic.
Validation is applied across forms to prevent invalid inputs.

🎓 Academic Purpose

This project was developed as part of the MO-IT03 Object-Oriented Programming course to demonstrate the design, refactoring, and implementation of a payroll-based employee management system using OOP principles.