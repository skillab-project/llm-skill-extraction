from pypdf import PdfReader as PyPDFReader
from typing import BinaryIO
from skill_extractor.data_handlers.base_reader import BaseReader # Import the interface

class PdfReader(BaseReader):
    """
    A concrete implementation of a data reader specialized for Portable Document Format (PDF) files.

    It adheres to the BaseReader contract, guaranteeing it has a 'read' method.
    Utilizes the 'pypdf' library for parsing.
    """

    def __init__(self):
        """
        Initializes the PdfReader instance.
        """
        pass

    def read(self, file_object: BinaryIO) -> str:
        """
        Reads a binary file object containing PDF data and extracts text from pages.

        :param file_object: The file-like object containing the PDF data.
        :return: All extracted text content joined by newlines.
        """
        try:
            # PyPDFReader reads directly from the file-like object (buffer)
            reader = PyPDFReader(file_object)

            full_text = []

            # Iterate over every page to ensure all text is captured.
            for page in reader.pages:
                text = page.extract_text()

                # Only append non-empty text segments.
                if text:
                    full_text.append(text)

            # Join all extracted text segments with a newline character.
            return '\n'.join(full_text)

        except Exception as e:
            # Re-raise the exception with context for upstream error handling (e.g., FastAPI).
            raise Exception(f"PdfReader error during parsing: {e}")