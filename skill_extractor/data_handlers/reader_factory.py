from skill_extractor.data_handlers.readers.docx_reader import DocxReader
from skill_extractor.data_handlers.readers.pdf_reader import PdfReader
from skill_extractor.data_handlers.base_reader import BaseReader
from skill_extractor.data_handlers.readers.text_reader import TextReader

class ReaderFactory:
    """Implements the Factory Pattern to return the appropriate, initialized Reader class"""

    @staticmethod
    def get_reader(content_type: str) -> BaseReader:
        """
        Returns an instance of the appropriate Reader class based on the file's MIME type.

        :param content_type: The MIME type string (e.g., 'application/pdf').
        :raises ValueError: If the file type is not supported by the factory.
        :return: An initialized instance of a class inheriting from BaseReader.
        """
        # Dictionary mapping MIME types to their corresponding Reader classes
        reader_map = {
            "application/pdf": PdfReader,
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document": DocxReader,
            "text/plain": TextReader
        }

        # Look up the class in the map
        reader_class = reader_map.get(content_type)

        if reader_class:
            # Instantiate and return the appropriate reader
            return reader_class()
        else:
            # Handle unsupported types explicitly
            raise ValueError(f"Unsupported file type: {content_type}. Cannot find a suitable reader.")