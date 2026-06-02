from abc import ABC, abstractmethod
from typing import BinaryIO

class BaseReader(ABC):
    """
    The Abstract Base Class (Interface) for all file reader implementations

    It establishes the mandatory 'read' method that all concrete readers must implement
    """

    @abstractmethod
    def read(self, file_object: BinaryIO) -> str:
        """
        Processes a binary file-like object and extracts its raw text content

        :param file_object: The file-like object (a binary stream) containing the file content
        :type file_object: BinaryIO
        :return: All extracted text content as a single string
        :rtype: str
        """
        # This method signature ensures all readers are interchangeable.
        raise NotImplementedError("Subclasses must implement the 'read' method.")