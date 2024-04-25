/* eslint-disable @typescript-eslint/no-explicit-any */
import {
  AfterViewInit,
  Component,
  EventEmitter,
  HostListener,
  Input,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import {
  NgxChessBoardComponent,
  NgxChessBoardModule,
  PieceIconInput,
} from 'ngx-chess-board';

@Component({
  selector: 'app-chessboard',
  standalone: true,
  templateUrl: './chessboard.component.html',
  imports: [NgxChessBoardModule],
})
export class ChessboardComponent implements OnInit, AfterViewInit {
  public size = 500;
  public whiteDisabled = false;
  public blackDisabled = false;
  
  @ViewChild('board') 
  public boardRef!: NgxChessBoardComponent;

  @Input()
  public blackSquareColor = '#b96331';

  @Input()
  public whiteSquareColor = '#f0c697';

  @Input()
  public pieceIcons: PieceIconInput;

  @Input()
  public orientation: 'white' | 'black' = 'white';

  @Output()
  public moveChange = new EventEmitter<any>();

  constructor() {
    this.pieceIcons = {
      whiteKingUrl: 'assets/pieces/white-king.png',
      whiteQueenUrl: 'assets/pieces/white-queen.png',
      whiteKnightUrl: 'assets/pieces/white-knight.png',
      whiteRookUrl: 'assets/pieces/white-rook.png',
      whitePawnUrl: 'assets/pieces/white-pawn.png',
      whiteBishopUrl: 'assets/pieces/white-bishop.png',
      blackKingUrl: 'assets/pieces/black-king.png',
      blackQueenUrl: 'assets/pieces/black-queen.png',
      blackKnightUrl: 'assets/pieces/black-knight.png',
      blackRookUrl: 'assets/pieces/black-rook.png',
      blackPawnUrl: 'assets/pieces/black-pawn.png',
      blackBishopUrl: 'assets/pieces/black-bishop.png',
    };
  }

  ngOnInit() {
    this.resizeBoard();
  }

  ngAfterViewInit(): void {
    // Disable the opposite color and reverse the board if the orientation is black.
    if (this.orientation === 'black') {
      this.whiteDisabled = true;
      setTimeout(() => this.reverseBoard(), 1);
    }
    else {
      this.blackDisabled = true;
    }
  }

  getPgn(): string {
    return this.boardRef.getPGN();
  }

  reverseBoard() {
    this.boardRef.reverse();
  }

  move(from: string, to: string) {
    this.boardRef.move(from + to);
  }

  emitMoveChangeEvent(event: any) {
    this.moveChange.emit(event);
  }

  @HostListener('window:resize', ['$event'])
  onResize() {
    this.resizeBoard();
  }

  private resizeBoard() {
    const boardSize = Math.min(window.innerWidth, window.innerHeight);
    this.size = Math.max(boardSize - 200, 200);
  }
}
