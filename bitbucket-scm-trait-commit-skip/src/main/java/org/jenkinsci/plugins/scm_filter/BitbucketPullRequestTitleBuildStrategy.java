package org.jenkinsci.plugins.scm_filter;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.kohsuke.stapler.DataBoundConstructor;

import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMSource;
import com.cloudbees.jenkins.plugins.bitbucket.PullRequestSCMHead;
import com.cloudbees.jenkins.plugins.bitbucket.api.BitbucketApi;
import com.cloudbees.jenkins.plugins.bitbucket.api.BitbucketPullRequest;

import hudson.Extension;
import hudson.Util;
import jenkins.branch.BranchBuildStrategyDescriptor;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.SCMSourceDescriptor;

/**
 * @author avolanis
 */
public class BitbucketPullRequestTitleBuildStrategy extends PullRequestTitleBuildStrategy {

    @DataBoundConstructor
    public BitbucketPullRequestTitleBuildStrategy(String tokens) {
        super(tokens);
    }

    @Override
    protected String getTitle(SCMSource source, SCMHead head) throws CouldNotGetCommitDataException {
        if (source instanceof BitbucketSCMSource && head instanceof PullRequestSCMHead) {
            BitbucketApi client = ((BitbucketSCMSource) source).buildBitbucketClient((PullRequestSCMHead) head);
            try {
                BitbucketPullRequest pr = client
                        .getPullRequestById(Integer.valueOf(((PullRequestSCMHead) head).getId()));
                return Util.fixEmpty(pr.getTitle());
            } catch (NumberFormatException e) {
                throw new CouldNotGetCommitDataException("Could not parse pull request ID to integer");
            } catch (IOException e) {
                throw new CouldNotGetCommitDataException("Could not fetch pull request information");
            } catch (InterruptedException e) {
                throw new CouldNotGetCommitDataException("Could not fetch pull request information");
            }
        }

        throw new CouldNotGetCommitDataException(
                "SCMSource class is not a BitbucketSCMSource or SCMHead class is not a PullRequestSCMHead");
    }

    @Extension
    public static class DescriptorImpl extends BranchBuildStrategyDescriptor {
        /**
         * {@inheritDoc}
         */
        @Override
        public @Nonnull String getDisplayName() {
            return PullRequestTitleBuildStrategy.getDisplayName();
        }

        /**
         * {@inheritDoc} this is currently never called for organization folders, see
         * JENKINS-54468
         */
        @Override
        public boolean isApplicable(@Nonnull SCMSourceDescriptor sourceDescriptor) {
            return BitbucketSCMSource.DescriptorImpl.class.isAssignableFrom(sourceDescriptor.getClass());
        }
    }
}
