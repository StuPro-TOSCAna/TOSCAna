export class ViewState {
    csarId: string;

    constructor(csarId: string) {
        this.csarId = csarId;
    }
}

export class TransformationOpen extends ViewState {
    platform: string;

    constructor(csarId: string, platform: string) {
        super(csarId);
        this.platform = platform;
    }
}

export class InputFormOpen extends TransformationOpen {
    constructor(csarId: string, platform: string) {
        super(csarId, platform);
    }
}

export class CsarOpen extends ViewState {
    constructor(csarId: string) {
        super(csarId);
    }
}
