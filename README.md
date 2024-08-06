# ArchWorker Project

## Overview

ArchWorker is a comprehensive code generation project designed to dynamically create full-stack applications based on user-defined configurations. The core application, built using Java and Angular, facilitates the creation of backend and frontend code for various frameworks and languages such as Node.js, React, Angular, Python, and Laravel. The architecture is plugin-based to support different applications, enabling users to generate customized applications on the fly.

## Project Architecture

The ArchWorker project consists of several key components:

1. **Core Application**: A monolithic application built with Java and Angular that collects user inputs and orchestrates the code generation process.
2. **Microservices**: Responsible for generating backend and frontend code for various languages and frameworks. Each microservice handles specific parts of the generation process.
3. **Natural Language Processing (NLP) Module**: Enhances user interactions by interpreting and processing natural language inputs to generate project specifications.
4. **Message Queue**: Kafka is used for task management and communication between microservices.
5. **Shared Storage**: A NAS (Network Attached Storage) system is used to store generated code zips, which are later combined and provided to the user.
6. **Mixer Service**: Combines backend and frontend code zips into a final, ready-to-use zip file.
7. **User Interface**: An Angular frontend where users can input their project requirements.

## Core Application Workflow

1. **User Input**: Users provide their project requirements through a web form in the Angular frontend. The NLP module can process natural language inputs to extract these requirements.
2. **Task Publication**: The core application publishes tasks to a message queue (Kafka) for generating backend and frontend code.
3. **Code Generation**:
   - **Backend Microservice**: Listens for tasks from the queue, generates backend code, saves the generated zip to the NAS, and notifies the mixer service.
   - **Frontend Microservice**: Listens for tasks from the queue, generates frontend code, saves the generated zip to the NAS, and notifies the mixer service.
4. **Combining Code**: The mixer microservice listens for notifications from backend and frontend services, fetches the zips from the NAS, combines them, saves the final zip to the NAS, and notifies the core application.
5. **Download Link**: The core application provides the user with a download link to the final zip.

## Detailed Component Description

### Core Application

The core application serves as the central point for collecting user inputs and managing the code generation process. It comprises:

- **Frontend**: An Angular application that provides a user-friendly interface for inputting project requirements.
- **Backend**: A Java application that processes user inputs, publishes tasks to Kafka, and handles notifications from the mixer service.

### Natural Language Processing (NLP) Module

The NLP module is an integral part of the ArchWorker system, allowing users to describe their project requirements in natural language. It includes:

- **Intent Recognition**: Understanding user intents from natural language input to accurately map requirements to application features.
- **Entity Extraction**: Identifying specific entities (e.g., database models, frontend components) from user descriptions.
- **Contextual Understanding**: Using context to resolve ambiguities in user inputs, providing more accurate and tailored code generation.

### Microservices

Each microservice is dedicated to generating code for a specific framework or language. The services include:

- **Java Microservice**: Generates backend code for Java applications.
- **Node.js Microservice**: Generates backend code for Node.js applications.
- **Python Microservice**: Generates backend code for Python applications.
- **Laravel Microservice**: Generates backend code for Laravel applications.
- **React Microservice**: Generates frontend code for React applications.
- **Angular Microservice**: Generates frontend code for Angular applications.

### Message Queue (Kafka)

Kafka is used for managing tasks and facilitating communication between the core application and the microservices. It ensures tasks are distributed and processed efficiently.

### Shared Storage (NAS)

A Network Attached Storage (NAS) system is used to store generated code zips. It allows the mixer service to access and combine these zips into a final, cohesive zip file.

### Mixer Service

The mixer service is responsible for combining the backend and frontend code zips into a single, ready-to-use zip file. It ensures that the generated code is correctly integrated and functional.

### User Interface

The Angular frontend of the core application provides users with a form to input their project requirements. It guides users through the process of selecting features, authentication methods, and other project-specific configurations. With the addition of NLP, users can now input project specifications in natural language, simplifying the process further.

## Development Approach

The development of ArchWorker follows a Test-Driven Development (TDD) approach, ensuring high-quality, reliable code. The project is designed to be flexible and scalable, allowing for the addition of new microservices and features as needed.

## Tasks

All tasks are regularly updated on the [ArchWorker Task Board](https://docs.google.com/spreadsheets/d/10xZQibyyRYsbtsZLF5KcgyfrZdzFlQxIX3jD_wkR2MU/edit?usp=sharing).

## Future Enhancements

Planned enhancements for the ArchWorker project include:

- Adding more microservices to support additional languages and frameworks.
- Implementing a more sophisticated templating engine for code generation.
- Enhancing the NLP module to better understand complex user inputs and support more languages.
- Enhancing the user interface to provide more customization options.
- Improving the mixer service to handle more complex integration scenarios.

## Conclusion

ArchWorker aims to simplify and automate the process of full-stack application development. By leveraging a plugin-based architecture, NLP capabilities, and a robust microservices approach, it provides a powerful tool for developers to quickly generate customized applications based on their specific requirements.
