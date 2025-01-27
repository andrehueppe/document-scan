package de.hueppe.example.scannerApp.domain.document.mime;

import java.io.File;
import java.io.IOException;

public interface MimeTypeDetector {
    String detect(File file) throws IOException;
}