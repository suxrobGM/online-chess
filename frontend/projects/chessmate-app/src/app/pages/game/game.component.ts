import {
  Component,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
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
  public boardOrientation: 'white' | 'black' = 'white';
  public pgn = '';

  @ViewChild('chessboardRef')
  private chessboardRef?: ChessboardComponent;
  
  constructor(
    private readonly matchService: MatchService,
    private readonly playerService: PlayerService)
  {
  }

  ngOnInit(): void {
    this.game = this.matchService.getCurrentMatch();

    this.receivedMoveSubscription = this.matchService.receivedMove$.subscribe((move) => {
      if (!this.chessboardRef) {
        return;
      }
      
      this.chessboardRef.move(move.from, move.to);
      this.pgn = this.chessboardRef.getPgn();
    });

    if (this.game?.blackPlayerId === this.playerService.getPlayerId()) {
      console.log('Reversing board');
      this.boardOrientation = 'black';
    }
  }

  ngOnDestroy(): void {
    this.receivedMoveSubscription?.unsubscribe();
  }

  resignGame(): void {
  }

  cancelGame(): void {
  }

  offerDraw(): void {
  }

  isCurrentPlayer(playerId?: string): boolean {
    return playerId === this.playerService.getPlayerId();
  }
}
