import {PagedQuery} from '../pagedQuery';
import {GameStatus} from './gameStatus';

export interface GetGamesQuery extends PagedQuery {
  gameStatus?: GameStatus;
}
