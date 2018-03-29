import {Component, OnInit} from '@angular/core';
import {Message, SuccessMessage} from '../../model/message';
import {MessageService} from '../../services/message.service';
import {IntervalObservable} from 'rxjs/observable/IntervalObservable';

@Component({
    selector: 'app-message',
    templateUrl: './message.component.html',
    styleUrls: ['./message.component.scss']
})
export class MessageComponent implements OnInit {
    messages: Message[] = [];

    constructor(private messageService: MessageService) {
    }

    ngOnInit() {
        this.listenForNewMessages();
        this.checkIfThereAreMessagesToRemove();
    }

    /**
     * removes visible messages after 2000 from the array
     */
    private checkIfThereAreMessagesToRemove() {
        IntervalObservable.create(2000).subscribe(() => {
            const currentDate = Date.now();
            for (const message of this.messages) {
                const diff = currentDate - message.timestamp;
                if (diff >= 2000) {
                    this.removeMessage(message);
                }
            }
        });
    }

    /**
     * waits for new messages
     */
    private listenForNewMessages() {
        this.messageService.messages.subscribe(messages => {
            this.messages = this.messages.concat(messages);
        });
    }

    removeMessage(message: Message) {
        this.messages.splice(this.messages.indexOf(message), 1);
    }

    getMessageBackgroundColor(message: Message) {
        if (message instanceof SuccessMessage) {
            return '#28a745';
        } else {
            return '#db3747';
        }
    }

}
