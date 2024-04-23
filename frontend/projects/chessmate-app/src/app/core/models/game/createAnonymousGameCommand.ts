import {PlayerColor} from '../playerColor';

export interface CreateAnonymousGameCommand {
  hostPlayerId: string;
  hostPlayerColor?: PlayerColor;
}
