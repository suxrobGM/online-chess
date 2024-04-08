import {Component, ElementRef, ViewChild} from '@angular/core';
import {NgClass} from '@angular/common';
import {MenuItem} from 'primeng/api';
import {MenubarModule} from 'primeng/menubar';
import {InputTextModule} from 'primeng/inputtext';
import {ButtonModule} from 'primeng/button';
import {ApiService, AuthService} from '@chessmate-app/core/services';


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
  public readonly menuItems: MenuItem[];
  public isSearchInputVisible = false;

  @ViewChild('searchInput')
  public searchInputRef!: ElementRef<HTMLInputElement>;

  constructor(
    private readonly authService: AuthService,
    private readonly apiService: ApiService,
  ) 
  {
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
    this.isSearchInputVisible = !this.isSearchInputVisible;

    if (this.isSearchInputVisible) {
      this.searchInputRef.nativeElement.focus();
    }
  }

  private openCreateGameDialog() {
    this.createAnonymousGame();
  }

  private createAnonymousGame() {
    this.apiService.createAnonymousGame({}).subscribe((game) => {
      console.log(game);
    });
  }
}
