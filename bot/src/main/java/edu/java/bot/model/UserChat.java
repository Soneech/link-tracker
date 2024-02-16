package edu.java.bot.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserChat {
    private Long chatId;
    private List<String> trackingLinks;
}
