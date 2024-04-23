import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {DialogModule} from 'primeng/dialog';
import {DropdownModule} from 'primeng/dropdown';
import {ButtonModule} from 'primeng/button';
import {TooltipModule} from 'primeng/tooltip';
import {ApiService, AuthService, PlayerService} from '@chessmate-app/core/services';
import {
  CreateAnonymousGameCommand,
  CreateGameCommand,
  PlayerColor,
} from '@chessmate-app/core/models';


@Component({
  selector: 'app-create-game-dialog',
  standalone: true,
  templateUrl: './create-game-dialog.component.html',
  styleUrl: './create-game-dialog.component.scss',
  imports: [
    DialogModule,
    DropdownModule,
    ReactiveFormsModule,
    ButtonModule,
    TooltipModule,
  ],
})
export class CreateGameDialogComponent {
  public isLoading = false;
  public form: FormGroup<CreateGameForm>;
  public timeControlOptions = ['Unlimited', 'Bullet', 'Blitz', 'Rapid', 'Classical'];
  public gameTypeOptions = ['Casual', 'Rated'];

  @Input()
  public visible = false;

  @Output()
  public visibleChange = new EventEmitter<boolean>();

  constructor(
    private readonly apiService: ApiService,
    private readonly authService: AuthService,
    private readonly playerService: PlayerService,
  )
  { 
    this.form = new FormGroup<CreateGameForm>({
      timeControl: new FormControl('Unlimited', {nonNullable: true}),
      gameType: new FormControl('Casual', {nonNullable: true}),
      ratingRange: new FormControl('', {nonNullable: true}),
      hostColor: new FormControl(null, {nonNullable: false}),
    });
  }

  show(): void {
    this.visible = true;
    this.visibleChange.emit(true);
  }

  hide(): void {
    this.visible = false;
    this.visibleChange.emit(false);
  }

  createGame(): void {
    if (this.form.invalid) {
      return;
    }

    this.isLoading = true;
    const isAnonymous = !this.authService.isAuthenticated();

    if (isAnonymous) {
      this.createAnonymousGame();
    }
    else {
      this.createAuthenticatedGame();
    }
  }

  setWhiteHostColor(): void {
    this.setHostColor(PlayerColor.WHITE);
  }

  setBlackHostColor(): void {
    this.setHostColor(PlayerColor.BLACK);
  }

  setRandomHostColor(): void {
    this.setHostColor(null);
  }

  private setHostColor(color: number | null): void {
    this.form.controls.hostColor.setValue(color);
  }

  private createAnonymousGame() {
    const command: CreateAnonymousGameCommand = {
      hostPlayerId: this.playerService.getPlayerId(),
      hostPlayerColor: this.form.controls.hostColor.value,
    };

    this.apiService.createAnonymousGame(command).subscribe((game) => {
      this.isLoading = false;
      this.hide();
      console.log(game);
    });
  }

  private createAuthenticatedGame() {
    const command: CreateGameCommand = {
      hostPlayerId: this.playerService.getPlayerId(),
      hostPlayerColor: this.form.controls.hostColor.value,
    };

    this.apiService.createGame(command).subscribe((game) => {
      this.isLoading = false;
      this.hide();
      console.log(game);
    });
  }
}

interface CreateGameForm {
  timeControl: FormControl<string>;
  gameType: FormControl<string>;
  ratingRange: FormControl<string>;
  hostColor: FormControl<PlayerColor | null>;
}
