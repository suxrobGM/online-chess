import {Component, OnInit} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {ToastModule} from 'primeng/toast';
import {
  AuthService,
  MatchService,
  PlayerService,
  ThemeService,
} from '@chessmate-app/core/services';
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
  public playerId: string;

  constructor(
    //private readonly authService: AuthService,
    private readonly matchService: MatchService,
    private readonly themeService: ThemeService,
    private readonly playerService: PlayerService,
  )
  {
    this.themeService.applyThemeFromStorage();
    this.playerId = this.playerService.getPlayerId();
  }

  ngOnInit(): void {
    // Connect to the WebSocket server when the app starts.
    this.matchService.connect();
  }

  // isAuthenticated(): boolean {
  //   return this.authService.isAuthenticated();
  // }
}
