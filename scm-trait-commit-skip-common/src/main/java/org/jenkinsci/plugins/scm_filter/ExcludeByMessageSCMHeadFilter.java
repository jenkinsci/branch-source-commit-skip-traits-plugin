package org.jenkinsci.plugins.scm_filter;

import java.io.IOException;

import javax.annotation.Nonnull;

import jenkins.scm.api.SCMHead;
import jenkins.scm.api.trait.SCMHeadFilter;
import jenkins.scm.api.trait.SCMSourceRequest;

/**
 * Filter that excludes pull requests according to its last commit message (if it contains [ci skip] or [skip ci], case insensitive).
 *
 * @author witokondoria
 */
abstract class ExcludeByMessageSCMHeadFilter extends SCMHeadFilter {

    private final TokenListMatcher matcher;

    public ExcludeByMessageSCMHeadFilter(String tokens) {
        matcher = new TokenListMatcher(tokens);
    }

    @Override
    abstract public boolean isExcluded(@Nonnull SCMSourceRequest scmSourceRequest, @Nonnull SCMHead scmHead) throws IOException, InterruptedException;

    boolean containsSkipToken(String commitMsg) {
        return matcher.containsSkipToken(commitMsg);
    }
}
