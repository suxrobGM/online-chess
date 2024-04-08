import {GameStatus} from './gameStatus';

export interface GameDto {
  id: string;
  whitePlayerId?: string;
  whitePlayerUsername?: string;
  whitePlayerElo?: number;
  blackPlayerId?: string;
  blackPlayerUsername?: string;
  blackPlayerElo?: number;
  winnerPlayerId?: string;
  status: GameStatus;
  currentTurnPlayerId?: string;
  isRanked: boolean;
  isTimerEnabled: boolean;
  pgn: string;
  createdDate: string;
}
