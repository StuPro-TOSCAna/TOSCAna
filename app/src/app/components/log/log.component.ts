import {Component, Input, OnChanges, OnInit, SimpleChange} from '@angular/core';
import {TransformationsProvider} from '../../providers/transformations/transformations.provider';
import {LogEntry} from '../../api/index';
import 'rxjs/add/operator/takeWhile';

@Component({
    selector: 'app-log',
    templateUrl: './log.component.html',
    styleUrls: ['./log.component.scss']
})
export class LogComponent implements OnInit, OnChanges {
    @Input() csarId;
    @Input() platformId;
    @Input() status;
    logs: LogEntry[];
    last = 0;


    constructor(private transformationProvider: TransformationsProvider) {
    }

    refresh() {
        this.transformationProvider.getLogs(this.csarId, this.platformId, this.last).subscribe(data => {
            this.logs = data.logs;
            this.last = data.end;
            this.logs.push.apply(this.logs, data.logs);
        });
    }

    ngOnChanges(changes: { [propKey: string]: SimpleChange }) {
        this.refresh();
    }

    ngOnInit() {
    }

}
