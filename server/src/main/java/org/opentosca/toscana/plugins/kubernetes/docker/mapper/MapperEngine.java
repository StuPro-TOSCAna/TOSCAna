package org.opentosca.toscana.plugins.kubernetes.docker.mapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.opentosca.toscana.model.capability.OsCapability;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.model.DockerImage;
import org.opentosca.toscana.plugins.kubernetes.docker.mapper.model.DockerImageTag;

import com.vdurmont.semver4j.Semver;

import static org.opentosca.toscana.model.capability.OsCapability.Architecture.x86_64;
import static org.opentosca.toscana.model.capability.OsCapability.Type.LINUX;
import static org.opentosca.toscana.plugins.kubernetes.docker.mapper.MapperConstants.ARCHITECTURE_MAP;
import static org.opentosca.toscana.plugins.kubernetes.docker.mapper.MapperConstants.DEFAULT_IMAGE_DISTRO;
import static org.opentosca.toscana.plugins.kubernetes.docker.mapper.MapperConstants.DEFAULT_IMAGE_PATH;
import static org.opentosca.toscana.plugins.kubernetes.docker.mapper.MapperUtils.TAG_COMPARATOR_MINOR_VERSION;
import static org.opentosca.toscana.plugins.kubernetes.docker.mapper.MapperUtils.anythingSet;

/**
 This class wraps the mapping functionality of the Base image mapper
 */
class MapperEngine {

    TagStorage tagStorage;

    public MapperEngine(TagStorage tagStorage) {
        this.tagStorage = tagStorage;
    }

    /**
     This method attempts to map a OsCapability to a docker base image.
     If the mapping fails a UnsopportedOperationException is thrown. Reasons for failiure are: Invalid Architecture,
     Unsupported type, Unknown version...
     */
    public String mapToBaseImage(OsCapability capability) {
        //Return default image if noting is present
        if (!anythingSet(capability)) {
            return DEFAULT_IMAGE_PATH;
        }

        String tag = null;
        String distro = null;

        //Check if the type (if set) is not linux. If so: the mapping fails
        if (capability.getType().isPresent() && !capability.getType().get().equals(LINUX)) {
            throw new UnsupportedOperationException("Could not map to base image. " +
                "Only linux based images are supported");
        }

        //Check if the Distribution (if set) can be mapped to a docker image. If thats possible the mapping will continue
        //otherwise it will fail
        if (capability.getDistribution().isPresent() && !hasKnownDistributionName(capability)) {
            throw new UnsupportedOperationException("Could not map to base image, " +
                "because the distribution '" + capability.getDistribution().get() + "' is not supported.");
        } else if (!capability.getDistribution().isPresent()) {
            tag = "latest";
            distro = DEFAULT_IMAGE_DISTRO;
        } else {
            distro = convertDistributionName(capability);
            if (!capability.getVersion().isPresent()) {
                tag = "latest";
            } else {
                String version = capability.getVersion().get();
                DockerImage img = tagStorage.get(distro);
                Optional<DockerImageTag> imgTag = img.findTagByName(version);
                if (imgTag.isPresent()) {
                    tag = imgTag.get().getName();
                } else {
                    tag = lookForPossibleTag(version, img);
                }
            }
        }
        return performMapping(capability, tag, distro);
    }

    /**
     This method looks for a possible tag for the given version and Docker Image, Throws a unsupported operation
     exception if it fails
     */
    private String lookForPossibleTag(String version, DockerImage img) {
        String tag;//Check if the Version definition is too specific e.g. ubuntu 16.04.3 that is non existant on dockerhub
        Semver semVer = new Semver(version, Semver.SemverType.LOOSE);

        List<DockerImageTag> eqMayorVersionTags = getTagsWithEqualMayorVersion(img, semVer.getMajor());

        List<DockerImageTag> possibleMinorImageTags = getPossibleMinorImageTags(semVer, eqMayorVersionTags);

        // If there is only one possibility return it.
        if (possibleMinorImageTags.size() == 1) {
            tag = possibleMinorImageTags.get(0).getName();
        } else {
            tag = determineTagFromMinorVersion(possibleMinorImageTags, semVer);
        }
        return tag;
    }

