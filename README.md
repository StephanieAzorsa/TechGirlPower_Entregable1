# 🏦 Sistema Bancario - Bootcamp Tech Girls Power: Entregable 2

Proyecto incremental de un sistema bancario desarrollado durante el bootcamp, evolucionando desde una aplicación monolítica en Java hasta una arquitectura de
microservicios con Spring Boot.

## 📋 Descripción del Proyecto

Este conjunto de proyectos plantea el desarrollo progresivo de un sistema integral para el sector bancario, orientado a la gestión de clientes, cuentas
bancarias y transacciones financieras. A través de una serie de etapas, cada una más compleja que la anterior, se aplican y refuerzan conocimientos clave en
diseño orientado a objetos, arquitectura basada en microservicios, bases de datos relacionales y no relacionales, desarrollo reactivo, aseguramiento de la
calidad y buenas prácticas de programación.

## 🚀 Entregables

### 🔹 Entregable-2: Spring Boot + Microservicios

- **Tecnologías**: Spring Boot, Spring Cloud, JPA/Hibernate, MySQL, Spring Validation, Lombok, RestTemplate, OpenAPI
- **Arquitectura**: Microservicios (customer-service, account-service)
- **Funcionalidades**:
    - CRUD completo de clientes y cuentas
    - Validación de DNI único y formato de email
    - Prevención de eliminación si tiene cuentas activas
    - Creación de cuentas (Ahorros/Corrientes) asociadas a clientes
    - API REST documentada con OpenAPI
    - Comunicación entre microservicios
    - Persistencia con JPA/Hibernate


### 👥 Colaboradoras

| Nombre           | GitHub                                                                                                                            | Rol                    | Contribución               |
|------------------|-----------------------------------------------------------------------------------------------------------------------------------|------------------------|----------------------------|
| Stephanie Azorsa | [![GitHub](https://img.shields.io/badge/GitHub-@StephanieAzorsa-pink?style=flat&logo=github)](https://github.com/StephanieAzorsa) | Desarrolladora Backend | Microservicio Customer     |
| Angie Loa        | [![GitHub](https://img.shields.io/badge/GitHub-@AngieLoaPacora-pink?style=flat&logo=github)](https://github.com/AngieLoaPacora)   | Desarrolladora Backend | Microservicio Account: Crear cuenta bancaria          |
| Aracely Coronel  | [![GitHub](https://img.shields.io/badge/GitHub-@jaz123456789-pink?style=flat&logo=github)](https://github.com/jaz123456789)       | Desarrolladora Backend | Microservicio Account: Listar cuentas, listar una cuenta, listar cuentas de un cliente y eliminar cuenta |
| Andrea Molina    | [![GitHub](https://img.shields.io/badge/GitHub-@Moliinaandy-pink?style=flat&logo=github)](https://github.com/Moliinaandy)         | Desarrolladora Backend | Microservicio Account: Depositar y Retirar dinero       |
