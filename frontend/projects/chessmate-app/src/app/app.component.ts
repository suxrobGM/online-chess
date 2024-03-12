import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {ToastModule} from 'primeng/toast';
import {AuthService, ThemeService} from '@chessmate-app/core/services';
import {TopbarComponent} from '@chessmate-app/layout';

@Component({
  selector: 'app-root',
  standalone: true,
  templateUrl: './app.component.html',
  imports: [
    TopbarComponent,
    RouterOutlet,
    ToastModule,
  ],
})
export class AppComponent {
  constructor(
    //private readonly authService: AuthService,
    private readonly themeService: ThemeService,
  )
  {
    this.themeService.applyThemeFromStorage();
  }

  // isAuthenticated(): boolean {
  //   return this.authService.isAuthenticated();
  // }
}
