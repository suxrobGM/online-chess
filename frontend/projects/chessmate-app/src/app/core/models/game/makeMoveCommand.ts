import {PlayerColor} from './playerColor';

export interface MakeMoveCommand {
  gameId: string;
  color: PlayerColor;
  from: string;
  to: string;
  isCheckmate: boolean;
  isStalemate: boolean;
}
