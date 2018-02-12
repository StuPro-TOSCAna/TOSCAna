export class Message {
    text: string;
    timestamp: number;

    constructor(text: string) {
        this.text = text;
        this.timestamp = Date.now();
    }
}

export class SuccessMessage extends Message {
    constructor(text: string) {
        super(text);
    }
}

export class ErrorMessage extends Message {
    constructor(text: string) {
        super(text);
    }
}
