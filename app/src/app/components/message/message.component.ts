import {Component, OnInit} from '@angular/core';
import {Message, SuccessMessage} from '../../model/message';
import {MessageService} from '../../providers/message/message.service';
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
        this.messageService.messages.subscribe(messages => {
            this.messages = messages;
        });
        IntervalObservable.create(2000).subscribe(() => {
            const currentDate = Date.now();
            for (const message of this.messages) {
                const diff = currentDate - message.timestamp;
                if (diff >= 2000) {
                    this.delete(message);
                }
            }
        });
    }

    delete(message: Message) {
        this.messages.splice(this.messages.indexOf(message), 1);
    }

    getColor(message: Message) {
        if (message instanceof SuccessMessage) {
            return '#28a745';
        } else {
            return '#db3747';
        }
    }

}
