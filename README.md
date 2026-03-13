# 📦 Ikommercy API - Dropshipping Backend

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)]()
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)]()
[![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)]()
[![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)]()

> **Final Year Project - Systems Development Technical Course**
> 
> A RESTful API developed for the backend of the **Ikommercy** dropshipping web application, designed with a focus on security, performance, and software engineering best practices.

---

## 🚀 Overview

The system manages the entire data flow for the Ikommercy e-commerce platform. The application was built with **Java and Spring Boot**, utilizing a hybrid database architecture to optimize response times and ensure secure user sessions.

## 🛠️ Technologies Used

* **Language & Framework:** Java + Spring Boot
* **Relational Database:** MySQL (Structured persistence for users, products, orders, suppliers, etc.)
* **In-Memory Database:** Redis (High-performance session management and token versioning)
* **Security:** Spring Security + JWT (JSON Web Tokens)

## 🛡️ Security and Authentication

The system features a robust protection layer, implementing:
* **JWT Authentication:** Secure issuance and validation of user identity.
* **Blacklisting Strategy (Redis):** Strict control of active tokens through versioning, allowing for instant and secure session invalidation.
* **Role-Based Access Control (RBAC):** Verification of roles and granular permissions to protect API routes.

---

## 🌐 Project Ecosystem

Access the other components of the Ikommercy project and our technical documentation:

* 💻 **[Front-End Repository](https://github.com/K4uePinheiro/Ikommercy)** - E-commerce user interface.
* 📖 **[Documentation Site (JavaDocs)](https://sam-umbra.github.io/dropship-apidocs/)** - Complete documentation of endpoints, classes, and methods.
* 🗄️ **[Documentation Repository](https://github.com/Sam-Umbra/dropship-apidocs)** - Source code for the documentation.
