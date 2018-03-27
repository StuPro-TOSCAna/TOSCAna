import {Component, HostListener, Input, OnChanges, OnInit, SimpleChange} from '@angular/core';
import {ClientsTransformationsService} from '../../services/transformations.service';
import {LogEntry} from '../../api/index';
import 'rxjs/add/operator/takeWhile';
import {ClientCsarsService} from '../../services/csar.service';
import {isNullOrUndefined} from 'util';
import {MessageService} from '../../services/message.service';

@Component({
    selector: 'app-log',
    templateUrl: './log.component.html',
    styleUrls: ['./log.component.scss']
})
export class LogComponent implements OnInit, OnChanges {
    @Input() type;
    @Input() csarId;
    @Input() platformId;
    logs: LogEntry[] = [];
    visibleLogs = [];
    last = -1;
    logLevel = 'debug';
    logLevels = ['trace', 'debug', 'info', 'warn', 'error'];
    validLogLevels = this.logLevels;
    scrollToTop = false;

    constructor(private messageService: MessageService, private transformationProvider: ClientsTransformationsService,
                private csarsProvider: ClientCsarsService) {
    }

    getColor(level: string) {
        switch (level) {
            case 'ERROR':
                return 'red';
            case 'WARN':
                return 'orangered';
            case 'DEBUG':
                return 'darkslategray';
            case 'TRACE':
                return 'slategray';
            default:
                return 'black';
        }
    }

    setLogLevel() {
        const index = this.logLevels.indexOf(this.logLevel);
        this.validLogLevels = this.logLevels.slice(index, this.logLevels.length);
        this.visibleLogs = this.logs.filter(item =>
            this.validLogLevels.indexOf(item.level.toLowerCase()) !== -1);
    }

    getClassName(context: string) {
        if (!isNullOrUndefined(context)) {
            const arr = context.split('.');
            return arr[arr.length - 1];
        }
    }

    scroll() {
        const element: Element = document.getElementById('scroll-me');
        element.scrollTop = element.scrollHeight;

    }

    @HostListener('window:scroll')
    showScrollToTop() {
        const table: Element = document.getElementById('table');
        const tableHeight = table.scrollHeight;
        this.scrollToTop = tableHeight > document.body.offsetHeight && window.pageYOffset > 100;
    }

    refresh() {
        this.last += 1;
        if (this.type === 'transformation') {
            this.transformationProvider.getLogs(this.csarId, this.platformId, this.last).subscribe(data => {
                this.updateLogs(data);
            }, err => this.messageService.addErrorMessage('Failed to load logs'));
        } else if (this.type === 'csar') {
            this.csarsProvider.getLogs(this.csarId, this.last).subscribe(data => {
                this.updateLogs(data);
            }, err => this.messageService.addErrorMessage('Failed to load logs'));
        }
    }

    private updateLogs(data) {
        if (this.last !== data.end) {
            let logs: LogEntry[] = data.logs;
            for (let i = 0; i < logs.length; i++) {
                const item: LogEntry = logs[i];
                item.message = item.message.replace(/\ /g, '&nbsp;');
                if (item.context === 'EOL') {
                    logs = logs.splice(i, 1);
                }
            }
            this.logs.push.apply(this.logs, data.logs);
            this.last = data.end;
            this.scroll();
        }
        this.visibleLogs = this.logs;
        this.setLogLevel();
    }

    ngOnChanges(changes: { [propKey: string]: SimpleChange }) {
        this.logs = [];
        this.last = -1;
        this.refresh();
    }

    ngOnInit() {
        this.last = -1;
        this.refresh();
    }

    goToTop() {
        document.body.scrollTop = document.documentElement.scrollTop = 0;
    }
}
