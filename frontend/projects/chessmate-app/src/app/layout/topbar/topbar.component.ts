import {Component, ElementRef, ViewChild} from '@angular/core';
import {NgClass} from '@angular/common';
import {MenuItem} from 'primeng/api';
import {MenubarModule} from 'primeng/menubar';
import {InputTextModule} from 'primeng/inputtext';
import {ButtonModule} from 'primeng/button';
import {CreateGameDialogComponent} from '@chessmate-app/shared/components';


@Component({
  selector: 'app-topbar',
  standalone: true,
  templateUrl: './topbar.component.html',
  styleUrl: './topbar.component.scss',
  imports: [
    MenubarModule,
    InputTextModule,
    ButtonModule,
    NgClass,
    CreateGameDialogComponent,
  ],
})
export class TopbarComponent {
  public readonly menuItems: MenuItem[];
  public searchInputVisible = false;
  public createGameDialogVisible = false;

  @ViewChild('searchInput')
  public searchInputRef!: ElementRef<HTMLInputElement>;

  constructor() {
    this.menuItems = [
      {
        label: 'PLAY',
        items: [
          { 
            label: 'Create a game',
            icon: 'bi bi-plus-square',
            command: () => this.openCreateGameDialog(),
          },
        ]
      },
      {
        label: 'PUZZLES',
      },
    ];
  }

  toggleSearchInputVisibility() {
    this.searchInputVisible = !this.searchInputVisible;

    if (this.searchInputVisible) {
      this.searchInputRef.nativeElement.focus();
    }
  }

  private openCreateGameDialog() {
    this.createGameDialogVisible = true;
  }
}
