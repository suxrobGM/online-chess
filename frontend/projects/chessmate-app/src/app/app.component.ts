import {Component, OnInit} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {ToastModule} from 'primeng/toast';
import {AuthService, MatchService, ThemeService} from '@chessmate-app/core/services';
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
export class AppComponent implements OnInit {
  constructor(
    //private readonly authService: AuthService,
    private readonly matchService: MatchService,
    private readonly themeService: ThemeService,
  )
  {
    this.themeService.applyThemeFromStorage();
  }

  ngOnInit(): void {
    // Connect to the WebSocket server when the app starts.
    this.matchService.connect();
  }

  // isAuthenticated(): boolean {
  //   return this.authService.isAuthenticated();
  // }
}
