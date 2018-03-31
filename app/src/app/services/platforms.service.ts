import {Injectable} from '@angular/core';
import {PlatformResponse, PlatformsService} from '../api/index';

@Injectable()
export class ClientPlatformsService {
    platforms: PlatformResponse[] = [];

    constructor(private platformsService: PlatformsService) {
        this.loadPlatforms();
    }

    public loadPlatforms() {
        this.platformsService.getPlatformsUsingGET().toPromise().then(data => {
            this.platforms = data._embedded.platform;
        }, err => {
            return Promise.reject('Getting Platforms failed.');
        });
        return new Promise<PlatformResponse[]>(resolve => resolve(this.platforms));
    }


    public getFullPlatformName(id: string): string {
        return this.platforms.find(item => item.id === id).name;
    }

    public getPlatforms(): PlatformResponse[] {
        return this.platforms;
    }

}
