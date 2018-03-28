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
    logEntries: LogEntry[] = [];
    visibleLogEntries: LogEntry[] = [];
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

    /**
     * triggered if log level is changes
     * filters logs for the given log levels and the levels below
     */
    setLogLevel() {
        const index = this.logLevels.indexOf(this.logLevel);
        this.validLogLevels = this.logLevels.slice(index, this.logLevels.length);
        this.visibleLogEntries = this.logEntries.filter(item =>
            this.validLogLevels.indexOf(item.level.toLowerCase()) !== -1);
    }

    /**
     * splices the class name from the log entry context
     * @param {string} context
     * @returns {string} java class name
     */
    getJavaClassNameFromContext(context: string) {
        if (!isNullOrUndefined(context)) {
            const arr = context.split('.');
            return arr[arr.length - 1];
        }
    }

    /**
     * scrollToLogViewEnd to the bottom of the log
     * this is kinda buggy and sometimes does not really work
     * TODO find a better solution for autoscrolling logs
     */
    scrollToLogViewEnd() {
        const element: Element = document.getElementById('scrollToLogViewEnd-me');
        element.scrollTop = element.scrollHeight;
    }

    /**
     * shows the scroll to the top button if user scrolled about the half of the screen
     */
    @HostListener('window:scroll')
    showScrollToTop() {
        const table: Element = document.getElementById('table');
        const tableHeight = table.scrollHeight;
        this.scrollToTop = tableHeight > document.body.offsetHeight && window.pageYOffset > 100;
    }

    /**
     * fetches log entries published since the last time this method called
     * if method was never called fetch all entries that are available
     */
    loadNewLogEntries() {
        this.last += 1;
        if (this.type === 'transformation') {
            this.transformationProvider.getLogs(this.csarId, this.platformId, this.last).subscribe(data => {
                this.updateLogs(data);
            }, err => this.messageService.addErrorMessage('Failed to load logEntries'));
        } else if (this.type === 'csar') {
            this.csarsProvider.getCsarLogs(this.csarId, this.last).subscribe(data => {
                this.updateLogs(data);
            }, err => this.messageService.addErrorMessage('Failed to load logEntries'));
        }
    }

    /**
     * replaces white spaces with unicode chars because angular removes normal whitespaces
     * and the indentation gets lost
     * @param data result from the server
     */
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
            this.logEntries.push.apply(this.logEntries, data.logs);
            this.last = data.end;
            this.scrollToLogViewEnd();
        }
        this.setLogLevel();
    }

    /**
     * reactes if the component gets new input,
     * f.ex. if the type of the "log" parent changs
     * @param {{[p: string]: SimpleChange}} changes
     */
    ngOnChanges(changes: { [propKey: string]: SimpleChange }) {
        this.logEntries = [];
        this.last = -1;
        this.loadNewLogEntries();
    }

    ngOnInit() {
        this.last = -1;
        this.loadNewLogEntries();
    }

    /**
     * scrolls to the to of the log
     */
    goToTop() {
        document.body.scrollTop = document.documentElement.scrollTop = 0;
    }
}
