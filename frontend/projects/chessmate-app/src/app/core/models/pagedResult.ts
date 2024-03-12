import {Result} from './result';

export type PagedResult<T> = Result<T[]> & {
  totalItems: number;
  totalPages: number;
}
