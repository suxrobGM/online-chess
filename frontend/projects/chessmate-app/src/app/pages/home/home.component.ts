import {Component, type OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ButtonModule} from 'primeng/button';
import {RxStomp} from '@stomp/rx-stomp';
import {v4 as uuid} from 'uuid';
import {CreateAnonymousGameCommand, CreateGameCommand, PlayerColor} from "@chessmate-app/core/models";
import {ChessboardComponent} from '@chessmate-app/shared/components';
import { ApiService } from '@chessmate-app/core/services';


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  standalone: true,
  imports: [
    CommonModule,
    ChessboardComponent,
    ButtonModule,
  ],
})
export class HomeComponent implements OnInit {
  private readonly stomp: RxStomp;

  constructor(private readonly apiService: ApiService) {
    this.stomp = new RxStomp();
    this.stomp.configure({
      brokerURL: 'ws://localhost:8000/ws',
      debug: (str) => console.log(str),
    });
  }

  ngOnInit(): void {
    this.stomp.activate();
  }

  createGame(): void {
    const command: CreateAnonymousGameCommand = {
      hostPlayerColor: PlayerColor.WHITE,
    }

    // this.stomp.publish({
    //   destination: '/app/matchmaking/createAnonymousGame',
    //   body: JSON.stringify(command),
    // });

    this.apiService.createAnonymousGame(command).subscribe((result) => {
      console.log(result);
    });
  }
}
