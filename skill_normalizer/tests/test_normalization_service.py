import json

import pytest

from skill_normalizer.services.normalization_service import Normalizer

RESULT_KEYS = {
    "input_skill",
    "similarity_score",
    "esco_preferredLabel",
    "esco_description",
    "esco_uri",
    "esco_alt_labels",
}


# ---------------------------------------------------------------------------
# Initialisation / data loading
# ---------------------------------------------------------------------------

def test_loads_records_and_builds_index(normalizer):
    assert len(normalizer.data_records) == 3
    assert len(normalizer.search_labels) == 3
    assert normalizer.model is not None
    assert normalizer.stored_embeddings.shape[0] == 3


def test_search_label_combines_label_altlabels_and_description(normalizer):
    assert normalizer.search_labels[0] == (
        "Python python programming python language py cpython computer programming in python"
    )


def test_string_altlabels_are_tolerated(normalizer):
    assert "baking bread" in normalizer.search_labels[2]


def test_dict_shaped_dataset_is_converted_to_list(fake_model, tmp_path):
    path = tmp_path / "dict.json"
    path.write_text(json.dumps({"a": {"preferredLabel": "Python"}, "b": {"preferredLabel": "Java"}}))
    n = Normalizer(json_path=str(path))
    assert [r["preferredLabel"] for r in n.data_records] == ["Python", "Java"]


def test_missing_dataset_leaves_model_unloaded(fake_model, tmp_path):
    n = Normalizer(json_path=str(tmp_path / "does_not_exist.json"))
    assert n.model is None
    assert n.data_records == []
    assert n.normalize(["Python"]) == []


def test_corrupt_dataset_leaves_model_unloaded(fake_model, tmp_path):
    path = tmp_path / "broken.json"
    path.write_text("{ not valid json")
    n = Normalizer(json_path=str(path))
    assert n.model is None
    assert n.normalize(["Python"]) == []


def test_empty_dataset_has_no_embeddings(fake_model, tmp_path):
    path = tmp_path / "empty.json"
    path.write_text("[]")
    n = Normalizer(json_path=str(path))
    assert n.stored_embeddings is None
    assert n.normalize(["Python"]) == []


# ---------------------------------------------------------------------------
# normalize()
# ---------------------------------------------------------------------------

def test_normalize_empty_input_returns_empty_list(normalizer):
    assert normalizer.normalize([]) == []


def test_normalize_matches_closest_esco_skill(normalizer):
    [result] = normalizer.normalize(["Python programming"])

    assert set(result) == RESULT_KEYS
    assert result["input_skill"] == "Python programming"
    assert result["esco_preferredLabel"] == "Python"
    assert result["esco_description"] == "computer programming in python"
    assert result["esco_uri"] == "http://data.europa.eu/esco/skill/python"
    assert result["similarity_score"] >= 0.47


def test_normalize_truncates_alt_labels_to_three(normalizer):
    [result] = normalizer.normalize(["Python programming"])
    assert result["esco_alt_labels"] == ["python programming", "python language", "py"]


def test_normalize_missing_uri_defaults_to_empty_string(normalizer):
    [result] = normalizer.normalize(["manage music staff"])
    assert result["esco_preferredLabel"] == "manage musical staff"
    assert result["esco_uri"] == ""


def test_normalize_unmatched_skill_has_null_fields(normalizer):
    [result] = normalizer.normalize(["underwater basket weaving"])

    assert result["input_skill"] == "underwater basket weaving"
    assert result["esco_preferredLabel"] is None
    assert result["esco_description"] is None
    assert result["esco_uri"] is None
    assert result["esco_alt_labels"] == []
    assert result["similarity_score"] < 0.47


def test_normalize_returns_one_result_per_input_in_order(normalizer):
    skills = ["bake bread", "underwater basket weaving", "Python"]
    results = normalizer.normalize(skills)
    assert [r["input_skill"] for r in results] == skills


@pytest.mark.parametrize("threshold, matched", [(0.0, True), (0.99, False)])
def test_normalize_respects_threshold(normalizer, threshold, matched):
    [result] = normalizer.normalize(["python developer"], threshold=threshold)
    assert (result["esco_preferredLabel"] is not None) is matched


def test_similarity_score_is_rounded_float(normalizer):
    [result] = normalizer.normalize(["Python"])
    assert isinstance(result["similarity_score"], float)
    assert result["similarity_score"] == round(result["similarity_score"], 2)
