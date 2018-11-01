package org.jenkinsci.plugins.scm_filter;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.trait.SCMBuilder;
import jenkins.scm.api.trait.SCMSourceContext;
import jenkins.scm.api.trait.SCMSourceRequest;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.github_branch_source.BranchSCMHead;
import org.jenkinsci.plugins.github_branch_source.GitHubSCMBuilder;
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
    @Extension @Symbol("gitHubBranchCommitSkipTrait")
    @SuppressWarnings("unused") // instantiated by Jenkins
    public static class DescriptorImpl extends BranchCommitSkipTraitDescriptorImpl {

        @Override
        public boolean isApplicableToBuilder(@NonNull Class<? extends SCMBuilder> builderClass) {
            return GitHubSCMBuilder.class.isAssignableFrom(builderClass);
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
