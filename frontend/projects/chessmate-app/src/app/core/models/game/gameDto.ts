import {PlayerColor} from '../playerColor';
import {GameStatus} from './gameStatus';

export interface GameDto {
  id: string;
  hostPlayerId?: string;
  hostPlayerUsername?: string;
  hostPlayerColor?: PlayerColor;
  hostPlayerElo?: number;
  whitePlayerId?: string;
  whitePlayerUsername?: string;
  whitePlayerElo?: number;
  blackPlayerId?: string;
  blackPlayerUsername?: string;
  blackPlayerElo?: number;
  winnerPlayer?: PlayerColor;
  status: GameStatus;
  currentTurnPlayer?: PlayerColor;
  isRanked: boolean;
  isTimerEnabled: boolean;
  pgn: string;
  createdDate: string;
}
