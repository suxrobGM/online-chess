import {ApplicationConfig} from '@angular/core';
import {provideRouter} from '@angular/router';
import {AppRoutes} from './app.routes';

export const AppConfig: ApplicationConfig = {
  providers: [provideRouter(AppRoutes)]
};
