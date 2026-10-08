"""
Shared fixtures for the normalizer tests.

The real SentenceTransformer model (~90 MB, needs torch) is replaced by a tiny
deterministic bag-of-words encoder, so the tests run offline and in milliseconds.
"""
import json
import re
import sys
import types
import zlib

import numpy as np
import pytest

# If sentence-transformers is not installed, register a stub module so that
# `normalization_service` can still be imported. The class is patched per test anyway.
try:
    import sentence_transformers  # noqa: F401
except ImportError:
    stub = types.ModuleType("sentence_transformers")
    stub.SentenceTransformer = object
    sys.modules["sentence_transformers"] = stub

from skill_normalizer.services import normalization_service  # noqa: E402

DIM = 256


class FakeSentenceTransformer:
    """Hashes each lowercase word into a bucket -> similar wording gives high cosine."""

    def __init__(self, model_name=None):
        self.model_name = model_name

    def encode(self, texts):
        vectors = np.zeros((len(texts), DIM), dtype=np.float32)
        for row, text in enumerate(texts):
            for token in re.findall(r"[a-z0-9+#]+", text.lower()):
                vectors[row, zlib.crc32(token.encode()) % DIM] += 1.0
        return vectors


ESCO_RECORDS = [
    {
        "preferredLabel": "Python",
        "altLabels": ["python programming", "python language", "py", "cpython"],
        "description": "computer programming in python",
        "conceptUri": "http://data.europa.eu/esco/skill/python",
    },
    {
        "preferredLabel": "manage musical staff",
        "altLabels": ["manage music staff"],
        "description": "assign and manage staff tasks in music",
    },
    {
        "preferredLabel": "bake bread",
        "altLabels": "baking bread",  # string instead of list: must be tolerated
        "description": "prepare dough and bake bread loaves",
    },
]


@pytest.fixture
def fake_model(monkeypatch):
    monkeypatch.setattr(normalization_service, "SentenceTransformer", FakeSentenceTransformer)
    return FakeSentenceTransformer


@pytest.fixture
def esco_json(tmp_path):
    path = tmp_path / "esco_test.json"
    path.write_text(json.dumps(ESCO_RECORDS), encoding="utf-8")
    return path


@pytest.fixture
def normalizer(fake_model, esco_json):
    return normalization_service.Normalizer(json_path=str(esco_json))
