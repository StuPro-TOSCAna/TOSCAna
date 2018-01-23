import {Injectable} from '@angular/core';
import {PlatformResponse, PlatformsService} from '../../api/index';

@Injectable()
export class PlatformsProvider {
    platforms: PlatformResponse[] = [];

    constructor(private platformsService: PlatformsService) {
        this.loadPlatforms();
    }

    loadPlatforms() {
        this.platformsService.getPlatformsUsingGET().toPromise().then(data => {
            this.platforms = data._embedded.platform;
        }, err => {
            return Promise.reject('Getting Platforms failed.');
        });
        return new Promise<PlatformResponse[]>(resolve => resolve(this.platforms));
    }


    public getFullPlatformName(id: string) {
        return this.platforms.find(item => item.id === id).name;
    }

    public getPlatforms() {
        return this.platforms;
    }

}
