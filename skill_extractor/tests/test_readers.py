import io

import pytest
from docx import Document
from pypdf import PdfWriter

from skill_extractor.data_handlers.base_reader import BaseReader
from skill_extractor.data_handlers.reader_factory import ReaderFactory
from skill_extractor.data_handlers.readers.docx_reader import DocxReader
from skill_extractor.data_handlers.readers.pdf_reader import PdfReader
from skill_extractor.data_handlers.readers.text_reader import TextReader

DOCX_MIME = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"


# ---------------------------------------------------------------------------
# ReaderFactory
# ---------------------------------------------------------------------------

@pytest.mark.parametrize(
    "mime, expected_cls",
    [
        ("application/pdf", PdfReader),
        (DOCX_MIME, DocxReader),
        ("text/plain", TextReader),
    ],
)
def test_factory_returns_matching_reader(mime, expected_cls):
    reader = ReaderFactory.get_reader(mime)
    assert isinstance(reader, expected_cls)
    assert isinstance(reader, BaseReader)


@pytest.mark.parametrize("mime", ["image/png", "application/msword", "", None])
def test_factory_rejects_unsupported_types(mime):
    with pytest.raises(ValueError, match="Unsupported file type"):
        ReaderFactory.get_reader(mime)


def test_base_reader_cannot_be_instantiated():
    with pytest.raises(TypeError):
        BaseReader()


# ---------------------------------------------------------------------------
# TextReader
# ---------------------------------------------------------------------------

def test_text_reader_decodes_utf8():
    text = "Python, Ελληνικά, café"
    assert TextReader().read(io.BytesIO(text.encode("utf-8"))) == text


def test_text_reader_falls_back_to_latin1():
    raw = "café".encode("latin-1")  # b'caf\xe9' is invalid UTF-8
    assert TextReader().read(io.BytesIO(raw)) == "café"


def test_text_reader_empty_file():
    assert TextReader().read(io.BytesIO(b"")) == ""


# ---------------------------------------------------------------------------
# DocxReader
# ---------------------------------------------------------------------------

def _make_docx(*paragraphs: str) -> io.BytesIO:
    doc = Document()
    for p in paragraphs:
        doc.add_paragraph(p)
    buf = io.BytesIO()
    doc.save(buf)
    buf.seek(0)
    return buf


def test_docx_reader_extracts_paragraphs_and_skips_blank_ones():
    buf = _make_docx("John Doe", "", "   ", "Skills: Python, SQL")
    assert DocxReader().read(buf) == "John Doe\nSkills: Python, SQL"


def test_docx_reader_wraps_invalid_input():
    with pytest.raises(Exception, match="DocxReader error"):
        DocxReader().read(io.BytesIO(b"not a docx file"))


# ---------------------------------------------------------------------------
# PdfReader
# ---------------------------------------------------------------------------

def test_pdf_reader_blank_pdf_returns_empty_string():
    writer = PdfWriter()
    writer.add_blank_page(width=200, height=200)
    buf = io.BytesIO()
    writer.write(buf)
    buf.seek(0)
    assert PdfReader().read(buf) == ""


def test_pdf_reader_wraps_invalid_input():
    with pytest.raises(Exception, match="PdfReader error"):
        PdfReader().read(io.BytesIO(b"definitely not a pdf"))