    /**
     Searches for tags with a equal mayor version
     */
    private List<DockerImageTag> getTagsWithEqualMayorVersion(DockerImage img, int versionNumber) {
        //Get all tags with the same mayor version (index 0) this one is required to be equal
        List<DockerImageTag> eqMajorVersionTags = img.getVersionableTags().stream()
            .filter(e -> e.toVersion().getMajor() == versionNumber).collect(Collectors.toList());

        //Fail if there are no tags with the same major version
        if (eqMajorVersionTags.size() == 0) {
            throw new UnsupportedOperationException("Could not find base image. " +
                "No tag with major version '" + versionNumber + "' found.");
        }
        return eqMajorVersionTags;
    }

    /**
     Looks for tags that have none or a greater or equal minor version
     */
    private List<DockerImageTag> getPossibleMinorImageTags(Semver version, List<DockerImageTag> eqMayorVersionTags) {
        // Look for minor version candidates (has to be be greater or equal to the version number given)
        // A check if the value exists is not needed because the array length is at least 2
        List<DockerImageTag> possibleMinorImageTags = eqMayorVersionTags.stream()
            .filter(e -> e.toVersion().getMinor() >= version.getMinor()).collect(Collectors.toList());
        possibleMinorImageTags.sort(TAG_COMPARATOR_MINOR_VERSION);

        // If no image fulfilling the condition is found, the mapping fails
        if (possibleMinorImageTags.size() == 0) {
            throw new UnsupportedOperationException("Could not find base image. " +
                "No tag with greater or equal minor version '" + version + "' found.");
        }
        return possibleMinorImageTags;
    }

    /**
     Tries to determine the tag from the list of possible minor versions
     */
    private String determineTagFromMinorVersion(List<DockerImageTag> possibleTags, Semver version) {
        DockerImageTag resultTag = null;
        int i = 0;
        DockerImageTag tag;
        Semver tagVersion;
        do {
            tag = possibleTags.get(i);
            tagVersion = tag.toVersion();
            if (tagVersion.getMinor() == version.getMinor()) {
                int j = i;
                DockerImageTag prev = null;
                DockerImageTag t = null;
                Semver tv;
                do {
                    prev = t;
                    t = possibleTags.get(j);
                    tv = t.toVersion();
                    j++;
                } while (tv.getMinor().equals(version.getMinor()) && j < possibleTags.size());
                resultTag = prev;
            }
            i++;
        } while (i < possibleTags.size());
        if (resultTag == null) {
            throw new UnsupportedOperationException("Mapping failed");
        }
        return resultTag.getName();
    }

    /**
     Perfoms the mapping to the base image
     */
    private String performMapping(OsCapability capability, String tag, String distro) {
        DockerImage img = tagStorage.get(distro);
        String architecture = getCapabilityArchitecture(capability)
            .orElseThrow(() -> new UnsupportedOperationException("Architecture not supported!"));

        DockerImageTag imgTag = img.findTagByName(tag).get();
        if (imgTag.isSupported(architecture)) {
            return img.getUsername() + "/" + img.getRepository() + ":" + imgTag.getName();
        } else {
            throw new UnsupportedOperationException("Could not map to base image. " +
                "The image '" + img.getUsername() + "/" + img.getRepository() + ":" + tag + "' " +
                "does not support the architecture " + architecture);
        }
    }

    /**
     Converts the architecture of the capability to a architecture supported by docker
     */
    private Optional<String> getCapabilityArchitecture(OsCapability capability) {
        return Optional.ofNullable(ARCHITECTURE_MAP.get(capability.getArchitecture().orElse(x86_64)));
    }

    private boolean hasKnownDistributionName(OsCapability capability) {
        if (capability.getDistribution().isPresent()) {
            String distroName = convertDistributionName(capability);
            DockerImage image = tagStorage.get(distroName);
            return image != null;
        }
        return false;
    }

    private String convertDistributionName(OsCapability capability) {
        return capability.getDistribution().get().name()         // Retrieve distro name
            .toLowerCase()                                       // Convert to lower case
            .replace(" ", "");                // Strip spaces from string
    }
}
