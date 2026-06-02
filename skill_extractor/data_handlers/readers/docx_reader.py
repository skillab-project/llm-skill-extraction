from typing import BinaryIO
from docx import Document

from skill_extractor.data_handlers.base_reader import BaseReader


class DocxReader(BaseReader):
    """
    A concrete implementation of a data reader specialized for Microsoft Word (DOCX) files

    It adheres to the BaseReader contract, guaranteeing it has a 'read' method
    Utilizes the 'python-docx' library for parsing document structure
    """

    def __init__(self):
        """
        Initializes the DocxReader instance.
        """
        pass

    def read(self, file_object: BinaryIO) -> str:
        """
        Reads a binary file object containing a Word document and extracts text from paragraphs.

        :param file_object: The file-like object containing the DOCX data.
        :return: All extracted text content joined by newlines.
        """
        try:
        # Load the document directly from the binary stream using python-docx
            doc = Document(file_object)

            full_text = []

            # Iterate through all paragraphs in the document
            for para in doc.paragraphs:
                # Check if the paragraph contains text (ignoring empty lines/whitespace)
                # This helps in cleaning up the output.
                if para.text.strip():
                    full_text.append(para.text)

            # Join all non-empty paragraphs into a single string, separated by newlines
            return '\n'.join(full_text)
        except Exception as e:
            # Catch library-specific errors (like bad zip file) and re-raise generic error
            raise Exception(f"DocxReader error: {e}")
