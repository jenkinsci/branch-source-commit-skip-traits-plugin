package org.jenkinsci.plugins.scm_filter;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.trait.SCMBuilder;
import jenkins.scm.api.trait.SCMSourceContext;
import jenkins.scm.api.trait.SCMSourceRequest;
import org.jenkinsci.plugins.github_branch_source.GitHubSCMBuilder;
import org.jenkinsci.plugins.github_branch_source.GitHubSCMSourceRequest;
import org.jenkinsci.plugins.github_branch_source.PullRequestSCMHead;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.Iterator;

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
        context.withFilter(new GitHubCommitSkipTrait.ExcludeCommitPRsSCMHeadFilter());
    }
    /**
     * Our descriptor.
     */
    @Extension
    @SuppressWarnings("unused") // instantiated by Jenkins
    public static class DescriptorImpl extends CommitSkipTraitDescriptorImpl {

        /**
         * {@inheritDoc}
         */
        @Override
        public String getDisplayName() {
            return super.getDisplayName();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isApplicableToBuilder(@NonNull Class<? extends SCMBuilder> builderClass) {
            return GitHubSCMBuilder.class.isAssignableFrom(builderClass);
        }
    }

    /**
     * Filter that excludes pull requests according to its last commit message (if it contains [ci skip] or [skip ci], case unsensitive).
     */
    public static class ExcludeCommitPRsSCMHeadFilter extends ExcludePRsSCMHeadFilter{

        public ExcludeCommitPRsSCMHeadFilter() {
            super();
        }

        @Override
        public boolean isExcluded(@NonNull SCMSourceRequest scmSourceRequest, @NonNull SCMHead scmHead) throws IOException, InterruptedException {
            if (scmHead instanceof PullRequestSCMHead) {
                Iterable<GHPullRequest> pulls = ((GitHubSCMSourceRequest) scmSourceRequest).getPullRequests();
                Iterator<GHPullRequest> pullIterator = pulls.iterator();
                while (pullIterator.hasNext()) {
                    GHPullRequest pull = pullIterator.next();
                    if (("PR-" + pull.getNumber()).equals(scmHead.getName())) {
                        String message = pull.getHead().getCommit().getCommitShortInfo().getMessage().toLowerCase();
                        return super.containsSkipToken(message);
                    }
                }
            }
            return false;
        }
    }
}
