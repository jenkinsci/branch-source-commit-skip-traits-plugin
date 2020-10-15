package org.jenkinsci.plugins.scm_filter;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

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
    public GitHubCommitSkipTrait(@CheckForNull String tokens) {
        super(tokens);
    }

    @Override
    protected void decorateContext(SCMSourceContext<?, ?> context) {
        context.withFilter(new ExcludeCommitPRsSCMHeadFilter(getTokens()));
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

        public ExcludeCommitPRsSCMHeadFilter(String tokens) {
            super(tokens);
        }

        @Override
        public boolean isExcluded(@Nonnull SCMSourceRequest scmSourceRequest, @Nonnull SCMHead scmHead) throws IOException, InterruptedException {
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
