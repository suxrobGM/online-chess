import {Component, ElementRef, ViewChild} from '@angular/core';
import {NgClass} from '@angular/common';
import {MenuItem} from 'primeng/api';
import {MenubarModule} from 'primeng/menubar';
import {InputTextModule} from 'primeng/inputtext';
import {ButtonModule} from 'primeng/button';


@Component({
  selector: 'app-topbar',
  standalone: true,
  templateUrl: './topbar.component.html',
  styleUrl: './topbar.component.scss',
  imports: [
    MenubarModule,
    InputTextModule,
    ButtonModule,
    NgClass
  ],
})
export class TopbarComponent {
  public menuItems: MenuItem[];
  public isSearchInputVisible = false;

  @ViewChild('searchInput')
  public searchInputRef!: ElementRef<HTMLInputElement>;

  constructor() {
    this.menuItems = [
      {
        label: 'PLAY',
        items: [
          { label: 'Create a game', icon: 'bi bi-plus-square' },
        ]
      },
      {
        label: 'PUZZLES',
      },
    ];
  }

  public toggleSearchInputVisibility() {
    this.isSearchInputVisible = !this.isSearchInputVisible;

    if (this.isSearchInputVisible) {
      this.searchInputRef.nativeElement.focus();
    }
  }
}
