import {Component, type OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {NgxChessBoardModule} from 'ngx-chess-board';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  standalone: true,
  imports: [
    CommonModule,
    NgxChessBoardModule
  ],
})
export class HomeComponent implements OnInit {
  ngOnInit(): void {
    console.log('HomeComponent initialized');
  }
}
