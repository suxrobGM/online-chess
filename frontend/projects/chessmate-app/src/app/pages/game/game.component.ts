import {Component} from '@angular/core';
import {ButtonModule} from 'primeng/button';
import {ChessboardComponent} from '@chessmate-app/shared/components';

@Component({
  selector: 'app-game',
  standalone: true,
  templateUrl: './game.component.html',
  styleUrl: './game.component.scss',
  imports: [
    ChessboardComponent,
    ButtonModule,
  ]
})
export class GameComponent {

}
