package com.github.sirblobman.combatlogx.api.expansion;

import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public final class ExpansionComparator implements Comparator<Expansion> {
    @Override
    public int compare(@NotNull Expansion e1, @NotNull Expansion e2) {
        ExpansionDescription description1 = e1.getDescription();
        ExpansionDescription description2 = e2.getDescription();

        String expansionName1 = description1.getName();
        String expansionName2 = description2.getName();

        List<String> expansionDependencyList1 = description1.getExpansionDependencies();
        List<String> expansionDependencyList2 = description2.getExpansionDependencies();
        if (expansionDependencyList1.contains(expansionName2) && expansionDependencyList2.contains(expansionName1)) {
            throw new IllegalStateException("Cyclic Dependency: " + expansionName1 + ", " + expansionName2);
        }

        if (expansionDependencyList1.contains(expansionName2)) {
            return -1;
        }

        if (expansionDependencyList2.contains(expansionName1)) {
            return 1;
        }

        return expansionName1.compareTo(expansionName2);
    }
}
