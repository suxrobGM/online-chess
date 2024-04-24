import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {RxStomp} from '@stomp/rx-stomp';
import {APP_CONFIG} from '@chessmate-app/configs';
import {Subject} from 'rxjs';
import {
  ConnectPlayerCommand,
  GameDto,
  JoinGameCommand,
} from '@chessmate-app/core/models';
import {PlayerService} from './player.service';


@Injectable({providedIn: 'root'})
export class MatchService {
  private readonly stomp: RxStomp;
  private readonly baseUrl = APP_CONFIG.wsUrl;
  private readonly newGameCreated = new Subject<GameDto>();
  public readonly newGameCreated$ = this.newGameCreated.asObservable();

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

    this.stomp.watch('/topic/match.join').subscribe((message) => {
      console.log(message);
      
    });

    this.stomp.watch('/topic/game.created').subscribe((message) => {
      const game = JSON.parse(message.body) as GameDto;
      this.newGameCreated.next(game);
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

  disconnect(): void {
    this.stomp.deactivate();
  }
}
