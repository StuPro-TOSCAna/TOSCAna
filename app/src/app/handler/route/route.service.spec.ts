import { TestBed, inject } from '@angular/core/testing';

import { RouteHandler } from './route.service';

describe('RouteHandler', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [RouteHandler]
    });
  });

  it('should be created', inject([RouteHandler], (service: RouteHandler) => {
    expect(service).toBeTruthy();
  }));
});
