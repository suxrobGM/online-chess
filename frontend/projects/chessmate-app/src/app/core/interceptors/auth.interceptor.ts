import {inject} from '@angular/core';
import {
  HttpRequest,
  HttpInterceptorFn,
  HttpHandlerFn,
} from '@angular/common/http';
import {OAuthStorage} from 'angular-oauth2-oidc';
import {APP_CONFIG} from '@chessmate-app/configs';

export const authInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn) => {
  const authStorage = inject(OAuthStorage);
  const token = authStorage.getItem('access_token');
  const headers = {Authorization: ''};

  if (!req.url.startsWith(APP_CONFIG.apiUrl)) {
    return next(req);
  }

  headers['Authorization'] = `Bearer ${token}`;

  const newRequest = req.clone({
    setHeaders: headers,
  });

  return next(newRequest);
}
