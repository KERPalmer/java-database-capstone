## MySQL Database Schema Design

### Table: patients
- id: BIGINT, Primary Key, Auto Increment
- name: VARCHAR(100) NOT NULL
- email: VARCHAR(255) NOT NULL UNIQUE
- password: VARCHAR(255) NOT NULL
- phone: VARCHAR(15) NOT NULL
- address: VARCHAR(255) NOT NULL
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- updated_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

### Table: doctors
- id: BIGINT, Primary Key, Auto Increment
- name: VARCHAR(100) NOT NULL
- specialty: VARCHAR(50) NOT NULL
- email: VARCHAR(255) NOT NULL UNIQUE
- password: VARCHAR(255) NOT NULL
- phone: VARCHAR(15) NOT NULL
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- updated_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

### Table: admins
- id: BIGINT, Primary Key, Auto Increment
- username: VARCHAR(50) NOT NULL UNIQUE
- password: VARCHAR(255) NOT NULL
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- updated_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

### Table: appointments
- id: BIGINT, Primary Key, Auto Increment
- doctor_id: BIGINT NOT NULL, Foreign Key → doctors(id)
- patient_id: BIGINT NOT NULL, Foreign Key → patients(id)
- appointment_time: DATETIME NOT NULL
- status: TINYINT NOT NULL DEFAULT 0 (0 = Scheduled, 1 = Completed, 2 = Cancelled)

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

