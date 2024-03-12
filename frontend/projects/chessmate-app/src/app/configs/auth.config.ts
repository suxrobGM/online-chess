import {AuthConfig} from 'angular-oauth2-oidc';

export const AUTH_CONFIG: AuthConfig = {
  issuer: 'https://localhost:7001',
  redirectUri: window.location.origin,
  postLogoutRedirectUri: '/',
  clientId: 'planner.spa',
  responseType: 'code',
  scope: 'openid profile email offline_access planner.read planner.write',
  showDebugInformation: true,
};
