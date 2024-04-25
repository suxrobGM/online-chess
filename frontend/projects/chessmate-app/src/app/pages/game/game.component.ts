import {
  Component,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import {ButtonModule} from 'primeng/button';
import {CardModule} from 'primeng/card';
import {Subscription} from 'rxjs';
import {HistoryMove} from 'ngx-chess-board';
import {ChessboardComponent, GameResultDialogComponent} from '@chessmate-app/shared/components';
import {GameDto, MakeMoveCommand, MoveDto, PlayerColor} from '@chessmate-app/core/models';
import {MatchService, PlayerService} from '@chessmate-app/core/services';


@Component({
  selector: 'app-game',
  standalone: true,
  templateUrl: './game.component.html',
  styleUrl: './game.component.scss',
  imports: [
    ChessboardComponent,
    ButtonModule,
    CardModule,
    GameResultDialogComponent,
  ]
})
export class GameComponent implements OnInit, OnDestroy {
  private currentPlayerColor?: PlayerColor;
  private receivedMoveSubscription?: Subscription;
  public isLoading = false;
  public game: GameDto | null = null;
  public boardOrientation: 'white' | 'black' = 'white';
  public pgn = '';
  public currentTurn?: PlayerColor;
  public gameResultDialogVisible = false;
  public gameResult?: 'win' | 'lose' | 'draw';

  @ViewChild('chessboardRef')
  private chessboardRef?: ChessboardComponent;
  
  constructor(
    private readonly matchService: MatchService,
    private readonly playerService: PlayerService)
  {
  }

  ngOnInit(): void {
    this.game = this.matchService.getCurrentMatch();
    this.currentTurn = this.game?.currentTurn;

    this.currentPlayerColor = this.game?.whitePlayerId === this.playerService.getPlayerId() ? PlayerColor.WHITE : PlayerColor.BLACK;

    this.receivedMoveSubscription = this.matchService.receivedMove$.subscribe((move) => {
      this.handleMoveUpdate(move);
    });

    if (this.isCurrentPlayer(this.game?.blackPlayerId)) {
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

  sendMove(moveData: HistoryMove) {
    if (!this.game) {
      return;
    }

    const from = moveData.move.substring(0, 2);
    const to = moveData.move.substring(2, 4);

    const command: MakeMoveCommand = {
      gameId: this.game.id,
      color: moveData.color === 'white' ? PlayerColor.WHITE : PlayerColor.BLACK,
      from: from,
      to: to,
      isCheckmate: moveData.mate,
      isStalemate: moveData.stalemate,
    };
    
    this.matchService.makeMove(command);
  }

  private handleMoveUpdate(move: MoveDto) {
    if (!this.chessboardRef) {
      return;
    }

    this.chessboardRef.move(move.from, move.to);
    this.currentTurn = move.color === PlayerColor.WHITE ? PlayerColor.BLACK : PlayerColor.WHITE;
    this.pgn = this.chessboardRef.getPgn();

    if (move.isCheckmate) {
      this.gameResult = this.currentPlayerColor === move.color ? 'lose' : 'win';
      this.gameResultDialogVisible = true;
    }
    else if (move.isStalemate) {
      this.gameResult = 'draw';
      this.gameResultDialogVisible = true;
    }
  }
}
