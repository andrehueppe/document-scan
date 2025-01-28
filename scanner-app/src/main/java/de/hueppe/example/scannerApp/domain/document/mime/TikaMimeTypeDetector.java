package de.hueppe.example.scannerApp.domain.document.mime;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Component
public class TikaMimeTypeDetector implements MimeTypeDetector {

    private final Tika tika = new Tika();

    @Override
    public String detect(File file) throws IOException {
        return tika.detect(file);
    }

    @Override
    public String detect(InputStream inputStream) throws IOException {
        return tika.detect(inputStream);
    }
}
