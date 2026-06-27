/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.jenkins.plugins.computeengine;

import static com.google.jenkins.plugins.computeengine.ComputeEngineCloud.checkPermissions;

import com.google.api.services.compute.model.Network;
import com.google.api.services.compute.model.Subnetwork;
import com.google.cloud.graphite.platforms.plugin.client.ComputeClient;
import com.google.common.base.Strings;
import hudson.Extension;
import hudson.RelativePath;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

public class AutofilledNetworkConfiguration extends NetworkConfiguration {
    private static final Logger LOGGER = Logger.getLogger(AutofilledNetworkConfiguration.class.getName());

    private static String firstNonEmpty(String... values) {
        for (String value : values) {
            if (!Strings.isNullOrEmpty(value)) {
                return value;
            }
        }
        return "";
    }

    @DataBoundConstructor
    public AutofilledNetworkConfiguration(String network, String subnetwork) {
        super(network, subnetwork);
    }

    public AutofilledNetworkConfiguration() {
        super("", "");
    }

    @Extension
    public static final class DescriptorImpl extends NetworkConfigurationDescriptor {
        public String getDisplayName() {
            return "Available networks";
        }

        public ListBoxModel doFillNetworkItems(
                @AncestorInPath Jenkins context,
                @QueryParameter("projectId") @RelativePath("../..") final String projectId,
                @QueryParameter("projectId") @RelativePath("../../..") final String nestedProjectId,
                @QueryParameter("credentialsId") @RelativePath("../..") final String credentialsId,
                @QueryParameter("credentialsId") @RelativePath("../../..") final String nestedCredentialsId,
                @QueryParameter("googleApiProxyHost") @RelativePath("../..") final String googleApiProxyHost,
                @QueryParameter("googleApiProxyHost") @RelativePath("../../..") final String nestedGoogleApiProxyHost,
                @QueryParameter("googleApiProxyPort") @RelativePath("../..") final String googleApiProxyPort,
                @QueryParameter("googleApiProxyPort") @RelativePath("../../..") final String nestedGoogleApiProxyPort,
                @QueryParameter("googleApiProxyUsername") @RelativePath("../..") final String googleApiProxyUsername,
                @QueryParameter("googleApiProxyUsername") @RelativePath("../../..") final String nestedGoogleApiProxyUsername,
                @QueryParameter("googleApiProxyPassword") @RelativePath("../..") final String googleApiProxyPassword,
                @QueryParameter("googleApiProxyPassword") @RelativePath("../../..") final String nestedGoogleApiProxyPassword) {
            String effectiveProjectId = firstNonEmpty(projectId, nestedProjectId);
            String effectiveCredentialsId = firstNonEmpty(credentialsId, nestedCredentialsId);
            String effectiveGoogleApiProxyHost = firstNonEmpty(googleApiProxyHost, nestedGoogleApiProxyHost);
            String effectiveGoogleApiProxyPort = firstNonEmpty(googleApiProxyPort, nestedGoogleApiProxyPort);
            String effectiveGoogleApiProxyUsername = firstNonEmpty(googleApiProxyUsername, nestedGoogleApiProxyUsername);
            String effectiveGoogleApiProxyPassword = firstNonEmpty(googleApiProxyPassword, nestedGoogleApiProxyPassword);
            checkPermissions(Jenkins.get(), Jenkins.ADMINISTER);
            ListBoxModel items = new ListBoxModel();
            items.add("");

            try {
                ComputeClient compute = computeClient(
                        context,
                        effectiveCredentialsId,
                        GoogleApiProxyConfiguration.fromFormFields(
                                effectiveGoogleApiProxyHost,
                                effectiveGoogleApiProxyPort,
                                effectiveGoogleApiProxyUsername,
                                effectiveGoogleApiProxyPassword));
                List<Network> networks = compute.listNetworks(effectiveProjectId);

                for (Network n : networks) {
                    items.add(n.getName(), n.getSelfLink());
                }
                return items;
            } catch (IOException | IllegalArgumentException e) {
                String message = "Error retrieving networks";
                LOGGER.log(Level.SEVERE, message, e);
                items.clear();
                items.add(new ListBoxModel.Option(message, "", true));
                return items;
            }
        }

