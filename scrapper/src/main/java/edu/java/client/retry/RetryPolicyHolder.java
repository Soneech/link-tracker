package edu.java.client.retry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RetryPolicyHolder {

    private final Map<String, RetryPolicy> retryPolicies;

    @Autowired
    public RetryPolicyHolder(List<RetryPolicy> retryPolicies) {
        this.retryPolicies = new HashMap<>();
        retryPolicies.forEach(retry -> this.retryPolicies.put(retry.getTypeName(), retry));
    }

    public RetryPolicy getRetryPolicyByName(String typeName) {
        return retryPolicies.get(typeName);
    }
}
