from unittest.mock import MagicMock

import pytest
from fastapi.testclient import TestClient

from skill_extractor.controllers import skill_extraction_api as api

SKILLS = {
    "technical": ["Python (Software Engineering)", "Docker (DevOps)"],
    "soft": ["Teamwork (Interpersonal Skill)"],
    "certifications": [],
    "languages": ["English"],
    "experience": ["Developer at Acme (2020-2023)"],
}


@pytest.fixture
def client():
    return TestClient(api.app)


@pytest.fixture
def mock_llm(monkeypatch):
    mock = MagicMock(return_value=SKILLS)
    monkeypatch.setattr(api.llm_service, "extract_skills", mock)
    return mock


def _txt(content: bytes = b"Experienced Python developer"):
    return {"file": ("cv.txt", content, "text/plain")}


def test_extract_raw_skills(client, mock_llm):
    resp = client.post("/extract-skills/", files=_txt())

    assert resp.status_code == 200
    body = resp.json()
    assert body["filename"] == "cv.txt"
    assert body["status"] == "success"
    assert body["skills"] == SKILLS
    assert body["total_skills_found"] == 5
    mock_llm.assert_called_once_with("Experienced Python developer")


def test_unsupported_file_type_returns_400(client, mock_llm):
    files = {"file": ("photo.png", b"\x89PNG", "image/png")}
    resp = client.post("/extract-skills/", files=files)

    assert resp.status_code == 400
    assert "Unsupported file type" in resp.json()["detail"]
    mock_llm.assert_not_called()


def test_normalize_sends_flat_skill_list(client, mock_llm, monkeypatch):
    normalizer_response = MagicMock()
    normalizer_response.raise_for_status.return_value = None
    normalizer_response.json.return_value = {"results": [], "total_count": 0}
    mock_post = MagicMock(return_value=normalizer_response)
    monkeypatch.setattr(api.requests, "post", mock_post)

    resp = client.post(
        "/extract-skills/",
        params={"normalize": "true", "only_matched": "true"},
        files=_txt(),
    )

    assert resp.status_code == 200
    body = resp.json()
    assert body["raw_extracted_skills"] == SKILLS
    assert body["esco_normalized_skills"] == {"results": [], "total_count": 0}

    url = mock_post.call_args.args[0]
    payload = mock_post.call_args.kwargs["json"]
    assert url == api.NORMALIZER_URL
    assert payload["only_matched"] is True
    assert payload["skills"] == [s for v in SKILLS.values() for s in v]


def test_llm_failure_returns_500(client, monkeypatch):
    monkeypatch.setattr(
        api.llm_service, "extract_skills", MagicMock(side_effect=RuntimeError("LLM down"))
    )
    resp = client.post("/extract-skills/", files=_txt())

    assert resp.status_code == 500
    assert "LLM down" in resp.json()["detail"]
