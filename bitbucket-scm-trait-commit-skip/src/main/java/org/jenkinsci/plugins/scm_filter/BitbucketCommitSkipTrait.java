package org.jenkinsci.plugins.scm_filter;

import com.cloudbees.jenkins.plugins.bitbucket.BitbucketGitSCMBuilder;
import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMSourceRequest;
import com.cloudbees.jenkins.plugins.bitbucket.PullRequestSCMHead;
import com.cloudbees.jenkins.plugins.bitbucket.api.BitbucketPullRequest;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.trait.SCMBuilder;
import jenkins.scm.api.trait.SCMSourceContext;
import jenkins.scm.api.trait.SCMSourceRequest;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.Iterator;

/**
 * @author witokondoria
 */
public class BitbucketCommitSkipTrait extends CommitSkipTrait{

    /**
     * Constructor for stapler.
     */
    @DataBoundConstructor
    public BitbucketCommitSkipTrait() {
        super();
    }

    @Override
    protected void decorateContext(SCMSourceContext<?, ?> context) {
        context.withFilter(new BitbucketCommitSkipTrait.ExcludeCommitPRsSCMHeadFilter());
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
            return BitbucketGitSCMBuilder.class.isAssignableFrom(builderClass);
        }
    }

    /**
     * Filter that excludes pull requests according to its last commit message (if it contains [ci skip] or [skip ci], case unsensitive).
     */
    public static class ExcludeCommitPRsSCMHeadFilter extends ExcludePRsSCMHeadFilter {

        public ExcludeCommitPRsSCMHeadFilter() {
            super();
        }

        @Override
        public boolean isExcluded(@NonNull SCMSourceRequest scmSourceRequest, @NonNull SCMHead scmHead) throws IOException, InterruptedException {
            if (scmHead instanceof PullRequestSCMHead) {
                Iterable<BitbucketPullRequest> pulls = ((BitbucketSCMSourceRequest) scmSourceRequest).getPullRequests();
                Iterator<BitbucketPullRequest> pullIterator = pulls.iterator();
                while (pullIterator.hasNext()) {
                    BitbucketPullRequest pull = pullIterator.next();
                    if (pull.getSource().getBranch().getName().equals(scmHead.getName())) {
                        String message = pull.getSource().getCommit().getMessage().toLowerCase();
                        return super.containsSkipToken(message);
                    }
                }
            }
            return false;
        }
    }
}
