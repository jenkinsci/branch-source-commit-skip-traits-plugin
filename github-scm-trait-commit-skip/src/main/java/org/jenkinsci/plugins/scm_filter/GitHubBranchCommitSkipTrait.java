package org.jenkinsci.plugins.scm_filter;

import edu.umd.cs.findbugs.annotations.NonNull;
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
    public GitHubBranchCommitSkipTrait() {
        super();
    }

    @Override
    protected void decorateContext(SCMSourceContext<?, ?> context) {
        context.withFilter(new ExcludeBranchCommitSCMHeadFilter());
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

        @Override
        public boolean isExcluded(@NonNull SCMSourceRequest scmSourceRequest, @NonNull SCMHead scmHead) throws IOException, InterruptedException {
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
