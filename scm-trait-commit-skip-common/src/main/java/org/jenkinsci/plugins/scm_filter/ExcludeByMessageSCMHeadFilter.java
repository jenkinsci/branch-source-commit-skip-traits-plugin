package org.jenkinsci.plugins.scm_filter;

import edu.umd.cs.findbugs.annotations.NonNull;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.trait.SCMHeadFilter;
import jenkins.scm.api.trait.SCMSourceRequest;

import java.io.IOException;

/**
 * Filter that excludes pull requests according to its last commit message (if it contains [ci skip] or [skip ci], case insensitive).
 *
 * @author witokondoria
 */
abstract class ExcludeByMessageSCMHeadFilter extends SCMHeadFilter {

    @Override
    abstract public boolean isExcluded(@NonNull SCMSourceRequest scmSourceRequest, @NonNull SCMHead scmHead) throws IOException, InterruptedException;

    boolean containsSkipToken(String commitMsg) {
        return commitMsg.contains("[ci skip]") || commitMsg.contains("[skip ci]");
    }
}
