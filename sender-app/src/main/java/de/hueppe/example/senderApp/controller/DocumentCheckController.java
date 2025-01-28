package de.hueppe.example.senderApp.controller;

import com.sdase.malware.scanner.streaming.model.v1.CheckEvent;
import de.hueppe.example.senderApp.messaging.KafkaCheckEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/check")
@RequiredArgsConstructor
public class DocumentCheckController {

    private final KafkaCheckEventProducer kafkaCheckEventProducer;

    @PostMapping("/send")
    public String sendDocument(@RequestParam("fileType") String fileType, @RequestParam("url") String url) {
        CheckEvent event = CheckEvent.builder()
                .url(url)
                .fileType(fileType)
                .build();

        kafkaCheckEventProducer.sendCheckEvent(event);
        return "Document event sent to Kafka: " + url;
    }
}