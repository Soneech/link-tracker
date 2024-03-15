package edu.java.bot.website;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WebsiteInfo {
    GITHUB("github.com", "отслеживание изменений в репозиториях (нужна ссылка на репозиторий)."),
    STACK_OVERFLOW("stackoverflow.com", "отслеживание изменений в вопросах (нужна ссылка на вопрос).");

    private final String domain;

    private final String description;
}
