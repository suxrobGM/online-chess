import {Component, OnDestroy, OnInit} from '@angular/core';
import {TableLazyLoadEvent, TableModule} from 'primeng/table';
import {ButtonModule} from 'primeng/button';
import {Subscription} from 'rxjs';
import {GameDto, GameStatus, GetGamesQuery, PlayerColor} from '@chessmate-app/core/models';
import {ApiService, MatchService} from '@chessmate-app/core/services';
import {SortUtils} from '@chessmate-app/shared/utils';


@Component({
  selector: 'app-lobby',
  standalone: true,
  templateUrl: './lobby.component.html',
  styleUrl: './lobby.component.scss',
  imports: [
    TableModule,
    ButtonModule,
  ],
})
export class LobbyComponent implements OnInit, OnDestroy {
  private gameAddedSubscription?: Subscription;
  private gameRemovedSubscription?: Subscription;
  public isLoading = true;
  public first = 0;
  public openGames: GameDto[] = [];

  constructor(
    private readonly apiService: ApiService,
    private readonly matchService: MatchService)
  {
  }

  ngOnInit(): void {
    this.gameAddedSubscription = this.matchService.gameAdded$.subscribe((game) => {
      this.openGames = [game, ...this.openGames];
    });

    this.gameRemovedSubscription = this.matchService.gameRemoved$.subscribe((game) => {
      this.openGames = this.openGames.filter((g) => g.id !== game.id);
    });
  }

  ngOnDestroy(): void {
    this.gameAddedSubscription?.unsubscribe();
    this.gameRemovedSubscription?.unsubscribe();
  }

  loadGames(event: TableLazyLoadEvent) {
    this.isLoading = true;
    const first = event.first ?? 1;
    const rows = event.rows ?? 10;
    const page = first / rows + 1;
    const sortField = SortUtils.parseSortProperty(event.sortField as string, event.sortOrder);
    const query: GetGamesQuery = {
      orderBy: sortField, 
      page: page, 
      pageSize: rows,
      gameStatus: GameStatus.OPEN,
    };

    this.apiService.getGames(query).subscribe((result) => {
      if (result) {
        this.openGames = result;
      }

      this.isLoading = false;
    });
  }

  joinGame(game: GameDto): void {
    const isAnonymousGame = game.hostPlayerUsername === 'Anonymous';
    this.matchService.joinGame(game.id, isAnonymousGame);
  }

  getHostColor(game: GameDto): string {
    if (game.hostPlayerColor === PlayerColor.WHITE) {
      return 'White';
    }
    else if (game.hostPlayerColor === PlayerColor.BLACK) {
      return 'Black';
    }

    return 'Random';
  }
}
