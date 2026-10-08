import pytest

from skill_extractor.services.llm_service import LLMService


SAMPLE_LLM_OUTPUT = """Here is the analysis of the CV:

TECHNICAL SKILLS:
- Python (Software Engineering)
- Docker (DevOps), Kubernetes (DevOps)

SOFT SKILLS:
- Communication (Interpersonal Skill)
- None

CERTIFICATIONS:
- None

LANGUAGES:
- English
- Greek

WORK EXPERIENCE:
- Senior Developer at Acme, Inc. (2020-2023)
"""


@pytest.fixture
def llm_service():
    return LLMService()


@pytest.fixture
def sample_llm_output():
    return SAMPLE_LLM_OUTPUT
