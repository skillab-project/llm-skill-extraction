# Base Image: Python 3.10 version
# "slim" variants are smaller and more secure for production.
FROM python:3.10-slim

# Set Working Directory: This creates a folder inside the container
# where the app code will reside.
WORKDIR /app

# Environment Variables:
# PYTHONDONTWRITEBYTECODE: Prevents Python from writing .pyc files to disc
# PYTHONUNBUFFERED: Ensures logs are flushed directly to terminal
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1
ENV PYTHONPATH=/app

# Install Dependencies:
# Copy requirements.txt first to leverage Docker's caching mechanism.
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy Application Code:
# Copy the code into a subdirectory named 'skill_extractor'.
COPY . ./skill_extractor

# Expose Port: Inform Docker that the app listens on port 8000
EXPOSE 8000

# Run Command: Start the FastAPI server using Uvicorn
CMD ["uvicorn", "skill_extractor.controllers.skill_extraction_api:app", "--host", "0.0.0.0", "--port", "8000"]