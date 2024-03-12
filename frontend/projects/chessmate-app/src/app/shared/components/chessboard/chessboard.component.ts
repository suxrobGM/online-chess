import {Component, Input, ViewChild} from '@angular/core';
import {
  NgxChessBoardModule,
  NgxChessBoardView,
  PieceIconInput,
} from 'ngx-chess-board';

@Component({
  selector: 'app-chessboard',
  standalone: true,
  templateUrl: './chessboard.component.html',
  styleUrl: './chessboard.component.scss',
  imports: [NgxChessBoardModule],
})
export class ChessboardComponent {
  @ViewChild('board') 
  public boardRef!: NgxChessBoardView;

  @Input()
  public blackSquareColor = '#b96331';

  @Input()
  public whiteSquareColor = '#f0c697';

  @Input()
  public pieceIcons: PieceIconInput;

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
}
