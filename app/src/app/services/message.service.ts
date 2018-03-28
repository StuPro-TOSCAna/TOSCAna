import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {ErrorMessage, Message} from '../model/message';

@Injectable()
export class MessageService {
    _messages: BehaviorSubject<Message[]>;
    dataStore: {
        messages: Message[];
    };

    constructor() {
        this.dataStore = {messages: []};
        this._messages = new BehaviorSubject<Message[]>([]);
    }

    get messages() {
        return this._messages.asObservable();
    }

    public add(message: Message) {
        this.dataStore.messages.push(message);
        this._messages.next(Object.assign({}, this.dataStore).messages);
        this.dataStore.messages = [];
    }

    public addErrorMessage(message: string) {
        this.add(new ErrorMessage(message));
    }
}
