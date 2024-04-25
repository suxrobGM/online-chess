import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Router} from '@angular/router';
import {DialogModule} from 'primeng/dialog';
import {ButtonModule} from 'primeng/button';


@Component({
  selector: 'app-game-result-dialog',
  standalone: true,
  templateUrl: './game-result-dialog.component.html',
  imports: [
    DialogModule,
    ButtonModule,
  ],
})
export class GameResultDialogComponent {
  @Input() 
  public result?: 'win' | 'lose' | 'draw';

  @Input()
  public visible = false;

  @Output()
  public visibleChange = new EventEmitter<boolean>();

  constructor(private readonly router: Router) { 
  }

  show(): void {
    this.visible = true;
    this.visibleChange.emit(true);
  }

  hide(): void {
    this.visible = false;
    this.visibleChange.emit(false);
    this.router.navigate(['/home']);
  }
}
