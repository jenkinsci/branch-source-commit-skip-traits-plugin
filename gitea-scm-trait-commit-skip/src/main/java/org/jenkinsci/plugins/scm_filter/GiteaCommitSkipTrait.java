package org.jenkinsci.plugins.scm_filter;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.trait.SCMBuilder;
import jenkins.scm.api.trait.SCMSourceContext;
import jenkins.scm.api.trait.SCMSourceRequest;

import org.jenkinsci.plugin.gitea.GiteaSCMBuilder;
import org.jenkinsci.plugin.gitea.GiteaSCMSourceRequest;
import org.jenkinsci.plugin.gitea.PullRequestSCMHead;
import org.jenkinsci.plugin.gitea.client.api.GiteaBranch;
import org.jenkinsci.plugin.gitea.client.api.GiteaPullRequest;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.Iterator;

/**
 * @author witokondoria
 */
public class GiteaCommitSkipTrait extends CommitSkipTrait {

    /**
     * Constructor for stapler.
     */
    @DataBoundConstructor
    public GiteaCommitSkipTrait() {
        super();
    }

    @Override
    protected void decorateContext(SCMSourceContext<?, ?> context) {
        context.withFilter(new GiteaCommitSkipTrait.ExcludeCommitPRsSCMHeadFilter());
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
            return GiteaSCMBuilder.class.isAssignableFrom(builderClass);
        }
    }

    /**
     * Filter that excludes pull requests according to its last commit message (if it contains [ci skip] or [skip ci], case unsensitive).
     */
    public static class ExcludeCommitPRsSCMHeadFilter extends ExcludePRsSCMHeadFilter{

        public ExcludeCommitPRsSCMHeadFilter() {
            super();
        }

        @Override
        public boolean isExcluded(@NonNull SCMSourceRequest scmSourceRequest, @NonNull SCMHead scmHead) throws IOException, InterruptedException {
            if (scmHead instanceof PullRequestSCMHead) {
                Iterable<GiteaPullRequest> pulls = ((GiteaSCMSourceRequest) scmSourceRequest).getPullRequests();
                Iterator<GiteaPullRequest> pullIterator = pulls.iterator();
                while (pullIterator.hasNext()) {
                    GiteaPullRequest pull = pullIterator.next();
                    if (("PR-" + pull.getNumber()).equals(scmHead.getName())) {
                        String sha1 = pull.getHead().getSha();
                        Iterable<GiteaBranch> branches = ((GiteaSCMSourceRequest) scmSourceRequest).getBranches();
                        for (GiteaBranch branch: branches) {
                            if (sha1.equals(branch.getCommit().getId())) {
                                String message = branch.getCommit().getMessage().toLowerCase();
                                return super.containsSkipToken(message);
                            }
                        }
                        return false;
                    }
                }
            }
            return false;
        }
    }
}
