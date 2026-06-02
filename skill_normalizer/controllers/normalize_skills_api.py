from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
from typing import List

# Import the core service logic
from skill_normalizer.services.normalization_service import Normalizer

# --- Application initialization ---
# Initialize the FastAPI application with metadata used for the Swagger UI documentation.
app = FastAPI(
    title="ESCO Skill Normalizer Service",
    description="A semantic search microservice utilizing Sentence Transformers to map raw CV skills to the ESCO standard taxonomy.",
    version="1.3.0"
)

# --- Middleware configuration ---
# Configure Cross-Origin Resource Sharing (CORS) to allow requests from the Extractor Service 
# and potentially other clients.
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# --- Data Transfer Objects (DTOs) ---
class SkillRequest(BaseModel):
    """
    Defines the structure of the input payload using Pydantic for validation.
    """
    skills: List[str] = Field(
        ...,
        example=["Python", "React", "Team Leadership"],
        description="A list of raw skill strings extracted from the candidate's Resume/CV."
    )
    only_matched: bool = Field(
        False,
        description="Boolean flag. If set to True, the response filters out unmatched skills (high precision mode)."
    )

# --- Global State Management ---
# Uses a global variable to hold the Normalizer instance.
# This ensures the heavy AI model (MiniLM) is loaded into memory only once (Singleton pattern).
service = None

@app.on_event("startup")
def startup_event():
    """
    System Bootstrap Handler.
    This function runs automatically when the Docker container starts.
    It initializes the AI Inference Engine and builds the Vector Index.
    """
    global service
    print(">>> System Startup: Initializing AI Inference Engine...")

    try:
        # Initialize the Normalizer service.
        # Do not pass a file path argument here; rely on the Normalizer class
        # to dynamically resolve the absolute path of the 'esco_light.json' dataset.
        service = Normalizer()

        if service.model is None:
            print("WARNING: Model did not load correctly. Check logs.")
        else:
            print(">>> System Ready: AI Model loaded and Vector Index built successfully.")

    except Exception as e:
        print(f"CRITICAL ERROR: Failed to initialize service. Reason: {e}")

# --- API ENDPOINTS ---

@app.get("/")
def health_check():
    """
    Liveness probe to verify the service is running.
    """
    return {
        "status": "online",
        "service": "ESCO Skill Normalizer",
        "version": "1.3.0",
        "documentation": "/docs"
    }

@app.post("/normalize")
def normalize_skills(request: SkillRequest):
    """
    Main API Endpoint: POST /normalize
    
    Accepts a list of raw skills and performs semantic matching against the ESCO database.
    
    Args:
        request (SkillRequest): JSON body containing 'skills' list and 'only_matched' flag.
        
    Returns:
        JSON object containing the matched ESCO concepts and similarity scores.
    """
    # Service Availability Check
    if service is None:
        raise HTTPException(
            status_code=503,
            detail="Service is not initialized. Please check server logs."
        )

    skills_list = request.skills
    only_matched = request.only_matched

    # Input Validation
    if not skills_list:
        return {"error": "Invalid Input. The 'skills' list cannot be empty."}

    # Business Logic Delegation: Perform semantic search
    results = service.normalize(skills_list)

    # Conditional Response Filtering
    # If the client requested 'only_matched', filter out results that did not meet the threshold.
    if only_matched:
        filtered_results = [
            item for item in results
            if item.get("esco_preferredLabel") is not None
        ]
        return {
            "results": filtered_results,
            "total_count": len(filtered_results),
            "filter_applied": True
        }

    # Default Response: Return all results (including potential non-matches for debugging context)
    return {
        "results": results,
        "total_count": len(results),
        "filter_applied": False
    }