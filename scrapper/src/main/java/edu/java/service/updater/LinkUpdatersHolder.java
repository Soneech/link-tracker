package edu.java.service.updater;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LinkUpdatersHolder {
    private final Map<String, LinkUpdater> linkUpdaters;

    @Autowired
    public LinkUpdatersHolder(List<LinkUpdater> linkUpdaters) {
        this.linkUpdaters = new HashMap<>();
        linkUpdaters.forEach((updater) -> this.linkUpdaters.put(updater.getSupportDomain(), updater));
    }

    public LinkUpdater getUpdaterByDomain(String domain) {
        return linkUpdaters.get(domain);
    }
}
