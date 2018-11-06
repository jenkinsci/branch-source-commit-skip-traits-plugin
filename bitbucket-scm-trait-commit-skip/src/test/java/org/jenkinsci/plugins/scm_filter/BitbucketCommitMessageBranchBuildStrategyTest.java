/*
 * Copyright 2018 Finantix
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jenkinsci.plugins.scm_filter;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.cloudbees.jenkins.plugins.bitbucket.BitbucketGitSCMRevision;
import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMSource;
import com.cloudbees.jenkins.plugins.bitbucket.client.branch.BitbucketCloudAuthor;
import com.cloudbees.jenkins.plugins.bitbucket.client.branch.BitbucketCloudCommit;

import jenkins.scm.api.SCMHead;

public class BitbucketCommitMessageBranchBuildStrategyTest {
    @Test
    public void skip_build_event_if_pattern_matches() throws Exception {
        BitbucketCommitMessageBranchBuildStrategy strategy = new BitbucketCommitMessageBranchBuildStrategy("initial");

        SCMHead head = mock(SCMHead.class);
        when(head.getName()).thenReturn("feature/release");

        BitbucketSCMSource source = new BitbucketSCMSource("amuniz", "test-repos");
        assertThat(strategy.isAutomaticBuild(source, head, buildRevision(head), null), equalTo(false));
    }

    @Test
    public void no_skip_build_event_if_no_matches() throws Exception {
        BitbucketCommitMessageBranchBuildStrategy strategy = new BitbucketCommitMessageBranchBuildStrategy(".*test.*");
        
        SCMHead head = mock(SCMHead.class);
        when(head.getName()).thenReturn("feature/release");
        
        BitbucketSCMSource source = new BitbucketSCMSource("amuniz", "test-repos");
        assertThat(strategy.isAutomaticBuild(source, head, buildRevision(head), null), equalTo(true));
    }

    private BitbucketGitSCMRevision buildRevision(SCMHead head) {
        BitbucketCloudAuthor author = new BitbucketCloudAuthor();
        author.setRaw("builder <no-reply@acme.com>");
        BitbucketCloudCommit commit = new BitbucketCloudCommit("initial commit", "2018-09-21T14:57:59.455870+00:00", "12345674890", author);
        return new BitbucketGitSCMRevision(head, commit);
    }
}