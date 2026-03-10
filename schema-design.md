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
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- updated_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

## MongoDB Collection Design

### Collection: prescriptions
- _id: ObjectId (Primary Key)
- patientName: String (required, min: 3, max: 100 characters)
- appointmentId: Long (required, references appointments.id)
- medication: String (required, min: 3, max: 100 characters)
- dosage: String (required)
- doctorNotes: String (optional, max: 200 characters)
- prescribedDate: Date (default: current timestamp)
- created_at: Date (default: current timestamp)
- updated_at: Date (default: current timestamp)

### Indexes:
- appointmentId: ascending index for quick lookup by appointment
- patientName: text index for searching prescriptions by patient name
- prescribedDate: descending index for chronological ordering

## Database Relationships & Constraints

### Foreign Key Constraints:
- appointments.doctor_id → doctors.id (ON DELETE RESTRICT, ON UPDATE CASCADE)
- appointments.patient_id → patients.id (ON DELETE RESTRICT, ON UPDATE CASCADE)

### Unique Constraints:
- patients.email: UNIQUE
- doctors.email: UNIQUE
- admins.username: UNIQUE

### Check Constraints:
- appointments.status: IN (0, 1, 2)
- appointments.appointment_time: > CURRENT_TIMESTAMP (future appointments only)

### Business Rules:
- Appointments are 1-hour long by default (calculated from appointment_time)
- Passwords should be hashed using BCrypt before storage
- Email addresses must follow valid email format
- Phone numbers should be validated for 10-digit format
- Patient names, doctor names, and medication names have length limits for data integrity

## Design Notes

### MySQL Design Decisions:
- Used BIGINT for primary keys to support large datasets
- Added created_at and updated_at timestamps for audit trails
- Used appropriate VARCHAR lengths based on typical data requirements
- Status field uses TINYINT for efficiency with enumerated values

### MongoDB Design Decisions:
- Used flexible document structure for prescriptions to accommodate varying medication details
- Included appointmentId reference to maintain relationship with MySQL appointments
- Added timestamps for tracking prescription history
- Used text indexes for search functionality

### Security Considerations:
- Passwords are stored as hashed values (BCrypt recommended)
- Sensitive fields are marked for write-only access in API responses
- Email uniqueness prevents account conflicts
- Foreign key constraints prevent orphaned records