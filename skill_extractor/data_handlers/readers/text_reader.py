from typing import BinaryIO

from skill_extractor.data_handlers.base_reader import BaseReader

class TextReader(BaseReader):
    """
    A concrete implementation of a data reader specialized for plain text (TXT) files.

    It adheres to the BaseReader contract, guaranteeing it has a 'read' method.
    Utilizes Python's built-in string decoding capabilities.
    """

    def __init__(self):
        """
        Initializes the TextReader instance.
        """
        pass

    def read(self, file_object: BinaryIO) -> str:
        """
        Reads a binary file object containing plain text and decodes it.

        :param file_object: The file-like object containing the text data.
        :return: The decoded string content.
        """

        #Read the bytes from the file
        content = file_object.read()

        try:
            # Attempt to decode the content using UTF-8, which is the standard encoding for modern text files
            return content.decode('utf-8')
        except UnicodeDecodeError:
            # If UTF-8 decoding fails,
            # fallback to ISO-8859-1 (Latin-1). This encoding maps every byte to a character,
            # ensuring the process does not crash due to encoding mismatches.
            return content.decode('ISO-8859-1')