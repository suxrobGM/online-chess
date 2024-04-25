import {PlayerColor} from './playerColor';

export interface MoveDto {
  gameId: string;
  whitePlayerId: string;
  blackPlayerId: string;
  color: PlayerColor;
  from: string;
  to: string;
  isCheckmate: boolean;
  isStalemate: boolean
  san: string;
  pgn: string;
}
