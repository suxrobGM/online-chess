import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {RxStomp} from '@stomp/rx-stomp';
import {APP_CONFIG} from '@chessmate-app/configs';
import {JoinGameCommand} from '@chessmate-app/core/models';
import {PlayerService} from './player.service';


@Injectable({providedIn: 'root'})
export class MatchService {
  private readonly stomp: RxStomp;
  private readonly baseUrl = APP_CONFIG.wsUrl;

  constructor(
    private readonly playerService: PlayerService,
    private readonly router: Router)
  {
    this.stomp = new RxStomp();
    this.stomp.configure({
      brokerURL: this.baseUrl,
      debug: (str) => console.log(str),
    });
  }

  /**
   * Initiates a connection to the WebSocket server.
   */
  connect(): void {
    this.stomp.activate();

    this.stomp.watch('/topic/match.join').subscribe((message) => {
      console.log(message);
      
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
