package org.jenkinsci.plugins.scm_filter;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.trait.SCMSourceContext;
import jenkins.scm.api.trait.SCMSourceRequest;
import jenkins.scm.impl.trait.Selection;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.github_branch_source.GitHubSCMSource;
import org.jenkinsci.plugins.github_branch_source.GitHubSCMSourceContext;
import org.jenkinsci.plugins.github_branch_source.GitHubSCMSourceRequest;
import org.jenkinsci.plugins.github_branch_source.PullRequestSCMHead;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * @author witokondoria
 */
public class GitHubCommitSkipTrait extends CommitSkipTrait {

    /**
     * Constructor for stapler.
     */
    @DataBoundConstructor
    public GitHubCommitSkipTrait() {
        super();
    }

    @Override
    protected void decorateContext(SCMSourceContext<?, ?> context) {
        context.withFilter(new ExcludeCommitPRsSCMHeadFilter());
    }

    /**
     * Our descriptor.
     */
    @Extension
    @Selection
    @Symbol("gitHubCommitSkipTrait")
    @SuppressWarnings("unused") // instantiated by Jenkins
    public static class DescriptorImpl extends CommitSkipTraitDescriptorImpl {

        @Override
        public Class<? extends SCMSourceContext> getContextClass() {
            return GitHubSCMSourceContext.class;
        }

        @Override
        public Class<? extends SCMSource> getSourceClass() {
            return GitHubSCMSource.class;
        }
    }

    /**
     * Filter that excludes pull requests according to its last commit message (if it contains [ci skip] or [skip ci], case insensitive).
     */
    private static class ExcludeCommitPRsSCMHeadFilter extends ExcludeByMessageSCMHeadFilter {

        @Override
        public boolean isExcluded(@NonNull SCMSourceRequest scmSourceRequest, @NonNull SCMHead scmHead) throws IOException, InterruptedException {
            if (scmHead instanceof PullRequestSCMHead) {
                Iterable<GHPullRequest> pulls = ((GitHubSCMSourceRequest) scmSourceRequest).getPullRequests();
                for (GHPullRequest pull : pulls) {
                    if (("PR-" + pull.getNumber()).equals(scmHead.getName())) {
                        String message = pull.getHead().getCommit().getCommitShortInfo().getMessage();
                        return super.containsSkipToken(message.toLowerCase());
                    }
                }
            }
            return false;
        }
    }
}
