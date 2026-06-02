import requests
import os
from typing import Dict, List, Any

class LLMService:
    """
    Service responsible for orchestrating interactions with the Local Large Language Model (LLM) via Ollama.

    It handles:
    1. Prompt Construction (Prompt Engineering).
    2. API Communication (HTTP Requests).
    3. Response Parsing (Post-processing raw text into structured JSON).
    """

    # Configuration for the inference engine
    # The specific model identifier loaded in Ollama
    #MODEL = "llama3.1"
    API_URL = os.getenv("API_URL")
    API_TOKEN = os.getenv("API_TOKEN")
    MODEL = os.getenv("MODEL_NAME")
    
    # Required headers from your example
    HEADERS = {
        "Authorization": f"Bearer {API_TOKEN}",
        "Accept": "application/json",
        "Content-Type": "application/json",
        "User-Agent": "SKILLAB-KPI-Recs/1.0",
    }

        


    def extract_skills(self, cv_text: str) -> Dict[str, List[str]]:
        """
        Constructs a prompt with the CV content and sends a generation request to the LLM.

        :param cv_text: The raw text extracted from the CV file.
        :return: A dictionary containing categorized skills (Technical, Soft, Certifications, Languages).
        :raises Exception: If the connection to Ollama fails or the model returns an error.
        """

        # --- PROMPT ENGINEERING ---
        # We guide the model towards a specific output format.
        prompt = f"""You are an expert HR Recruiter. Your task is to extract skills from a CV/Resume for ANY profession.

CV Text:
{cv_text}

---
INSTRUCTIONS:
1. Analyze the text to understand the candidate's profession (e.g., Chef, Developer, Nurse, Driver).
2. Extract ALL relevant skills based on that profession.
3. CONTEXTUALIZATION (CRITICAL): For every Technical and Soft Skill, you MUST append a short 2-3 word context or industry sector in parentheses to clarify its meaning. (Do NOT use commas inside the parentheses).

CATEGORIES TO EXTRACT:

A. TECHNICAL SKILLS (HARD SKILLS):
   - Look for: Industry-specific knowledge, Tools, Machinery, Equipment, Methodologies, Regulations, Standards, Software, Hardware.
   - Examples (Notice the contextual parentheses): 
     * For a Chef: "Knife skills (Culinary Arts)", "HACCP (Food Safety)", "Sous-vide (Cooking Method)".
     * For a Dev: "Python (Software Engineering)", "AWS (Cloud Computing)", "React (Frontend Web Library)".
     * For a Driver: "Forklift operation (Logistics)", "Defensive driving (Transportation)".

B. SOFT SKILLS:
   - Look for: Interpersonal skills, Leadership, Character traits, Problem-solving, Time management.
   - Examples: "Communication (Interpersonal Skill)", "Time management (Personal Organization)".

C. CERTIFICATIONS:
   - Look for: Degrees, Licenses, Awards, Official Certificates.

D. LANGUAGES:
   - Look for: Spoken/Written languages.
   
E. WORK EXPERIENCE:
   - Look for: Previous job roles, companies, and dates.
   - Format strictly as: "Job Title at Company Name (Dates)"
   - Example: "Senior Developer at Google (2020-2023)" or "Head Chef at Hilton (2018-Present)"

---
FORMAT YOUR ANSWER EXACTLY LIKE THIS:

TECHNICAL SKILLS:
- skill 1 (Context/Sector)
- skill 2 (Context/Sector)

SOFT SKILLS:
- skill 1 (Context/Sector)
- skill 2 (Context/Sector)

CERTIFICATIONS:
- cert 1

LANGUAGES:
- language 1

WORK EXPERIENCE:
- Job Title at Company (Dates)
- Job Title at Company (Dates)

If a category has no skills, write "- None"
Do NOT write sentences like "No programming skills found". Just list the skills found.
"""

        # Configuration payload for the inference parameters
        payload = {
            "model": self.MODEL,
            "messages": [
                {"role": "user", "content": prompt}
            ],
            "temperature": 0.1,
            "seed": 42
        }

        try:
            print(f"Sending request to remote API ({self.MODEL})...")

            # Use the /api/chat/completions endpoint as per your example
            url = f"{self.API_URL}/api/chat/completions"
            
            response = requests.post(
                url, 
                headers=self.HEADERS, 
                json=payload, 
                timeout=300
            )
            response.raise_for_status()

            # --- UPDATED RESPONSE PARSING ---
            # Extract content from the chat message structure
            outer_result = response.json()
            raw_response = outer_result["choices"][0]["message"]["content"]

            # Use your original parsing logic on the returned text
            return self._parse_skills(raw_response)

        except Exception as e:
            print(f"Error calling LLM Service: {e}")
            raise e


    def _parse_skills(self, raw_text: str) -> Dict[str, List[str]]:
        """
        Parses the unstructured raw text response from the LLM into a structured dictionary.

        It utilizes a state-machine approach to detect categories and extract list items.

        :param raw_text: The string response generated by the LLM.
        :return: A structured dictionary with lists of skills.
        """
        skills = {
            'technical': [],
            'soft': [],
            'certifications': [],
            'languages': [],
            'experience': []
        }

        lines = raw_text.split('\n')
        current_category = None

        for line in lines:
            line = line.strip()

            # --- State Detection: Identify which category we are currently parsing ---
            if line.upper().startswith('TECHNICAL SKILL'):
                current_category = 'technical'
                continue
            elif line.upper().startswith('SOFT SKILL'):
                current_category = 'soft'
                continue
            elif line.upper().startswith('CERTIFICATION'):
                current_category = 'certifications'
                continue
            elif line.upper().startswith('LANGUAGES:'):
                current_category = 'languages'
                continue
            elif line.upper().startswith('WORK EXPERIENCE') or line.upper().startswith('EXPERIENCE'):
                current_category = 'experience'
                continue

            # --- Extraction Logic: Process list items ---
            if line.startswith('-') and current_category:
                skill = line.lstrip('- ').strip()

                # Filter out null-values generated by the model (e.g., "- None")
                if skill.lower() == 'none' or skill.lower().startswith('none ('):
                    continue

                # Logic: If it contains a comma AND it's NOT Experience, split it.
                # Experience often contains commas (e.g., "Manager, IT Dept"), so should NOT split that.

                should_split = (',' in skill) and (len(skill) < 50) and (current_category != 'experience')

                if should_split:
                    # Case: The model outputted "Python, Java, C++" on one line
                    individual_skills = [s.strip() for s in skill.split(',')]
                    for s in individual_skills:
                        if s:
                            skills[current_category].append(s)
                else:
                    # Case: Standard single line OR Experience line
                    skills[current_category].append(skill)

        return skills