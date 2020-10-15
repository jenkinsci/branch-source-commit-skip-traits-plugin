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
import org.jenkinsci.plugins.github_branch_source.BranchSCMHead;
import org.jenkinsci.plugins.github_branch_source.GitHubSCMSource;
import org.jenkinsci.plugins.github_branch_source.GitHubSCMSourceContext;
import org.jenkinsci.plugins.github_branch_source.GitHubSCMSourceRequest;
import org.kohsuke.github.GHBranch;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * @author witokondoria
 */
public class GitHubBranchCommitSkipTrait extends BranchCommitSkipTrait {

    /**
     * Constructor for stapler.
     */
    @DataBoundConstructor
    public GitHubBranchCommitSkipTrait(@CheckForNull String tokens) {
        super(tokens);
    }

    @Override
    protected void decorateContext(SCMSourceContext<?, ?> context) {
        context.withFilter(new ExcludeBranchCommitSCMHeadFilter(getTokens()));
    }

    /**
     * Our descriptor.
     */
    @Extension
    @Selection
    @Symbol("gitHubBranchCommitSkipTrait")
    @SuppressWarnings("unused") // instantiated by Jenkins
    public static class DescriptorImpl extends BranchCommitSkipTraitDescriptorImpl {

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
    private static class ExcludeBranchCommitSCMHeadFilter extends ExcludeByMessageSCMHeadFilter {

        public ExcludeBranchCommitSCMHeadFilter(String tokens) {
            super(tokens);
        }

        @Override
        public boolean isExcluded(@Nonnull SCMSourceRequest scmSourceRequest, @Nonnull SCMHead scmHead) throws IOException, InterruptedException {
            if (scmHead instanceof BranchSCMHead) {
                Iterable<GHBranch> branches = ((GitHubSCMSourceRequest) scmSourceRequest).getBranches();
                for (GHBranch branch : branches) {
                    if ((branch.getName()).equals(scmHead.getName())) {
                        String message = ((GitHubSCMSourceRequest) scmSourceRequest).getRepository().getCommit(branch.getSHA1()).getCommitShortInfo().getMessage();
                        return super.containsSkipToken(message.toLowerCase());
                    }
                }
            }
            return false;
        }
    }
}
