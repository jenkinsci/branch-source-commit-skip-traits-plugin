package org.jenkinsci.plugins.scm_filter;

import javax.annotation.Nonnull;

import org.kohsuke.stapler.DataBoundConstructor;

import com.cloudbees.jenkins.plugins.bitbucket.BitbucketGitSCMRevision;
import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMSource;
import com.cloudbees.jenkins.plugins.bitbucket.PullRequestSCMRevision;

import hudson.Extension;
import hudson.Util;
import jenkins.scm.api.SCMRevision;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.SCMSourceDescriptor;

public class BitbucketCommitMessageBranchBuildStrategy extends CommitMessageBranchBuildStrategy {

    @DataBoundConstructor
    public BitbucketCommitMessageBranchBuildStrategy(String pattern) {
        super(pattern);
    }

    @Override
    public String getMessage(SCMSource source, SCMRevision currRevision) throws CouldNotGetCommitDataException {
        SCMRevision revision = currRevision;
        if (currRevision instanceof PullRequestSCMRevision) {
            PullRequestSCMRevision<?> pr = (PullRequestSCMRevision<?>) currRevision;
            revision = pr.getPull();
        }
        if (revision instanceof BitbucketGitSCMRevision) {
            BitbucketGitSCMRevision bbRevision = (BitbucketGitSCMRevision) revision;
            return Util.fixEmpty(bbRevision.getMessage());
        }

        throw new CouldNotGetCommitDataException("Revision class is not a BitbucketGitSCMRevision");
    }

    @Extension
    public static class DescriptorImpl extends RegexFilterBranchBuildStrategyDescriptor {

        @Override
        @Nonnull
        public String getDisplayName() {
            return CommitMessageBranchBuildStrategy.getDisplayName();
        }

        /**
         * {@inheritDoc}
         * this is currently never called for organization folders, see JENKINS-54468
         */
        @Override
        public boolean isApplicable(@Nonnull SCMSourceDescriptor sourceDescriptor) {
            return BitbucketSCMSource.DescriptorImpl.class.isAssignableFrom(sourceDescriptor.getClass());
        }
    }
}
