# CV Skill Extraction & Normalization System

A microservices-based architecture for extracting professional skills from Resumes (PDF) using Generative AI (Llama 3.1) and normalizing them to the European Skills taxonomy using Semantic Search (Sentence-BERT).

## 🚀 System Architecture

The project consists of three Dockerized services:
1.  **CV Extractor Service (FastAPI):** Orchestrates file processing, text extraction, and communicates with other services.
2.  **ESCO Normalizer Service (FastAPI):** A semantic search engine that maps raw skills to ESCO concepts using Vector Embeddings.
3.  **Inference Engine (Ollama):** Hosts the Llama 3.1 Large Language Model locally.

---

## 🛠 Prerequisites

Before running the project, ensure you have the following installed:

* **Docker Desktop** (or Docker Engine + Compose plugin)
    * [Download Docker](https://www.docker.com/products/docker-desktop/)
* **Git** (Optional, for cloning the repository)

> **Note:** The system requires approximately **8GB of RAM** and **6GB of free disk space** to run smoothly, as it downloads and hosts the Llama 3.1 AI model locally.

---

## 📥 Installation & Setup

1.  **Clone the repository** (or unzip the project folder):


2.  **Verify Directory Structure:**
    Ensure you are in the root directory containing `docker-compose.yml`. The structure should look like this:
    ```
    /project-root
    ├── docker-compose.yml
    ├── README.md
    ├── skill_extractor/      # Service 1 Source Code
    └── skill_normalizer/     # Service 2 Source Code
    ```

---

## ▶️ How to Run

1.  **Start the Services:**
    Open your terminal/command prompt in the project root and run:
    ```bash
    docker-compose up --build
    ```

2.  **Wait for Initialization:**
    * The first launch will take a few minutes.
    * **Important:** The `cv_ollama` container will automatically download the **Llama 3.1 model (approx. 4.7GB)**.
    * Watch the logs for the message: `>>> Model is ready for inference!`.
    * Once you see `Application startup complete` for both API services, the system is ready.

---

## 🔌 API Documentation (Usage)

Once the containers are running, you can access the services via their Swagger UI endpoints:

### 1. Main Entry Point (CV Extractor)
* **URL:** http://localhost:8000/docs
* **Function:** Upload PDF resumes here.

**How to test:**
1.  Open the URL above.
2.  Locate the `POST /extract-skills/` endpoint.
3.  Click **Try it out**.
4.  Set parameters:
    * `normalize`: **true** (Enables ESCO mapping)
    * `only_matched`: **true** (Filters out non-standard skills)
5.  Upload a PDF file.
6.  Click **Execute**.

### 2. ESCO Normalizer (Semantic Search)
* **URL:** http://localhost:8001/docs
* **Function:** Internal service for vector similarity search (can be tested independently).

---

## 🛑 Stopping the System

To stop the containers and free up resources, press `Ctrl+C` in the terminal, or run:

```bash
docker-compose down