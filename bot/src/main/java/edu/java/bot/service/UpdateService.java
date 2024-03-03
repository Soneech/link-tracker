package edu.java.bot.service;

import edu.java.bot.dto.request.LinkUpdateRequest;
import edu.java.bot.exception.UpdateAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateService {
    private List<LinkUpdateRequest> updates = new ArrayList<>();

    public void addUpdate(LinkUpdateRequest updateRequest) {
        for (var update: updates) {
            if (update.id().equals(updateRequest.id())) {
                throw new UpdateAlreadyExistsException(update.id());
            }
        }

        updates.add(updateRequest);
    }
}
