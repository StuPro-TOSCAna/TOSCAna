package org.opentosca.toscana.plugins.cloudformation.util;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.opentosca.toscana.plugins.cloudformation.mapper.CapabilityMapper;

import com.google.common.collect.ImmutableList;

public class MappingUtils {
    public static List<Integer> getMemByCpu(Integer numCpus, ImmutableList<CapabilityMapper.InstanceType> instanceTypes) {
        return instanceTypes.stream()
            .filter(u -> u.getNumCpus().equals(numCpus))
            .map(CapabilityMapper.InstanceType::getMemSize)
            .collect(Collectors.toList());
    }

    public static List<Integer> getCpuByMem(Integer memSize, ImmutableList<CapabilityMapper.InstanceType> instanceTypes) {
        return instanceTypes.stream()
            .filter(u -> u.getMemSize().equals(memSize))
            .map(CapabilityMapper.InstanceType::getNumCpus)
            .collect(Collectors.toList());
    }

    public static String getInstanceType(Integer numCpus, Integer memSize, ImmutableList<CapabilityMapper.InstanceType> instanceTypes) {
        Optional<CapabilityMapper.InstanceType> instanceType = instanceTypes.stream()
            .filter(u -> u.getNumCpus().equals(numCpus) && u.getMemSize().equals(memSize))
            .findAny();
        if (instanceType.isPresent()) {
            return instanceType.get().getType();
        } else {
            return "";
        }
    }

    /**
     Check if the value is in the list checker, if not take the next bigger. If there is none throw an
     IllegalArgumentException

     @param value   The value to check.
     @param checker The List to check in.
     @return A valid value of the checker list.
     @throws IllegalArgumentException If the value is too big
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
