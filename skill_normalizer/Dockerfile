# Base Image: Python 3.10 version
# "slim" variants are smaller and more secure for production.
FROM python:3.10-slim

# Set Working Directory
# All subsequent commands will be run from this folder inside the container.
WORKDIR /app

# Environment Variables
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1
ENV PYTHONPATH=/app

# Install Dependencies
# Copy requirements first to leverage Docker's layer caching mechanism.
COPY requirements.txt .

RUN pip install --no-cache-dir --upgrade pip && \
    pip install --no-cache-dir torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cpu && \
    pip install --no-cache-dir -r requirements.txt

# Model Pre-loading
# Run a Python command during the build to download the AI model.
# This ensures the model files are stored INSIDE the Docker image.
# When the container starts later, it won't need internet access to fetch the model.
RUN python -c "from sentence_transformers import SentenceTransformer; SentenceTransformer('all-MiniLM-L6-v2')"

# Copy Application Code
# Copy the code into a subdirectory named 'skill_normalizer'.
COPY . ./skill_normalizer

# Expose Port
# Inform Docker that the application listens on port 8001.
EXPOSE 8001

# Startup Command
# Use uvicorn to run the FastAPI server.
CMD ["uvicorn", "skill_normalizer.controllers.normalize_skills_api:app", "--host", "0.0.0.0", "--port", "8001"]