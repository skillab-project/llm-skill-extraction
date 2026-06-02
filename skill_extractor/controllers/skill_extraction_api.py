from fastapi import FastAPI, UploadFile, File, HTTPException, Form
from fastapi.responses import JSONResponse
from typing import BinaryIO
import io
import os
import requests

# Import project modules
from skill_extractor.data_handlers.reader_factory import ReaderFactory
from skill_extractor.services.llm_service import LLMService

# --- Application Initialization ---
# Initialize the FastAPI application with metadata for Swagger UI documentation.
app = FastAPI(
    title="CV Skill Extractor Service",
    description="A microservice that accepts CV documents, extracts text and identifies professional skills using GenAI.",
    version="1.0.0"
)

# --- Service Instantiation ---
# Instantiate the LLM Service once (Singleton pattern) to avoid reloading the model on every request.
llm_service = LLMService()

# Retrieve the Normalizer Service URL from Docker environment variables (with a default fallback)
NORMALIZER_URL = os.getenv("NORMALIZER_URL", "http://esco-api:8001/normalize")


@app.post(
    "/extract-skills/",
    summary="Process CV and Extract Skills",
    description="Orchestrates the pipeline: File Upload -> Text Extraction -> LLM Analysis -> Optional ESCO Normalization."
)
async def extract_skills_endpoint(
        file: UploadFile = File(...),
        normalize: bool = False,      # Flag to enable/disable ESCO standardization
        only_matched: bool = False    # Flag to filter out skills that do not match the ESCO taxonomy
):
    """
    Main endpoint for CV processing.

    Args:
        file (UploadFile): The resume file (PDF, DOCX, etc.).
        normalize (bool): If True, triggers a request to the Normalizer Service.
        only_matched (bool): If True, filters the output to show only ESCO-verified skills.

    Returns:
        JSONResponse: Structured data containing raw and/or normalized skills.
    """
    try:
        # ---------------------------------------------------------
        # File Ingestion & Text Extraction
        # ---------------------------------------------------------
        # Select the appropriate reader based on the file's MIME type using the Factory Pattern.
        reader = ReaderFactory.get_reader(file.content_type)

        # Read file content into memory buffer
        contents: bytes = await file.read()
        file_buffer: BinaryIO = io.BytesIO(contents)

        # Extract text content
        extracted_text: str = reader.read(file_buffer)

        # Validation: Check if text was extracted successfully
        if not extracted_text or not extracted_text.strip():
            raise HTTPException(
                status_code=400,
                detail="Could not extract readable text from the file. The file might be empty or scanned images."
            )

        # ---------------------------------------------------------
        # Extract Raw Skills using Llama 3.1 (Ollama)
        # ---------------------------------------------------------
        # The text is sent to the LLM service to be parsed into JSON categories (Technical, Soft, etc.)
        skills_data = llm_service.extract_skills(extracted_text)

#test <
        print("skills_data: ", skills_data)

#test end >



        # ---------------------------------------------------------
        # ESCO Normalization (Optional)
        # ---------------------------------------------------------
        if normalize:
            try:
                # Data Transformation: Flatten the categorized dictionary into a single list of strings
                # This is required because the Normalizer service expects a flat list of skills.
                flat_skills_list = []
                for category, skills_list in skills_data.items():
                    if isinstance(skills_list, list):
                        flat_skills_list.extend(skills_list)

                # Payload Construction: Prepare data for the inter-service request
                payload = {
                    "skills": flat_skills_list,
                    "only_matched": only_matched  # Pass the user's filtering preference
                }

                # Inter-Service Communication: Send POST request to the ESCO API container
                response = requests.post(NORMALIZER_URL, json=payload)
                response.raise_for_status() # Raise exception for 4xx/5xx errors

                # Parse the response from the Normalizer service
                normalized_skills = response.json()

                # Return combined response (Raw + Normalized)
                return JSONResponse(content={
                    "filename": file.filename,
                    "status": "success",
                    "raw_extracted_skills": skills_data,
                    "esco_normalized_skills": normalized_skills
                })

            except requests.exceptions.RequestException as e:
                # Handle connection errors between microservices
                print(f"Normalizer API Error: {e}")
                raise HTTPException(
                    status_code=502,
                    detail=f"Skills extracted, but failed to connect to ESCO Normalizer: {str(e)}"
                )

        # ---------------------------------------------------------
        # Default Response (Raw Skills Only)
        # ---------------------------------------------------------
        return JSONResponse(content={
            "filename": file.filename,
            "status": "success",
            "total_skills_found": sum(len(v) for v in skills_data.values() if isinstance(v, list)),
            "skills": skills_data
        })

    except ValueError as ve:
        # Handle errors related to unsupported file types
        raise HTTPException(status_code=400, detail=str(ve))

    except Exception as e:
        # Catch-all for unexpected server errors
        print(f"Server Error: {e}")
        raise HTTPException(status_code=500, detail=f"Processing failed: {str(e)}")