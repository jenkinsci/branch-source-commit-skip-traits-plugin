package org.jenkinsci.plugins.scm_filter;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hudson.model.TaskListener;
import hudson.util.LogTaskListener;
import org.junit.Test;

import com.cloudbees.jenkins.plugins.bitbucket.BitbucketGitSCMRevision;
import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMSource;
import com.cloudbees.jenkins.plugins.bitbucket.client.branch.BitbucketCloudAuthor;
import com.cloudbees.jenkins.plugins.bitbucket.client.branch.BitbucketCloudCommit;

import jenkins.scm.api.SCMHead;

import javax.annotation.Nonnull;
import java.io.PrintStream;

public class BitbucketCommitMessageBranchBuildStrategyTest {
    private final TaskListener listener = new TaskListener() {
        @Nonnull
        @Override
        public PrintStream getLogger() {
            return System.out;
        }
    };

    @Test
    public void skip_build_event_if_pattern_matches() throws Exception {
        BitbucketCommitMessageBranchBuildStrategy strategy = new BitbucketCommitMessageBranchBuildStrategy("initial");

        SCMHead head = mock(SCMHead.class);
        when(head.getName()).thenReturn("feature/release");

        BitbucketSCMSource source = new BitbucketSCMSource("amuniz", "test-repos");

        assertThat(strategy.isAutomaticBuild(source, head, buildRevision(head), null, listener), equalTo(false));
    }

    @Test
    public void no_skip_build_event_if_no_matches() throws Exception {
        BitbucketCommitMessageBranchBuildStrategy strategy = new BitbucketCommitMessageBranchBuildStrategy(".*test.*");

        SCMHead head = mock(SCMHead.class);
        when(head.getName()).thenReturn("feature/release");

        BitbucketSCMSource source = new BitbucketSCMSource("amuniz", "test-repos");
        assertThat(strategy.isAutomaticBuild(source, head, buildRevision(head), null, listener), equalTo(true));
    }

    private BitbucketGitSCMRevision buildRevision(SCMHead head) {
        BitbucketCloudAuthor author = new BitbucketCloudAuthor();
        author.setRaw("builder <no-reply@acme.com>");
        BitbucketCloudCommit commit = new BitbucketCloudCommit("initial commit", "2018-09-21T14:57:59.455870+00:00", "12345674890", author);
        return new BitbucketGitSCMRevision(head, commit);
    }
}
