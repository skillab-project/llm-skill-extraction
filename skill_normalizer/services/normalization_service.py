import json
import os
import numpy as np
from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity

class Normalizer:
    """
    The Normalizer class encapsulates the logic for Semantic Skill Matching.
    It uses a pre-trained SBERT model to map input skills to the ESCO taxonomy
    based on vector space similarity.
    """

    def __init__(self, json_path: str = None):
        """
        Initializes the Normalizer service.
        1. Resolves the path to the knowledge base (ESCO dataset).
        2. Loads and enriches the data.
        3. Loads the AI model and pre-calculates vector embeddings (Indexing).
        """
        # --- Service State Initialization ---

        # In-memory repository for the loaded ESCO taxonomy dataset.
        # Acts as the reference database for retrieving metadata (URIs, descriptions) after a match is found.
        self.data_records = []

        # Pre-processed textual corpus (concatenated features: Label + AltLabels + Description).
        # This list serves as the source for generating the vector embeddings.
        self.search_labels = []

        # Instance of the SentenceTransformer model (Deep Learning Inference Engine).
        # Initialized as None and loaded lazily or during startup to manage memory usage.
        self.model = None

        # The cached Vector Index (Embedding Matrix).
        # Stores pre-computed vector representations of the ESCO skills to enable fast O(1) cosine similarity searches.
        self.stored_embeddings = None

        # --- Dynamic Path Resolution ---
        # Determine the absolute path of the dataset relative to the script's location.
        # This ensures the service works correctly inside Docker containers regardless of the working directory.
        if json_path is None:
            current_dir = os.path.dirname(os.path.abspath(__file__)) # Path to /services
            parent_dir = os.path.dirname(current_dir)                # Path to /skill_normalizer
            json_path = os.path.join(parent_dir, 'data', 'esco_light.json')

        print(f">>> System Startup: Looking for Knowledge Base at: {json_path}")

        # --- Data Ingestion Layer ---
        if os.path.exists(json_path):
            print(f"Loading Knowledge Base...")
            try:
                with open(json_path, 'r', encoding='utf-8') as f:
                    self.data_records = json.load(f)

                # Data Structure Normalization: Ensure we have a list of records
                if isinstance(self.data_records, dict):
                    self.data_records = list(self.data_records.values())

                # --- Feature Engineering: Context Enrichment ---
                # Combine 'preferredLabel', 'altLabels', and 'description' to create a
                # rich semantic text representation for each ESCO concept.
                for item in self.data_records:
                    p_label = item.get('preferredLabel', '')
                    alt_labels = item.get('altLabels', [])

                    if isinstance(alt_labels, list):
                        alt_text = " ".join(alt_labels)
                    else:
                        alt_text = str(alt_labels)

                    desc = item.get('description', '')

                    # Concatenate fields to maximize semantic context for the vectorizer
                    full_search_text = f"{p_label} {alt_text} {desc}"
                    self.search_labels.append(full_search_text)

                print(f"Loaded and enriched {len(self.data_records)} records.")

            except Exception as e:
                print(f"Failed to load dataset: {e}")
                return
        else:
            print(f"CRITICAL ERROR: Dataset not found at {json_path}")
            return

        # --- Model Loading & Vector Indexing ---
        print("Loading AI Model (all-MiniLM-L6-v2)...")
        try:
            # Load the Sentence-BERT model optimized for semantic similarity
            self.model = SentenceTransformer('all-MiniLM-L6-v2')
            print("Pre-calculating vectors (Indexing)...")

            if self.search_labels:
                # Batch encode all ESCO concepts into high-dimensional vectors.
                # This 'index' is stored in memory for fast retrieval.
                self.stored_embeddings = self.model.encode(self.search_labels)
                print(">>> System Ready: Vector Index built successfully.")
            else:
                print("No labels found to encode.")

        except Exception as e:
            print(f"Error loading AI model: {e}")

    def normalize(self, user_skills: list, threshold: float = 0.47) -> list:
        """
        Executes the Semantic Matching process.

        Args:
            user_skills (list): A list of raw skill strings extracted from the CV.
            threshold (float): The minimum cosine similarity score (0.0 to 1.0) required
                               to consider a match valid. Default is 0.47.

        Returns:
            list: A list of dictionaries containing matched ESCO skills and metadata.
        """
        # Guard clause: Check if model is initialized
        if self.model is None or self.stored_embeddings is None:
            return []

        results = []

        try:
            if not user_skills:
                return []

            # Vectorization: Encode the input skills into vectors
            input_vectors = self.model.encode(user_skills)

            # Similarity Calculation: Compute Cosine Similarity between input vectors and the ESCO Index
            similarity_matrix = cosine_similarity(input_vectors, self.stored_embeddings)

            # Best Match Retrieval
            for i, original_text in enumerate(user_skills):
                # Find the index of the highest score in the similarity matrix
                best_idx = np.argmax(similarity_matrix[i])
                best_score = float(similarity_matrix[i][best_idx])

                # Retrieve the corresponding ESCO record
                if best_idx < len(self.data_records):
                    matched_item = self.data_records[best_idx]
                else:
                    matched_item = {}

                # Threshold Evaluation
                if best_score >= threshold:
                    # Valid Match: Keep the data
                    result = {
                        "input_skill": original_text,
                        "similarity_score": round(best_score, 2),
                        "esco_preferredLabel": matched_item.get('preferredLabel'),
                        "esco_description": matched_item.get('description'),
                        "esco_uri": matched_item.get('conceptUri', ''),
                        "esco_alt_labels": matched_item.get('altLabels', [])[:3]
                    }
                else:
                    # Unmatched Skill: Set ESCO fields to None
                    result = {
                        "input_skill": original_text,
                        "similarity_score": round(best_score, 2),
                        "esco_preferredLabel": None,
                        "esco_description": None,
                        "esco_uri": None,
                        "esco_alt_labels": []
                    }

                # ALWAYS append the result, regardless of the score
                results.append(result)

        except Exception as e:
            print(f"Error during normalization logic: {e}")

        return results