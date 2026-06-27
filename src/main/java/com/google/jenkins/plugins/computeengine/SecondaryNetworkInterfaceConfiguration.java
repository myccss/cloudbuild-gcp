package com.google.jenkins.plugins.computeengine;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import java.util.Iterator;
import java.util.List;
import jenkins.model.Jenkins;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

@Getter
@Setter(onMethod = @_(@DataBoundSetter))
@EqualsAndHashCode
public class SecondaryNetworkInterfaceConfiguration
        extends AbstractDescribableImpl<SecondaryNetworkInterfaceConfiguration> {
    private NetworkConfiguration networkConfiguration;
    private NetworkInterfaceIpStackMode networkInterfaceIpStackMode;

    @DataBoundConstructor
    public SecondaryNetworkInterfaceConfiguration() {
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<SecondaryNetworkInterfaceConfiguration> {
        @Override
        public String getDisplayName() {
            return "Secondary network interface";
        }

        @SuppressWarnings("unused") // jelly
        public static NetworkConfiguration defaultNetworkConfiguration() {
            return InstanceConfiguration.DescriptorImpl.defaultNetworkConfiguration();
        }

        @SuppressWarnings("unused") // jelly
        public List<NetworkConfiguration.NetworkConfigurationDescriptor> getNetworkConfigurationDescriptors() {
            List<NetworkConfiguration.NetworkConfigurationDescriptor> descriptors =
                    Jenkins.get().getDescriptorList(NetworkConfiguration.class);
            Iterator<NetworkConfiguration.NetworkConfigurationDescriptor> iterator = descriptors.iterator();
            while (iterator.hasNext()) {
                NetworkConfiguration.NetworkConfigurationDescriptor descriptor = iterator.next();
                if (descriptor.clazz.getName().equals("NetworkConfiguration")) {
                    iterator.remove();
                }
            }
            return descriptors;
        }

        @SuppressWarnings("unused") // jelly
        public List<NetworkInterfaceIpStackMode.Descriptor> getNetworkInterfaceIpStackModeDescriptors() {
            return ExtensionList.lookup(NetworkInterfaceIpStackMode.Descriptor.class);
        }
    }
}