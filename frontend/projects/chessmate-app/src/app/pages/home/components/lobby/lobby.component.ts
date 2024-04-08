import {Component, OnInit} from '@angular/core';
import {TableModule} from 'primeng/table';
import {GameDto, GameStatus} from '@chessmate-app/core/models';
import {ApiService} from '@chessmate-app/core/services';

@Component({
  selector: 'app-lobby',
  standalone: true,
  templateUrl: './lobby.component.html',
  styleUrl: './lobby.component.scss',
  imports: [
    TableModule,
  ],
})
export class LobbyComponent implements OnInit {
  public openGames: GameDto[] = [];

  constructor(private readonly apiService: ApiService) {
  }

  ngOnInit(): void {
    this.fetchOpenGames();
  }

  joinGame(gameId: string): void {
  }

  private fetchOpenGames(): void {
    this.apiService.getGames({gameStatus: GameStatus.OPEN}).subscribe((result) => {
      this.openGames = result;
    });
  }
}
