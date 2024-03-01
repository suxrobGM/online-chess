import {Component, type OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  standalone: true,
  imports: [CommonModule],
})
export class HomeComponent implements OnInit {
  ngOnInit(): void {
    console.log('HomeComponent initialized');
  }
}
