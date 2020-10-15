package org.jenkinsci.plugins.scm_filter;

import java.io.IOException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import com.cloudbees.jenkins.plugins.bitbucket.BitbucketGitSCMBuilder;
import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMSourceRequest;
import com.cloudbees.jenkins.plugins.bitbucket.PullRequestSCMHead;
import com.cloudbees.jenkins.plugins.bitbucket.api.BitbucketPullRequest;

import hudson.Extension;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.trait.SCMBuilder;
import jenkins.scm.api.trait.SCMSourceContext;
import jenkins.scm.api.trait.SCMSourceRequest;
import jenkins.scm.impl.trait.Selection;

/**
 * @author avolanis
 */
public class BitbucketPullRequestTitleSkipTrait extends TitleSkipTrait {

    /**
     * Constructor for stapler.
     */
    @DataBoundConstructor
    public BitbucketPullRequestTitleSkipTrait(@CheckForNull String tokens) {
        super(tokens);
    }

    @Override
    protected void decorateContext(SCMSourceContext<?, ?> context) {
        context.withFilter(new ExcludeTitlePRsSCMHeadFilter(getTokens()));
    }

    /**
     * Our descriptor.
     */
    @Extension
    @Selection
    @Symbol("bitbucketTitleSkipTrait")
    @SuppressWarnings("unused") // instantiated by Jenkins
    public static class DescriptorImpl extends TitleSkipTraitDescriptorImpl {

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
        public boolean isApplicableToBuilder(@Nonnull Class<? extends SCMBuilder> builderClass) {
            return BitbucketGitSCMBuilder.class.isAssignableFrom(builderClass);
        }
    }

    /**
     * Filter that excludes pull requests according to its title (if it contains [ci
     * skip] or [skip ci], case insensitive).
     */
    public static class ExcludeTitlePRsSCMHeadFilter extends ExcludeByMessageSCMHeadFilter {

        public ExcludeTitlePRsSCMHeadFilter(String tokens) {
            super(tokens);
        }

        @Override
        public boolean isExcluded(@Nonnull SCMSourceRequest scmSourceRequest, @Nonnull SCMHead scmHead)
                throws IOException, InterruptedException {
            if (scmHead instanceof PullRequestSCMHead) {
                Iterable<BitbucketPullRequest> pulls = ((BitbucketSCMSourceRequest) scmSourceRequest).getPullRequests();
                for (BitbucketPullRequest pull : pulls) {
                    if (pull.getSource().getBranch().getName().equals(scmHead.getName())) {
                        String title = pull.getTitle();
                        return super.containsSkipToken(title.toLowerCase());
                    }
                }
            }
            return false;
        }
    }
}
