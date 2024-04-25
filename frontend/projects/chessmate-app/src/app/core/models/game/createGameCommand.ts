import {PlayerColor} from './playerColor';

export interface CreateGameCommand {
  hostPlayerId: string;
  hostPlayerColor?: PlayerColor | null;
}
