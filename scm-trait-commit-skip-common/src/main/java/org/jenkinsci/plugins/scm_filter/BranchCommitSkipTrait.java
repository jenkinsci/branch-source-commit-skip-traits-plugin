package org.jenkinsci.plugins.scm_filter;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.plugins.git.GitSCM;
import hudson.scm.SCMDescriptor;
import jenkins.scm.api.trait.SCMSourceContext;
import jenkins.scm.api.trait.SCMSourceTrait;
import jenkins.scm.api.trait.SCMSourceTraitDescriptor;

/**
 * @author witokondoria
 */
public abstract class BranchCommitSkipTrait extends SCMSourceTrait {

    @Override
    protected abstract void decorateContext(SCMSourceContext<?, ?> context);

    /**
     * Our descriptor.
     */
    abstract static class BranchCommitSkipTraitDescriptorImpl extends SCMSourceTraitDescriptor {

        @Override
        public String getDisplayName() {
            return "Commit message filtering behaviour (branches)";
        }

        @Override
        public boolean isApplicableToSCM(@NonNull SCMDescriptor<?> scm) {
            return scm instanceof GitSCM.DescriptorImpl;
        }
    }
}
