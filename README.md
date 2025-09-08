# 🏦 Sistema Bancario - Bootcamp Tech Girls Power: Entregable 3

Proyecto incremental de un sistema bancario desarrollado durante el bootcamp, evolucionando desde una aplicación monolítica en Java hasta una arquitectura de
microservicios con Spring Boot.

## 📋 Descripción del Proyecto

Este conjunto de proyectos plantea el desarrollo progresivo de un sistema integral para el sector bancario, orientado a la gestión de clientes, cuentas
bancarias y transacciones financieras. A través de una serie de etapas, cada una más compleja que la anterior, se aplican y refuerzan conocimientos clave en
diseño orientado a objetos, arquitectura basada en microservicios, bases de datos relacionales y no relacionales, desarrollo reactivo, aseguramiento de la
calidad y buenas prácticas de programación.

## 🚀 Entregables

### 🔹 Entregable-3: Spring WebFlux

- **Tecnologías**: SpringWebFlux, MongoDB reactivo, Spring Validation, WebClient, Lombok, OpenAPI
- **Arquitectura**: Microservicios (customer-service, account-service, transaction-service)
- **Funcionalidades**:
    - Registro y consulta de historial de transacciones (depósitos, retiros, transferencias)
    - Consultar historial de transacciones
    - OpenAPI [contract-first]
    - Comunicación entre microservicios

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
│   ├── Diagrama UML - Entregable 3.pdf
│   └── Resultados del Postman.pdf
│
├── services/
│   ├── customer-service/     # Microservicio de clientes
│   ├── account-service/      # Microservicio de cuentas
│   └── transaction-service/  # Microservicio de transacciones
│   └── gateway/              # API Gateway
│
└── README.md
```

## 👥 Colaboradoras

| Nombre           | GitHub                                                                                                                            | Rol                    | Contribución   |
|------------------|-----------------------------------------------------------------------------------------------------------------------------------|------------------------|----------------|
| Angie Loa        | [![GitHub](https://img.shields.io/badge/GitHub-@AngieLoaPacora-pink?style=flat&logo=github)](https://github.com/AngieLoaPacora)   | Desarrolladora Backend | Depositar      |
| Aracely Coronel  | [![GitHub](https://img.shields.io/badge/GitHub-@jaz123456789-pink?style=flat&logo=github)](https://github.com/jaz123456789)       | Desarrolladora Backend | Retirar        |
| Stephanie Azorsa | [![GitHub](https://img.shields.io/badge/GitHub-@StephanieAzorsa-pink?style=flat&logo=github)](https://github.com/StephanieAzorsa) | Desarrolladora Backend | Transferencia  |
| Andrea Molina    | [![GitHub](https://img.shields.io/badge/GitHub-@Moliinaandy-pink?style=flat&logo=github)](https://github.com/Moliinaandy)         | Desarrolladora Backend | Historial      |

