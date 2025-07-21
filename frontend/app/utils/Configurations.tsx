
export type StorageKeys = {
    buildTool: string,
    language: string,
    platform: string,
}

export type DocumentConfiguration = {
    key: string,
    name: string,
}

export const BuildTools: Record<string, DocumentConfiguration> = {
    "gradle": {
        key: "gradle",
        name: "Gradle",
    },
    "gradle_kts": {
        key: "gradle_kts",
        name: "Gradle KTS",
    },
    "maven": {
        key: "maven",
        name: "Maven",
    }
}

export const Languages: Record<string, DocumentConfiguration> = {
    "java": {
        key: "java",
        name: "Java",
    },
    "kotlin": {
        key: "kotlin",
        name: "Kotlin",
    }
}

export const Platforms: Record<string, DocumentConfiguration> = {
    "bukkit": {
        key: "bukkit",
        name: "Bukkit",
    },
    "paper": {
        key: "paper",
        name: "Paper",
    },
    "fabric": {
        key: "fabric",
        name: "Fabric",
    }
}
