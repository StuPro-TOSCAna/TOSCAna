package org.opentosca.toscana.plugins.cloudformation.util;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.opentosca.toscana.plugins.cloudformation.mapper.CapabilityMapper.InstanceType;

import com.google.common.collect.ImmutableList;

/**
 Utility class for mapping values.

 @see org.opentosca.toscana.plugins.cloudformation.mapper.CapabilityMapper */
public class MappingUtils {

    /**
     Gets a {@link List} of memory size which correspond with {@code numCpus}.
     <br>
     The memory integers are pulled from every {@link InstanceType}
     , from the {@code instanceTypes} {@link ImmutableList}, that also has {@code numCpus} as cpu value.

     @param numCpus       the numCpus to find the corresponding memory values for
     @param instanceTypes the instance types to search in
     @return a {@link List} of valid memory values that correspond with {@code numCpus}
     */
    public static List<Integer> getMemByCpu(Integer numCpus, ImmutableList<InstanceType> instanceTypes) {
        return instanceTypes.stream()
            .filter(u -> u.getNumCpus().equals(numCpus))
            .map(InstanceType::getMemSize)
            .collect(Collectors.toList());
    }

    /**
     Gets a {@link List} of numbers of cpus which correspond with {@code memSize}.
     <br>
     The numbers of cpu integers are pulled from every {@link InstanceType}
     , from the {@code instanceTypes} {@link ImmutableList}, that also has {@code memSize} as memory value.

     @param memSize       the memSize to find the corresponding number of cpus for
     @param instanceTypes the instance types to search in
     @return a {@link List} of numCpu values that correspond with {@code memSize}
     */
    public static List<Integer> getCpuByMem(Integer memSize, ImmutableList<InstanceType> instanceTypes) {
        return instanceTypes.stream()
            .filter(u -> u.getMemSize().equals(memSize))
            .map(InstanceType::getNumCpus)
            .collect(Collectors.toList());
    }

    /**
     Gets the {@link String} representation of an {@link InstanceType}
     that has both {@code numCpus} and {@code memSize} as values.

     @param numCpus       the number of cpus the {@link InstanceType}
     should have
     @param memSize       the memory size the {@link InstanceType}
     should have
     @param instanceTypes the {@link ImmutableList} to choose from
     @return the string representation of an {@link InstanceType}
     or {@code ""} if none was found
     */
    public static String getInstanceType(Integer numCpus, Integer memSize, ImmutableList<InstanceType> instanceTypes) {
        Optional<InstanceType> instanceType = instanceTypes.stream()
            .filter(u -> u.getNumCpus().equals(numCpus) && u.getMemSize().equals(memSize))
            .findAny();
        if (instanceType.isPresent()) {
            return instanceType.get().getType();
        } else {
            return "";
        }
    }

    /**
     Checks if the value is in the {@link List} {@code checker}, if not take the next bigger value. If there is none
     throw an IllegalArgumentException.

     @param value   the value to check
     @param checker the {@link List} to check in
     @return a valid value of the checker list
     @throws IllegalArgumentException if the value is too big
     */
    public static Integer checkValue(Integer value, List<Integer> checker) throws IllegalArgumentException {
        if (!checker.contains(value)) {
            for (Integer num : checker) {
                if (num > value) {
                    return num;
                }
            }
            throw new IllegalArgumentException("Can't support value: " + value);
        } else {
            return value;
        }
    }
}
