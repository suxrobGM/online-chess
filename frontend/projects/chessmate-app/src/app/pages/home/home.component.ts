import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ButtonModule} from 'primeng/button';
import {ChessboardComponent} from '@chessmate-app/shared/components';
import {LobbyComponent} from './components';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  standalone: true,
  imports: [
    CommonModule,
    ChessboardComponent,
    ButtonModule,
    LobbyComponent,
  ],
})
export class HomeComponent {
}
