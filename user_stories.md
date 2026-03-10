# User Story Template

**Title:**
_As a [user role], I want [feature/goal], so that [reason]._

**Acceptance Criteria:**
1. [Criteria 1]
2. [Criteria 2]
3. [Criteria 3]

**Priority:** [High/Medium/Low]
**Story Points:** [Estimated Effort in Points]
**Notes:**
- [Additional information or edge cases]

---

## Admin User Stories

### 1. Admin Login with Username and Password

**Title:**
_As an Admin, I want to log into the portal with my username and password, so that I can manage the platform securely._

**Acceptance Criteria:**
1. Admin can enter username and password in the login form
2. System validates credentials against the admin model database and grants access upon successful authentication
3. Invalid credentials display an error message and prevent login access

**Priority:** High
**Story Points:** 5
**Notes:**
- Implement secure password hashing (e.g., bcrypt)
- Add account lockout after 3 failed login attempts
- Include "Remember Me" functionality (optional)
- Redirect authenticated admins to admin dashboard

---

### 2. Admin Logout

**Title:**
_As an Admin, I want to log out of the portal, so that I can protect system access._

**Acceptance Criteria:**
1. Admin can click a logout button to end their session
2. Session is terminated and user is redirected to the login page

**Priority:** High
**Story Points:** 2
**Notes:**

---

### 3. Add Doctors to the Portal

**Title:**
_As an Admin, I want to add Doctors to the portal, so that I can enable new doctors to access the system._

**Acceptance Criteria:**
1. Admin can access a form to add a new doctor
2. System validates that the email is unique and correctly formatted

**Priority:** High
**Story Points:** 8
**Notes:**
- Store doctor credentials securely

---

### 4. Delete a Doctor's Profile

**Title:**
_As an Admin, I want to delete a doctor's profile from the portal, so that they no longer have access to the platform._

**Acceptance Criteria:**
1. Admin can select and delete a doctor
2. System prevents deletion if the doctor has active or pending appointments (soft delete option provided)
3. Deleted doctor's account is deactivated and cannot log back in

**Priority:** High
**Story Points:** 5
**Notes:**
- Log all deletion actions

---

### 5. Run Stored Procedure for Monthly Appointment Statistics

**Title:**
_As an Admin, I want to run a stored procedure in MySQL to get the number of appointments per month, so that I can track usage statistics._

**Acceptance Criteria:**
1. Admin can view a statistics on dashboard

**Priority:** Medium
**Story Points:** 5
**Notes:**
- Create MySQL stored procedure with date range filtering


---

## Patient User Stories

### 1. View Doctors Without Logging In

**Title:**
_As a Patient, I want to view a list of doctors without logging in, so that I can explore options before registering._

**Acceptance Criteria:**
1. Patient can access and browse a public list of doctors
3. Patient can explore without creating an account or logging in

**Priority:** High
**Story Points:** 5
**Notes:**

---

### 2. Patient Sign Up with Email and Password

**Title:**
_As a Patient, I want to sign up using my email and password, so that I can book appointments._

**Acceptance Criteria:**
1. Patient can access a registration form with email and password fields
2. System validates email format and password strength requirements
3. Unique email validation ensures no duplicate accounts are created
4. Account is successfully created and patient can log in immediately

**Priority:** High
**Story Points:** 5
**Notes:**
- Implement password strength validation
- Store passwords securely

---

### 3. Patient Login to Manage Bookings

**Title:**
_As a Patient, I want to log into the portal, so that I can manage my bookings._

**Acceptance Criteria:**
1. Patient can enter email and password to log in
2. System authenticates credentials and grants access to patient dashboard
3. Invalid credentials display an error message
4. Patient can access appointment management features after successful login

**Priority:** High
**Story Points:** 5
**Notes:**
- Redirect to patient dashboard upon successful login

---

### 4. Patient Logout

**Title:**
_As a Patient, I want to log out of the portal, so that I can secure my account._

**Acceptance Criteria:**
1. Patient can click a logout button from their dashboard

