import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ApplicationConfig, importProvidersFrom} from '@angular/core';
import {provideRouter} from '@angular/router';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {BrowserModule} from '@angular/platform-browser';
import {provideOAuthClient} from 'angular-oauth2-oidc';
import {MessageService} from 'primeng/api';
import {authInterceptor} from '@chessmate-app/core/interceptors';
import {AppRoutes} from './app.routes';

export const AppConfig: ApplicationConfig = {
  providers: [
    provideRouter(AppRoutes),
    //provideHttpClient(withInterceptors([authInterceptor])),
    provideHttpClient(),
    provideOAuthClient(),
    importProvidersFrom(BrowserModule, BrowserAnimationsModule),
    MessageService,
  ]
};
