import {Component, OnInit} from '@angular/core';
import {TableLazyLoadEvent, TableModule} from 'primeng/table';
import {ButtonModule} from 'primeng/button';
import {GameDto, GetGamesQuery, PlayerColor} from '@chessmate-app/core/models';
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
export class LobbyComponent implements OnInit {
  public isLoading = true;
  public totalRecords = 0;
  public first = 0;
  public openGames: GameDto[] = [];

  constructor(
    private readonly apiService: ApiService,
    private readonly matchService: MatchService)
  {
  }

  ngOnInit(): void {
    this.matchService.connect();
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
