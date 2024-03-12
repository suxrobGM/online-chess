import {Injectable} from '@angular/core';
import {OAuthService} from 'angular-oauth2-oidc';
import {Subject} from 'rxjs';
import {AUTH_CONFIG} from '@chessmate-app/configs';
import {UserDto} from '@chessmate-app/core/models';

@Injectable({providedIn: 'root'})
export class AuthService {
  private userData: UserDto | null = null;
  private readonly userDataSubject = new Subject<UserDto | null>();
  public readonly userData$ = this.userDataSubject.asObservable();

  constructor(private readonly oauthService: OAuthService) {
    this.oauthService.configure(AUTH_CONFIG);

    this.oauthService.loadDiscoveryDocumentAndTryLogin().then(async () => {
      if (this.oauthService.hasValidAccessToken()) {
        const userProfile = await this.oauthService.loadUserProfile();
        this.setUserData(userProfile as UserProfile);
      }
    });
  }

  login() {
    this.oauthService.initLoginFlow();
  }

  logout() {
    this.oauthService.logOut();
  }

  getUserData(): UserDto | null {
    return this.userData;
  }

  isAuthenticated(): boolean {
    return this.oauthService.hasValidAccessToken();
  }

  private setUserData(userData: UserProfile) {
    this.userData = {
      id: userData.info.sub,
      email: userData.info.email,
      firstName: userData.info.given_name,
      lastName: userData.info.family_name,
    };

    this.userDataSubject.next(this.userData);
  }
}

interface UserProfile {
  info: {
    sub: string;
    name: string;
    given_name: string;
    family_name: string;
    middle_name: string;
    preferred_username: string;
    email: string;
    email_verified: boolean;
  }
}
