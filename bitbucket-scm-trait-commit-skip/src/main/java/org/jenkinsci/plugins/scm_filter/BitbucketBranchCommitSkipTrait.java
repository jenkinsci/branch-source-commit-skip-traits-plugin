package org.jenkinsci.plugins.scm_filter;

import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMSource;
import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMSourceContext;
import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMSourceRequest;
import com.cloudbees.jenkins.plugins.bitbucket.BranchSCMHead;
import com.cloudbees.jenkins.plugins.bitbucket.api.BitbucketBranch;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.trait.SCMSourceContext;
import jenkins.scm.api.trait.SCMSourceRequest;
import jenkins.scm.impl.trait.Selection;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * @author witokondoria
 */
public class BitbucketBranchCommitSkipTrait extends BranchCommitSkipTrait {

    /**
     * Constructor for stapler.
     */
    @DataBoundConstructor
    public BitbucketBranchCommitSkipTrait() {
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
    @Symbol("bitbucketBranchCommitSkipTrait")
    @SuppressWarnings("unused") // instantiated by Jenkins
    public static class DescriptorImpl extends BranchCommitSkipTraitDescriptorImpl {

        @Override
        public Class<? extends SCMSourceContext> getContextClass() {
            return BitbucketSCMSourceContext.class;
        }

        @Override
        public Class<? extends SCMSource> getSourceClass() {
            return BitbucketSCMSource.class;
        }
    }

    /**
     * Filter that excludes pull requests according to its last commit message (if it contains [ci skip] or [skip ci], case insensitive).
     */
    private static class ExcludeBranchCommitSCMHeadFilter extends ExcludeByMessageSCMHeadFilter {

        @Override
        public boolean isExcluded(@NonNull SCMSourceRequest scmSourceRequest, @NonNull SCMHead scmHead) throws IOException, InterruptedException {
            if (scmHead instanceof BranchSCMHead) {
                Iterable<BitbucketBranch> branches = ((BitbucketSCMSourceRequest) scmSourceRequest).getBranches();
                for (BitbucketBranch branch : branches) {
                    if (branch.getName().equals(scmHead.getName())) {
                        String message = branch.getMessage();
                        return super.containsSkipToken(message.toLowerCase());
                    }
                }
            }
            return false;
        }
    }
}
