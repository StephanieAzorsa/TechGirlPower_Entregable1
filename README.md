# 🏦 Sistema Bancario - Bootcamp Tech Girls Power

Proyecto incremental de un sistema bancario desarrollado durante el bootcamp, evolucionando desde una aplicación monolítica en Java hasta una arquitectura de
microservicios con Spring Boot.

## 🔹 Entregable-4: Pruebas Unitarias y Calidad de Código

**Tecnologías:**  JUnit 5, Mockito, Jacoco y Checkstyle  

**Arquitectura:**  Microservicios (customer-service, account-service y transaction-service)  

**Funcionalidades probadas:**

-  Validación de creación de cuentas (saldo inicial mayor a 0, cliente existente)  
-  Operaciones sobre cuentas: depósitos, retiros (cuentas de ahorro y corriente con sobregiro)  
-  Manejo de excepciones personalizadas
-  Cobertura de pruebas con **Jacoco** 
-  Estándares de calidad de código con **Checkstyle**
-  Aplicación de los Principios SOLID
-  Aplicacion de Patrones de diseño: Factory method en account-service y Strategy en customer-service

## 📁 Project Structure

```
├──project-root/
├── services/
│   ├── customer-service/         # Microservicio de clientes
│   │   └── src/test/java/com/nttdata/customerservice/
│   │       ├── controller/
│   │       │   └── CustomerControllerTest.java
│   │       ├── exception/
│   │       │   └── GlobalExceptionHandlerTest.java
│   │       └── service/
│   │           ├── CustomerServiceTest.java
│   │           ├── AccountValidationServiceTest.java
│   │           └── strategy/
│   │               ├── DniValidationStrategyTest.java
│   │               └── ValidationContextTest.java
│   │
│   ├── account-service/          # Microservicio de cuentas
│   │   └── src/test/java/com/nttdata/accountservice/
│   │       ├── controller/
│   │       │   └── AccountControllerTest.java
│   │       ├── exception/
│   │       │   └── GlobalExceptionHandlerTest.java
│   │       └── service/
│   │           ├── AccountServiceImplTest.java
│   │           └── TransactionServiceImplTest.java
│   │
│   ├── transaction-service/      # Microservicio de transacciones
│   │   └── src/test/java/com/nttdata/transactionservice/
│   │       └── (tests pendientes o futuros)
│   │
│   └── gateway/                  # API Gateway
│
└── README.md
```    

## 👥 Colaboradoras

| Nombre           | GitHub                                                                                                                            |
|------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| Andrea Molina    | [![GitHub](https://img.shields.io/badge/GitHub-@Moliinaandy-pink?style=flat&logo=github)](https://github.com/Moliinaandy)         |
| Angie Loa        | [![GitHub](https://img.shields.io/badge/GitHub-@AngieLoaPacora-pink?style=flat&logo=github)](https://github.com/AngieLoaPacora)   | 
| Aracely Coronel  | [![GitHub](https://img.shields.io/badge/GitHub-@jaz123456789-pink?style=flat&logo=github)](https://github.com/jaz123456789)       | 
| Stephanie Azorsa | [![GitHub](https://img.shields.io/badge/GitHub-@StephanieAzorsa-pink?style=flat&logo=github)](https://github.com/StephanieAzorsa) | 

