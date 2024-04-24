import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {Subscription} from 'rxjs';
import {DialogModule} from 'primeng/dialog';
import {DropdownModule} from 'primeng/dropdown';
import {ButtonModule} from 'primeng/button';
import {TooltipModule} from 'primeng/tooltip';
import {ProgressSpinnerModule} from 'primeng/progressspinner';
import {AuthService, MatchService, PlayerService} from '@chessmate-app/core/services';
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
    ProgressSpinnerModule,
  ],
})
export class CreateGameDialogComponent implements OnInit, OnDestroy {
  private readonly playerId: string;
  private gameAddedSubscription?: Subscription;
  private createdGameId: string | null = null;
  public isLoading = false;
  public form: FormGroup<CreateGameForm>;
  public timeControlOptions = ['Unlimited', 'Bullet', 'Blitz', 'Rapid', 'Classical'];
  public gameTypeOptions = ['Casual', 'Rated'];

  @Input()
  public visible = false;

  @Output()
  public visibleChange = new EventEmitter<boolean>();

  constructor(
    private readonly authService: AuthService,
    private readonly playerService: PlayerService,
    private readonly matchService: MatchService,
  )
  { 
    this.form = new FormGroup<CreateGameForm>({
      timeControl: new FormControl('Unlimited', {nonNullable: true}),
      gameType: new FormControl('Casual', {nonNullable: true}),
      ratingRange: new FormControl('', {nonNullable: true}),
      hostColor: new FormControl(null, {nonNullable: false}), // null means random color
    });

    this.playerId = this.playerService.getPlayerId();
  }

  ngOnInit(): void {
    this.gameAddedSubscription = this.matchService.gameAdded$.subscribe((game) => {
      if (game.hostPlayerId === this.playerId) {
        this.createdGameId = game.id;
      }
    });
  }
  
  ngOnDestroy(): void {
    console.log('Destroying create game dialog');
    this.gameAddedSubscription?.unsubscribe();
    
    if (this.createdGameId) {
      this.matchService.cancelGame(this.createdGameId);
    }
  }

  show(): void {
    this.visible = true;
    this.isLoading = false;
    this.visibleChange.emit(true);
  }

  hide(): void {
    if (this.createdGameId) {
      this.matchService.cancelGame(this.createdGameId);
    }

    this.visible = false;
    this.visibleChange.emit(false);
    this.resetForm();
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
      hostPlayerId: this.playerId,
      hostPlayerColor: this.form.controls.hostColor.value,
    };

    this.matchService.createAnonymousGame(command);
  }

  private createAuthenticatedGame() {
    const command: CreateGameCommand = {
      hostPlayerId: this.playerId,
      hostPlayerColor: this.form.controls.hostColor.value,
    };

    this.matchService.createGame(command);
  }

  private resetForm(): void {
    this.isLoading = false;
    this.createdGameId = null;
    this.form.reset();
  }
}

interface CreateGameForm {
  timeControl: FormControl<string>;
  gameType: FormControl<string>;
  ratingRange: FormControl<string>;
  hostColor: FormControl<PlayerColor | null>;
}
