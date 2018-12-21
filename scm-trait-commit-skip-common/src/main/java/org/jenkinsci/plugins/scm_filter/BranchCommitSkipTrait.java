package org.jenkinsci.plugins.scm_filter;

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
    }
}
