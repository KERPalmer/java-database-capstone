
Overview:
Spring Boot application uses both MVC and REST controllers.

 Thymeleaf templates are used for the Admin and Doctor dashboards
 REST APIs serve all other modules.
 
 Databases
  MySQL for patient, doctor, appointment, and admin data with JPA entities
  MongoDB for prescriptions with document models.
  
  All controllers route requests through a common service layer, which in turn delegates to the appropriate repositories