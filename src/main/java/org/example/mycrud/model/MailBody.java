package org.example.mycrud.model;

import lombok.*;

import java.util.Map;
import java.util.Objects;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public final class MailBody {
    private String to;
    private String subject;
    private Map<String, Object> text;


}
