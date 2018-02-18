import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../../environments/environment';
import {Health} from '../../model/health';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class HealthService {

    constructor(private httpClient: HttpClient) {
    }

    getHealthStatus(): Observable<Health> {
        const headers = new HttpHeaders();
        headers.set('Accept', 'application/hal+json');
        console.log(`${environment.apiUrl}/api/status/health`);
        return this.httpClient.get<Health>(`${environment.apiUrl}/api/status/health`, {
            headers: headers
        });
    }
}
