package edu.java.service.ratelimiting;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class RateLimitingService {

    private static final String TELEGRAM_CHAT_ID_HEADER = "Tg-Chat-Id";

    public String getClientIdentifier(HttpServletRequest request) {
        String tgChatId = request.getHeader(TELEGRAM_CHAT_ID_HEADER);
        if (tgChatId == null) {
            return request.getRemoteAddr();
        }
        return tgChatId;
    }
}
