package org.jenkinsci.plugins.scm_filter;

import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMSource;
import jenkins.model.Jenkins;
import jenkins.scm.api.SCMSource;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.jvnet.hudson.test.JenkinsRule;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

public class BitbucketCommitSkipTraitTest {
    @ClassRule
    public static JenkinsRule j = new JenkinsRule();
    @Rule
    public TestName currentTestName = new TestName();

    private SCMSource load() {
        return load(currentTestName.getMethodName());
    }

    private SCMSource load(String dataSet) {
        return (BitbucketSCMSource) Jenkins.XSTREAM2.fromXML(
                getClass().getResource(getClass().getSimpleName() + "/" + dataSet + ".xml"));
    }

    @Test
    public void skipci() throws Exception {
        BitbucketSCMSource instance = (BitbucketSCMSource) load();
        assertThat(instance.getTraits(),
                containsInAnyOrder(
                        instanceOf(BitbucketCommitSkipTrait.class))
        );
    }
}