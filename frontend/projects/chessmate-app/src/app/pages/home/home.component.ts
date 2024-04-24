import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ButtonModule} from 'primeng/button';
import {ChessboardComponent} from '@chessmate-app/shared/components';
import {LobbyComponent} from './components';
import { PlayerService } from '@chessmate-app/core/services';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  standalone: true,
  imports: [
    CommonModule,
    ChessboardComponent,
    ButtonModule,
    LobbyComponent,
  ],
})
export class HomeComponent {
  public playerId: string;

  constructor(private readonly playerService: PlayerService) {
    this.playerId = this.playerService.getPlayerId();
  }
}
