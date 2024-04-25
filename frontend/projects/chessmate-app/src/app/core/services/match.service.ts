import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {RxStomp} from '@stomp/rx-stomp';
import {APP_CONFIG} from '@chessmate-app/configs';
import {Subject} from 'rxjs';
import {
  CancelGameCommand,
  ConnectPlayerCommand,
  CreateAnonymousGameCommand,
  CreateGameCommand,
  GameDto,
  JoinGameCommand,
  MakeMoveCommand,
  MoveDto,
} from '@chessmate-app/core/models';
import {PlayerService} from './player.service';


@Injectable({providedIn: 'root'})
export class MatchService {
  private readonly stomp: RxStomp;
  private readonly baseUrl = APP_CONFIG.wsUrl;
  private readonly gameAdded = new Subject<GameDto>();
  private readonly gameRemoved = new Subject<GameDto>();
  private readonly receivedMove = new Subject<MoveDto>();
  private currentMatch: GameDto | null = null;

  public readonly gameAdded$ = this.gameAdded.asObservable();
  public readonly gameRemoved$ = this.gameRemoved.asObservable();
  public readonly receivedMove$ = this.receivedMove.asObservable();

  constructor(
    private readonly playerService: PlayerService,
    private readonly router: Router)
  {
    this.stomp = new RxStomp();

    this.stomp.configure({
      brokerURL: this.baseUrl,
      debug: (str) => console.log(str)
    });
  }

  /**
   * Initiates a connection to the WebSocket server.
   */
  connect(): void {
    this.stomp.activate();

    const connectPlayer: ConnectPlayerCommand = {
      playerId: this.playerService.getPlayerId(),
    };

    this.stomp.publish({
      destination: '/app/player/connect',
      body: JSON.stringify(connectPlayer),
    });

    this.subscribeToGameEvents();
  }

  getCurrentMatch(): GameDto | null {
    return this.currentMatch;
  }

  createGame(command: CreateGameCommand): void {
    this.stomp.publish({
      destination: '/app/game/create',
      body: JSON.stringify(command),
    });
  }

  createAnonymousGame(command: CreateAnonymousGameCommand): void {
    this.stomp.publish({
      destination: '/app/game/createAnonymous',
      body: JSON.stringify(command),
    });
  }

  cancelGame(gameId: string): void {
    const command: CancelGameCommand = {
      gameId: gameId,
    };

    this.stomp.publish({
      destination: '/app/game/cancel',
      body: JSON.stringify(command),
    });
  }

  joinGame(gameId: string, isAnonymous: boolean): void {
    const command: JoinGameCommand = {
      gameId: gameId,
      playerId: this.playerService.getPlayerId(),
    };

    const destination = isAnonymous ? '/app/match/joinAnonymous' : '/app/match/join';
    
    this.stomp.publish({
      destination: destination,
      body: JSON.stringify(command),
    });
  }

  makeMove(command: MakeMoveCommand): void {
    this.stomp.publish({
      destination: '/app/match/move',
      body: JSON.stringify(command),
    });
  }

  disconnect(): void {
    this.stomp.deactivate();
  }

  private subscribeToGameEvents(): void {
    this.stomp.watch('/topic/match.join').subscribe((message) => {
      const game = JSON.parse(message.body) as GameDto;
      this.gameRemoved.next(game);
      this.currentMatch = game;
      this.router.navigate(['/game']);
    });

    this.stomp.watch('/topic/match.moveReceived').subscribe((message) => {
      const move = JSON.parse(message.body) as MoveDto;
      this.receivedMove.next(move);
    });

    this.stomp.watch('/topic/game.created').subscribe((message) => {
      const game = JSON.parse(message.body) as GameDto;
      this.gameAdded.next(game);
    });

    this.stomp.watch('/topic/game.cancelled').subscribe((message) => {
      const game = JSON.parse(message.body) as GameDto;
      this.gameRemoved.next(game);
    });
  }
}