        public FormValidation doCheckNetwork(@QueryParameter String value) {
            if (StringUtils.isEmpty(value)) {
                return FormValidation.error("Please select a network...");
            }
            return FormValidation.ok();
        }

        public ListBoxModel doFillSubnetworkItems(
                @AncestorInPath Jenkins context,
                @QueryParameter("network") final String network,
                @QueryParameter("region") @RelativePath("..") final String region,
                @QueryParameter("region") @RelativePath("../..") final String nestedRegion,
                @QueryParameter("projectId") @RelativePath("../..") final String projectId,
                @QueryParameter("projectId") @RelativePath("../../..") final String nestedProjectId,
                @QueryParameter("credentialsId") @RelativePath("../..") final String credentialsId,
                @QueryParameter("credentialsId") @RelativePath("../../..") final String nestedCredentialsId,
                @QueryParameter("googleApiProxyHost") @RelativePath("../..") final String googleApiProxyHost,
                @QueryParameter("googleApiProxyHost") @RelativePath("../../..") final String nestedGoogleApiProxyHost,
                @QueryParameter("googleApiProxyPort") @RelativePath("../..") final String googleApiProxyPort,
                @QueryParameter("googleApiProxyPort") @RelativePath("../../..") final String nestedGoogleApiProxyPort,
                @QueryParameter("googleApiProxyUsername") @RelativePath("../..") final String googleApiProxyUsername,
                @QueryParameter("googleApiProxyUsername") @RelativePath("../../..") final String nestedGoogleApiProxyUsername,
                @QueryParameter("googleApiProxyPassword") @RelativePath("../..") final String googleApiProxyPassword,
                @QueryParameter("googleApiProxyPassword") @RelativePath("../../..") final String nestedGoogleApiProxyPassword) {
            String effectiveRegion = firstNonEmpty(region, nestedRegion);
            String effectiveProjectId = firstNonEmpty(projectId, nestedProjectId);
            String effectiveCredentialsId = firstNonEmpty(credentialsId, nestedCredentialsId);
            String effectiveGoogleApiProxyHost = firstNonEmpty(googleApiProxyHost, nestedGoogleApiProxyHost);
            String effectiveGoogleApiProxyPort = firstNonEmpty(googleApiProxyPort, nestedGoogleApiProxyPort);
            String effectiveGoogleApiProxyUsername = firstNonEmpty(googleApiProxyUsername, nestedGoogleApiProxyUsername);
            String effectiveGoogleApiProxyPassword = firstNonEmpty(googleApiProxyPassword, nestedGoogleApiProxyPassword);
            checkPermissions(Jenkins.get(), Jenkins.ADMINISTER);
            ListBoxModel items = new ListBoxModel();

            if (Strings.isNullOrEmpty(effectiveRegion)) {
                return items;
            }

            try {
                ComputeClient compute = computeClient(
                        context,
                        effectiveCredentialsId,
                        GoogleApiProxyConfiguration.fromFormFields(
                                effectiveGoogleApiProxyHost,
                                effectiveGoogleApiProxyPort,
                                effectiveGoogleApiProxyUsername,
                                effectiveGoogleApiProxyPassword));
                List<Subnetwork> subnetworks = compute.listSubnetworks(effectiveProjectId, network, effectiveRegion);

                if (subnetworks.size() <= 1) {
                    items.add(new ListBoxModel.Option("", "", false));
                }
                if (subnetworks.isEmpty()) {
                    items.add(new ListBoxModel.Option("default", "default", true));
                    return items;
                }

                for (Subnetwork s : subnetworks) {
                    items.add(s.getName(), s.getSelfLink());
                }
                return items;
            } catch (IOException | IllegalArgumentException e) {
                String message = "Error retrieving subnetworks";
                LOGGER.log(Level.SEVERE, message, e);
                items.clear();
                items.add(new ListBoxModel.Option(message, "", true));
                return items;
            }
        }

        public FormValidation doCheckSubnetwork(@QueryParameter String value) {
            if (value.isEmpty()) {
                return FormValidation.error("Please select a subnetwork...");
            }

            return FormValidation.ok();
        }
    }
}
