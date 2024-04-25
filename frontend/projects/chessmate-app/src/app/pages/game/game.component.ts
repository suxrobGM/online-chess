import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ButtonModule} from 'primeng/button';
import {CardModule} from 'primeng/card';
import {ChessboardComponent} from '@chessmate-app/shared/components';
import {GameDto} from '@chessmate-app/core/models';
import {MatchService, PlayerService} from '@chessmate-app/core/services';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-game',
  standalone: true,
  templateUrl: './game.component.html',
  styleUrl: './game.component.scss',
  imports: [
    ChessboardComponent,
    ButtonModule,
    CardModule,
  ]
})
export class GameComponent implements OnInit, OnDestroy {
  //private currentPlayerId: string | null = null;
  private receivedMoveSubscription?: Subscription;
  public isLoading = false;
  public game: GameDto | null = null;

  @ViewChild('chessboardRef')
  private chessboardRef?: ChessboardComponent;
  
  constructor(
    private readonly matchService: MatchService,
    private readonly playerService: PlayerService)
  {
  }

  ngOnDestroy(): void {
    this.receivedMoveSubscription?.unsubscribe();
  }

  ngOnInit(): void {
    this.game = this.matchService.getCurrentMatch();

    this.receivedMoveSubscription = this.matchService.receivedMove$.subscribe((move) => {
      this.chessboardRef?.move(move.from, move.to);
    });

    if (this.game?.blackPlayerId === this.playerService.getPlayerId()) {
      this.chessboardRef?.reverseBoard();
    }
  }

  resignGame(): void {
  }

  cancelGame(): void {
  }

  offerDraw(): void {
  }
}
