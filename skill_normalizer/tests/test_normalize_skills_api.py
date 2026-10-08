from unittest.mock import MagicMock

import pytest
from fastapi.testclient import TestClient

from skill_normalizer.controllers import normalize_skills_api as api

MATCHED = {
    "input_skill": "Python",
    "similarity_score": 0.91,
    "esco_preferredLabel": "Python",
    "esco_description": "computer programming in python",
    "esco_uri": "",
    "esco_alt_labels": [],
}
UNMATCHED = {
    "input_skill": "Juggling",
    "similarity_score": 0.12,
    "esco_preferredLabel": None,
    "esco_description": None,
    "esco_uri": None,
    "esco_alt_labels": [],
}


@pytest.fixture
def client():
    # Not used as a context manager -> the startup event (which loads the real model) does not run.
    return TestClient(api.app)


@pytest.fixture
def fake_service(monkeypatch):
    service = MagicMock()
    service.normalize.return_value = [MATCHED, UNMATCHED]
    monkeypatch.setattr(api, "service", service)
    return service


def test_health_check(client):
    resp = client.get("/")
    assert resp.status_code == 200
    assert resp.json()["status"] == "online"


def test_returns_503_when_service_not_initialised(client, monkeypatch):
    monkeypatch.setattr(api, "service", None)
    resp = client.post("/normalize", json={"skills": ["Python"]})
    assert resp.status_code == 503


def test_returns_all_results_by_default(client, fake_service):
    resp = client.post("/normalize", json={"skills": ["Python", "Juggling"]})

    assert resp.status_code == 200
    body = resp.json()
    assert body["results"] == [MATCHED, UNMATCHED]
    assert body["total_count"] == 2
    assert body["filter_applied"] is False
    fake_service.normalize.assert_called_once_with(["Python", "Juggling"])


def test_only_matched_filters_out_unmatched(client, fake_service):
    resp = client.post("/normalize", json={"skills": ["Python", "Juggling"], "only_matched": True})

    body = resp.json()
    assert body["results"] == [MATCHED]
    assert body["total_count"] == 1
    assert body["filter_applied"] is True


def test_empty_skill_list_returns_error_message(client, fake_service):
    resp = client.post("/normalize", json={"skills": []})

    assert resp.status_code == 200
    assert "error" in resp.json()
    fake_service.normalize.assert_not_called()


def test_missing_skills_field_is_rejected(client, fake_service):
    resp = client.post("/normalize", json={"only_matched": True})
    assert resp.status_code == 422
