package org.opentosca.toscana.core.testutils;

public enum TestCategories {
    FAST("fast"),
    SLOW("slow"),
    SYSTEM("system"),
    ALL("all");

    private String identifier;

    TestCategories(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean shouldBeExecuted(TestCategories mode) {
        return this == ALL || mode == this;
    }

    public static TestCategories findByIdentifier(String id) {
        for (TestCategories mode : values()) {
            if (mode.identifier.equalsIgnoreCase(id)) {
                return mode;
            }
        }
        //Allow running in old categorization on ci
        if (id.equals("ci")) {
            return FAST;
        }
        return ALL;
    }

    public static TestCategories getCurrentTestMode() {
        String env = System.getenv("TEST_MODE");
        return findByIdentifier(env == null ? "all" : env);
    }
}
