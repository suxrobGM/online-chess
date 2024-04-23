import {Injectable} from '@angular/core';
import {StorageService} from './storage.service';
import {v4 as uuidv4} from 'uuid';

@Injectable({providedIn: 'root'})
export class PlayerService {
  private readonly storageKey = 'PLAYER_ID';
  private playerId: string;

  constructor(private readonly strorageService: StorageService) {
    this.playerId = this.strorageService.get(this.storageKey) ?? uuidv4();
    this.strorageService.set(this.storageKey, this.playerId);
  }

  setPlayerId(playerId: string): void {
    this.playerId = playerId;
    this.strorageService.set(this.storageKey, playerId);
  }

  getPlayerId(): string {
    return this.playerId;
  }
}
