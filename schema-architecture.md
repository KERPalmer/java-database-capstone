
Overview:
Spring Boot application uses both MVC and REST controllers.

 Thymeleaf templates are used for the Admin and Doctor dashboards
 REST APIs serve all other modules.
 
 Databases
  MySQL for patient, doctor, appointment, and admin data with JPA entities
  MongoDB for prescriptions with document models.

  All controllers route requests through a common service layer, which in turn delegates to the appropriate repositories


1. User accesses AdminDashboard or Appointment pages.
2. The action is routed to the appropriate Thymeleaf or REST controller.
3. The controller calls the service and the service layer performs any business logic, rules and validations.
4. The service layer ueses the repository layer to access two repositories: MySQL and MongoDB
5. Each repository accesses the underlying database engine
6. Once data is retrieved it is mapped to the appropiate Java model. JPA entites for MySQL and document objects for MongoDB
7. The Models include: patient, doctor, appointment and admin models 
