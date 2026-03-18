## MySQL Database Schema Design

### Table: patients
- id: INT, Primary Key, Auto Increment
- name: VARCHAR(100) Not Null
- email: VARCHAR(255) Not Null UNIQUE
- password: VARCHAR(255) Not Null
- address: VARCHAR(255) Not Null
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- updated_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

### Table: doctors
- id: INT, Primary Key, Auto Increment
- name: VARCHAR(100) Not Null
- specialty: VARCHAR(50) Not Null
- email: VARCHAR(255) Not Null UNIQUE
- password: VARCHAR(255) Not Null
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- updated_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

### Table: admins
- id: INT, Primary Key, Auto Increment
- username: VARCHAR(50) Not Null UNIQUE
- email: VARCHAR(255) Not Null UNIQUE
- password: VARCHAR(255) Not Null
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- updated_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

### Table: appointments
- id: INT, Primary Key, Auto Increment
- doctor_id: INT, Foreign Key → doctors(id)
- patient_id: INT, Foreign Key → patients(id)
- appointment_time: DATETIME, Not Null
- status: INT (0 = Scheduled, 1 = Completed, 2 = Cancelled)

## MongoDB Collection Design

### Collection: prescriptions
- _id: ObjectId (Primary Key)
- patientName: String (required, min: 3, max: 100 characters)
- appointmentId: Long (required, references appointments.id)
- medication: String (required, min: 3, max: 100 characters)
- dosage: String (required)
- doctorNotes: String (optional, max: 200 characters)
- refillCount: Integer (optional, default: 0)
- pharmacy: Object (optional)
  - name: String
  - location: String
- prescribedDate: Date (default: current timestamp)
- created_at: Date (default: current timestamp)
- updated_at: Date (default: current timestamp)