**Priority:** High
**Story Points:** 2
**Notes:**
- Provide logout confirmation message

---

### 5. Book an Hour-Long Appointment with a Doctor

**Title:**
_As a Patient, I want to log in and book an hour-long appointment, so that I can consult with a doctor._

**Acceptance Criteria:**
1. Patient can select a doctor and available time slot from the appointment booking interface
2. System reserves a 1-hour appointment slot and displays confirmation
3. Appointment is saved to patient's record and appears in their upcoming appointments

**Priority:** High
**Story Points:** 8
**Notes:**

---

### 6. View Upcoming Appointments

**Title:**
_As a Patient, I want to view my upcoming appointments, so that I can prepare accordingly._

**Acceptance Criteria:**
1. Patient can access a list of all their upcoming appointments on their dashboard
2. Each appointment displays date, time, doctor name

**Priority:** High
**Story Points:** 5
**Notes:**
- Display appointments in chronological order

---

## Doctor User Stories

### 1. Doctor Login to Manage Appointments

**Title:**
_As a Doctor, I want to log into the portal, so that I can manage my appointments._

**Acceptance Criteria:**
1. Doctor can enter email/username and password to log in
2. System authenticates credentials and grants access to doctor dashboard

**Priority:** High
**Story Points:** 5
**Notes:**
- Redirect to doctor dashboard upon successful login

---

### 2. Doctor Logout

**Title:**
_As a Doctor, I want to log out of the portal, so that I can protect my data._

**Acceptance Criteria:**
1. Doctor can click a logout button from their dashboard
2. user is redirected to the login page

**Priority:** High
**Story Points:** 2
**Notes:**
- Provide logout confirmation message

---

### 3. View Appointment Calendar

**Title:**
_As a Doctor, I want to view my appointment calendar, so that I can stay organized._

**Acceptance Criteria:**
1. Doctor can view a calendar displaying all their scheduled appointments
2. Calendar shows appointment dates, times, patient names, and appointment status

**Priority:** High
**Story Points:** 8
**Notes:**

---

### 4. Mark Unavailability to Inform Patients

**Title:**
_As a Doctor, I want to mark my unavailability, so that I can inform patients of only available slots._

**Acceptance Criteria:**
1. Doctor can set unavailable time slots on their calendar
2. System prevents patients from booking appointments during marked unavailable periods
3. Unavailable periods are clearly visible to both doctor and patients
4. Doctor can easily update or remove unavilability

**Priority:** High
**Story Points:** 5
**Notes:**
- Support recurring unavailability (e.g., lunch breaks, specific days off)
- Allow setting unavailability reasons (meeting, training, personal)
- Notify patients when availability changes
- Show available slots to patients in real-time

---

### 5. Update Doctor Profile with Specialization

**Title:**
_As a Doctor, I want to update my profile with specialization and contact information, so that patients have up-to-date information._

**Acceptance Criteria:**
1. Doctor can access and edit their profile with fields for specialization, qualifications, and contact information
2. System validates data and saves changes
3. Updated profile information is visible to patients searching for doctors
4. Doctor can upload a professional photo or bio

**Priority:** Medium
**Story Points:** 5
**Notes:**
- Include fields for medical license/board certification numbers
- Allow upload of credentials and certifications
- Support multiple specializations
- Display patient ratings and reviews on profile
- Show average response time to appointment requests

---

### 6. View Patient Details for Upcoming Appointments

**Title:**
_As a Doctor, I want to view the patient details for upcoming appointments, so that I can be prepared._

**Acceptance Criteria:**
1. Doctor can click on an upcoming appointment to view patient medical history
2. Patient details include name, age, medical history, previous appointments, and notes
3. System displays any allergies or health conditions prominently for safety
4. Doctor can add or update notes for the upcoming appointment

**Priority:** High
**Story Points:** 5
**Notes:**
- Display patient contact information for appointment reminders
- Show previous appointment notes and diagnoses
- Include list of current medications
- Allow doctor to mark important details for reference
- Provide quick access to patient records during appointment
