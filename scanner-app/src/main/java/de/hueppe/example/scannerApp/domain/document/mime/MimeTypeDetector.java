package de.hueppe.example.scannerApp.domain.document.mime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface MimeTypeDetector {
    String detect(File file) throws IOException;
    String detect(InputStream inputStream) throws IOException;
}