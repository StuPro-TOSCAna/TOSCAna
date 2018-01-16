export * from './csars.service';
import { CsarsService } from './csars.service';
export * from './hypermedia.service';
import { HypermediaService } from './hypermedia.service';
export * from './platforms.service';
import { PlatformsService } from './platforms.service';
export * from './transformations.service';
import { TransformationsService } from './transformations.service';
export const APIS = [CsarsService, HypermediaService, PlatformsService, TransformationsService];
