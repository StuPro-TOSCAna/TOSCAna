interface DiskSpace {
    status: string;
    total: number;
    free: number;
    threshhold: number;
}

export interface Health {
    status: string;
    transformer: Transformer;
    diskSpace: DiskSpace;
}

export interface Transformer {
    status: string;
    installed_plugins: string[];
    running_transformations: TransformationInterface[];
    errored_transformations: TransformationInterface[];
}

export interface TransformationInterface {
    csar: string;
    platform: string;
}
