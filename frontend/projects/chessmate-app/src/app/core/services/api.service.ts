import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable, catchError, of} from 'rxjs';
import {APP_CONFIG} from '@chessmate-app/configs';
import {ToastService} from '@chessmate-app/core/services';
import {
  SearchableQuery,
  //Result,
  CreateAnonymousGameCommand,
  CreateGameCommand,
  GameDto,
  //PagedResult,
  GetGamesQuery,
} from '@chessmate-app/core/models';

@Injectable({providedIn: 'root'})
export class ApiService {
  private readonly baseUrl = APP_CONFIG.apiUrl;
  private readonly headers: Record<string, string> = {'content-type': 'application/json'};

  constructor(
    private readonly httpClient: HttpClient,
    private readonly toastService: ToastService,
  )
  {
  }

  //#region Users API

  // getUsers(query?: SearchableQuery): Observable<PagedResult<UserDto>> {
  //   const url = `/users?${this.stringfySearchableQuery(query)}`;
  //   return this.get(url);
  // }

  //#endregion


  //#region Game API

  getGame(id: string): Observable<GameDto> {
    const url = `/games/${id}`;
    return this.get(url);
  }

  getGames(query?: GetGamesQuery): Observable<GameDto[]> {
    let url = `/games`;

    if (query?.gameStatus) {
      url += `?gameStatus=${query.gameStatus}`;
    }

    return this.get(url);
  }

  createGame(command: CreateGameCommand): Observable<GameDto> {
    const url = `/games`;
    return this.post(url, command);
  }

  createAnonymousGame(command: CreateAnonymousGameCommand): Observable<GameDto> {
    const url = `/games/anonymous`;
    return this.post(url, command);
  }

  // joinGame(gameId: string): Observable<GameDto> {
  //   const url = `/games/${gameId}/join`;
  //   return this.post(url, {});
  // }

  // updateClient(command: UpdateClientCommand): Observable<Result> {
  //   const url = `/clients/${command.id}`;
  //   return this.put(url, command);
  // }

  // deleteClient(id: string): Observable<Result> {
  //   const url = `/clients/${id}`;
  //   return this.delete(url);
  // }

  //#endregion


  //#region Internal methods

  private get<TResponse>(endpoint: string): Observable<TResponse> {
    return this.httpClient
      .get<TResponse>(this.baseUrl + endpoint)
      .pipe(catchError((err) => this.handleError(err)));
  }

  private post<TResponse, TBody>(endpoint: string, body: TBody): Observable<TResponse> {
    const bodyJson = JSON.stringify(body);

    return this.httpClient
      .post<TResponse>(this.baseUrl + endpoint, bodyJson, {headers: this.headers})
      .pipe(catchError((err) => this.handleError(err)));
  }

  private put<TResponse, TBody>(endpoint: string, body: TBody): Observable<TResponse> {
    const bodyJson = JSON.stringify(body);

    return this.httpClient
      .put<TResponse>(this.baseUrl + endpoint, bodyJson, {headers: this.headers})
      .pipe(catchError((err) => this.handleError(err)));
  }

  private delete<TResponse>(endpoint: string): Observable<TResponse> {
    return this.httpClient
      .delete<TResponse>(this.baseUrl + endpoint)
      .pipe(catchError((err) => this.handleError(err)));
  }

  private stringfySearchableQuery(query?: SearchableQuery): string {
    const search = query?.search ?? '';
    const orderBy = query?.orderBy ?? '';
    const page = query?.page ?? 1;
    const pageSize = query?.pageSize ?? 10;
    return `search=${search}&orderBy=${orderBy}&page=${page}&pageSize=${pageSize}`;
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private handleError(responseData: any): Observable<any> {
    const errorMessage = responseData.error?.error ?? responseData.error;

    this.toastService.showError(errorMessage);
    console.error(errorMessage ?? responseData);
    return of({error: errorMessage, success: false});
  }

  //#endregion
}
