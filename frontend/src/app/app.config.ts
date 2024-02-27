import {ApplicationConfig} from '@angular/core';
import {provideRouter} from '@angular/router';
import {ROUTES} from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [provideRouter(ROUTES)],
};
