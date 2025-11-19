# 🏦 Banking Platform

Proyecto incremental de un sistema bancario, evolucionando desde una aplicación monolítica en Java hasta una arquitectura de
microservicios con Spring Boot.



## 🏗️ Estructura del Proyecto

```
project-root/
│
├── api-request/
│   └── account-ms            # Requests específicos para account-service
│   └── customer-ms           # Requests específicos para customer-service
│   └── transaction-ms        # Requests específicos para transaction-service
│   └── postman               # Colección de Postman
│
├── documentation/            # Documentación del proyecto
│
├── services/
│   ├── customer-service/     # Microservicio de clientes
│   ├── account-service/      # Microservicio de cuentas
│   └── transaction-service/  # Microservicio de transacciones
│   └── gateway/              # API Gateway
│
└── README.md
```

## 🏗️ Desripción de los microservicios

#### 👥 Microservicio de Clientes (customer-service)
- Gestión completa de clientes
- Validación de DNI único y formato de email
- Prevención de eliminación si tiene cuentas activas

#### 💰 Microservicio de Cuentas (account-service)
- Gestión completa de cuentas
- Creación de cuentas (Ahorros/Corrientes) asociadas a clientes
- Prevención del saldo mayor a 0
- No se puede realizar retiro que deje el saldo en negativo para cuentas de Ahorro
- Las cuentas corrientes pueden tener un sobregiro de hasta -500 

#### 🔄 Microservicio de Transacciones (transaction-service)
- Depósitos y retiros
- Transferencias entre cuentas
- Historial transaccional

## 🛠 Stack Tecnológico por Entregable

| Entregable | Tecnologías                                                        | Arquitectura   |
|------------|--------------------------------------------------------------------|----------------|
| 1          | Java 8/11, MySQL, UML                                              | Monolítica     |
| 2          | Spring Boot, Spring Cloud, Spring Data JPA, MySQL, OpenAPI         | Microservicios |
| 3          | Spring Boot, Spring Webflux, Spring Data Reactive MongoDB, OpenAPI | Microservicios |
| 4          | JUnit 5, Mockito, Jacoco, Checkstyle                               | Microservicios |


## 📊 Descripción de Ramas

| Rama         | Propósito                                                    |
|--------------|--------------------------------------------------------------|
| master       | Versión estable en producción                                | 
| develop      | Integración para desarrollo activo                           |
| feature/\*   | Desarrollo de nuevas funcionalidades                         |
| hotfix/\*    | Correcciones urgentes                                        |
| Entregable-1 | Versión entregada del Proyecto (Java + MySQL)              |
| Entregable-2 | Versión entregada del Proyecto (Microservices + MySQL)    |
| Entregable-3 | Versión entregada del Proyecto (Microservice + MongoDB)  |
| Entregable-4 | Versión entregada del Proyecto (Pruebas Unitarias reactivas y no reactivas (JUnit + Mockito))|            

## 👥 Colaboradoras

| Nombre           | GitHub                                                                                                                            |
|------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| Andrea Molina    | [![GitHub](https://img.shields.io/badge/GitHub-@Moliinaandy-pink?style=flat&logo=github)](https://github.com/Moliinaandy)         |
| Angie Loa        | [![GitHub](https://img.shields.io/badge/GitHub-@AngieLoaPacora-pink?style=flat&logo=github)](https://github.com/AngieLoaPacora)   | 
| Aracely Coronel  | [![GitHub](https://img.shields.io/badge/GitHub-@jaz123456789-pink?style=flat&logo=github)](https://github.com/jaz123456789)       | 
| Stephanie Azorsa | [![GitHub](https://img.shields.io/badge/GitHub-@StephanieAzorsa-pink?style=flat&logo=github)](https://github.com/StephanieAzorsa) | 

