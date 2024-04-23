import {Component} from '@angular/core';
import {TableLazyLoadEvent, TableModule} from 'primeng/table';
import {GameDto, GetGamesQuery} from '@chessmate-app/core/models';
import {ApiService} from '@chessmate-app/core/services';
import {SortUtils} from '@chessmate-app/shared/utils';


@Component({
  selector: 'app-lobby',
  standalone: true,
  templateUrl: './lobby.component.html',
  styleUrl: './lobby.component.scss',
  imports: [
    TableModule,
  ],
})
export class LobbyComponent {
  public isLoading = true;
  public totalRecords = 0;
  public first = 0;
  public openGames: GameDto[] = [];

  constructor(private readonly apiService: ApiService) {
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

  joinGame(gameId: string): void {
  }
}
