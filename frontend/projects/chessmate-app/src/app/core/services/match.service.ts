import {Injectable} from '@angular/core';
import {RxStomp} from '@stomp/rx-stomp';
import {APP_CONFIG} from '@chessmate-app/configs';

@Injectable({providedIn: 'root'})
export class MatchService {
  private readonly stomp: RxStomp;
  private readonly baseUrl = APP_CONFIG.wsUrl;

  constructor() {
    this.stomp = new RxStomp();
    this.stomp.configure({
      brokerURL: this.baseUrl,
      debug: (str) => console.log(str),
    });

    // this.stomp.publish({
    //   destination: '/app/matchmaking/createAnonymousGame',
    //   body: JSON.stringify(command),
    // });
  }

  /**
   * Initiates a connection to the WebSocket server.
   */
  connect(): void {
    this.stomp.activate();
  }
}
