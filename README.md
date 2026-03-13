# 📦 Ikommercy API - Dropshipping Backend
[![Language: EN](https://img.shields.io/badge/Language-EN-blue?style=for-the-badge)](README.en.md)

---

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)]()
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)]()
[![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)]()
[![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)]()

> **Projeto de Conclusão de Curso (TCC) - Técnico em Desenvolvimento de Sistemas**
> 
> Uma API RESTful desenvolvida para o backend da aplicação web de dropshipping **Ikommercy**, projetada com foco em segurança, performance e boas práticas de engenharia de software.

---

## 🚀 Visão Geral

O sistema gerencia todo o fluxo de dados do e-commerce Ikommercy. A aplicação foi construída com **Java e Spring Boot**, utilizando uma arquitetura de banco de dados híbrida para otimizar o tempo de resposta e garantir a segurança das sessões dos usuários.

## 🛠️ Tecnologias Utilizadas

* **Linguagem & Framework:** Java + Spring Boot
* **Banco de Dados Relacional:** MySQL (Persistência estruturada para usuários, produtos, pedidos, fornecedores, etc.)
* **Banco de Dados Em Memória:** Redis (Gerenciamento de sessão e versionamento de tokens de alta performance)
* **Segurança:** Spring Security + JWT (JSON Web Tokens)

## 🛡️ Segurança e Autenticação

O sistema conta com uma camada robusta de proteção, implementando:
* **Autenticação JWT:** Emissão e validação segura da identidade do usuário.
* **Estratégia de Blacklisting (Redis):** Controle rigoroso de tokens ativos através de versionamento, permitindo a invalidação instantânea e segura de sessões.
* **Controle de Acesso (RBAC):** Verificação de cargos (Roles) e permissões granulares para proteger as rotas da API.

---

## 🌐 Ecossistema do Projeto

Acesse os outros componentes do projeto Ikommercy e nossa documentação técnica:

* 💻 **[Repositório Front-End](https://github.com/K4uePinheiro/Ikommercy)** - Interface de usuário do e-commerce.
* 📖 **[Site da Documentação (JavaDocs)](https://sam-umbra.github.io/dropship-apidocs/)** - Documentação completa de endpoints, classes e métodos.
* 🗄️ **[Repositório da Documentação](https://github.com/Sam-Umbra/dropship-apidocs)** - Código fonte da documentação.
