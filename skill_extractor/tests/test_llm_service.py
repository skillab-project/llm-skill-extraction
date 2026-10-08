from unittest.mock import MagicMock

import pytest
import requests

from skill_extractor.services import llm_service as llm_module

EXPECTED_KEYS = {"technical", "soft", "certifications", "languages", "experience"}


# ---------------------------------------------------------------------------
# _parse_skills
# ---------------------------------------------------------------------------

def test_parse_returns_all_categories_for_empty_text(llm_service):
    result = llm_service._parse_skills("")
    assert set(result) == EXPECTED_KEYS
    assert all(v == [] for v in result.values())


def test_parse_full_sample(llm_service, sample_llm_output):
    result = llm_service._parse_skills(sample_llm_output)

    assert result["technical"] == [
        "Python (Software Engineering)",
        "Docker (DevOps)",
        "Kubernetes (DevOps)",
    ]
    assert result["soft"] == ["Communication (Interpersonal Skill)"]
    assert result["certifications"] == []
    assert result["languages"] == ["English", "Greek"]
    assert result["experience"] == ["Senior Developer at Acme, Inc. (2020-2023)"]


def test_parse_ignores_bullets_before_any_header(llm_service):
    text = "- stray item\nTECHNICAL SKILLS:\n- Java"
    assert llm_service._parse_skills(text)["technical"] == ["Java"]


def test_parse_headers_are_case_insensitive(llm_service):
    text = "technical skills:\n- Go\nsoft skills:\n- Teamwork"
    result = llm_service._parse_skills(text)
    assert result["technical"] == ["Go"]
    assert result["soft"] == ["Teamwork"]


@pytest.mark.parametrize("null_value", ["None", "none", "- None", "None (not found)"])
def test_parse_filters_none_values(llm_service, null_value):
    text = f"LANGUAGES:\n- {null_value}"
    assert llm_service._parse_skills(text)["languages"] == []


def test_parse_does_not_split_long_comma_lines(llm_service):
    long_skill = "Designing, implementing and maintaining large distributed systems at scale"
    assert len(long_skill) >= 50
    text = f"TECHNICAL SKILLS:\n- {long_skill}"
    assert llm_service._parse_skills(text)["technical"] == [long_skill]


def test_parse_skips_empty_parts_when_splitting(llm_service):
    text = "TECHNICAL SKILLS:\n- C, , C++,"
    assert llm_service._parse_skills(text)["technical"] == ["C", "C++"]


def test_parse_experience_short_header(llm_service):
    text = "EXPERIENCE:\n- Chef at Hilton (2018-Present)"
    assert llm_service._parse_skills(text)["experience"] == ["Chef at Hilton (2018-Present)"]


def test_parse_ignores_non_bullet_lines(llm_service):
    text = "TECHNICAL SKILLS:\nThe candidate knows:\n- SQL\n* Rust"
    assert llm_service._parse_skills(text)["technical"] == ["SQL"]


# ---------------------------------------------------------------------------
# extract_skills (HTTP call mocked)
# ---------------------------------------------------------------------------

def _fake_response(content: str) -> MagicMock:
    response = MagicMock()
    response.raise_for_status.return_value = None
    response.json.return_value = {"choices": [{"message": {"content": content}}]}
    return response


def test_extract_skills_calls_chat_endpoint_and_parses(monkeypatch, llm_service, sample_llm_output):
    mock_post = MagicMock(return_value=_fake_response(sample_llm_output))
    monkeypatch.setattr(llm_module.requests, "post", mock_post)

    result = llm_service.extract_skills("Some CV text about Python")

    mock_post.assert_called_once()
    url = mock_post.call_args.args[0]
    payload = mock_post.call_args.kwargs["json"]
    assert url == f"{llm_service.API_URL}/chat/completions"
    assert payload["model"] == llm_service.MODEL
    assert payload["messages"][0]["role"] == "user"
    assert "Some CV text about Python" in payload["messages"][0]["content"]
    assert result["languages"] == ["English", "Greek"]


def test_extract_skills_propagates_http_errors(monkeypatch, llm_service):
    response = MagicMock()
    response.raise_for_status.side_effect = requests.HTTPError("500 Server Error")
    monkeypatch.setattr(llm_module.requests, "post", MagicMock(return_value=response))

    with pytest.raises(requests.HTTPError):
        llm_service.extract_skills("cv")


def test_extract_skills_raises_on_malformed_response(monkeypatch, llm_service):
    response = MagicMock()
    response.raise_for_status.return_value = None
    response.json.return_value = {"unexpected": "shape"}
    monkeypatch.setattr(llm_module.requests, "post", MagicMock(return_value=response))

    with pytest.raises(KeyError):
        llm_service.extract_skills("cv")
