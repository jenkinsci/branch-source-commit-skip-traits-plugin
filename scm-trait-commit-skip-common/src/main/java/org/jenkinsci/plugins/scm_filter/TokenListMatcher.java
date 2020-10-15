package org.jenkinsci.plugins.scm_filter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TokenListMatcher {
    private final Set<String> tokenList;

    public TokenListMatcher(String tokens) {
        Set<String> list = new HashSet<String>();
        for (String token : tokens.split(",")) {
            token = token.trim().toLowerCase();
            if (!token.isEmpty()) {
                list.add(token);
            }
        }
        tokenList = Collections.unmodifiableSet(list);
    }

    public boolean containsSkipToken(String commitMsg) {
        for (String token : tokenList) {
            if (commitMsg.contains(token)) {
                return true;
            }
        }
        return false;
    }
}
