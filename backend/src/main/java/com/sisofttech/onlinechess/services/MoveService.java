package com.sisofttech.onlinechess.services;

import com.sisofttech.onlinechess.model.Move;
import com.sisofttech.onlinechess.repository.GameRepository;
import com.sisofttech.onlinechess.repository.MoveRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class MoveService {
    private final MoveRepository moveRepository;
    private final GameRepository gameRepository;

    public MoveService(MoveRepository moveRepository, GameRepository gameRepository) {
        this.moveRepository = moveRepository;
        this.gameRepository = gameRepository;
    }

    /**
     * Log a move in the game
     *
     * @param gameId         The game ID
     * @param fromPosition   The position from which the piece is moved
     * @param toPosition     The position to which the piece is moved
     * @param piece          The piece moved
     * @param capturedPiece  The piece captured, if any
     * @param isCheck        Whether the move puts the opponent's king in check
     * @param isCheckmate    Whether the move puts the opponent's king in checkmate
     * @return The move logged
     */
    public Move logMove(
            UUID gameId,
            String fromPosition,
            String toPosition,
            String piece,
            String capturedPiece,
            boolean isCheck,
            boolean isCheckmate)
    {
        var game = gameRepository.findById(gameId).orElseThrow();
        var move = new Move();
        move.setGame(game);
        move.setFromPosition(fromPosition);
        move.setToPosition(toPosition);
        move.setPiece(piece);
        move.setCapturedPiece(capturedPiece);
        move.setCheck(isCheck);
        move.setCheckmate(isCheckmate);
        move.setCapturedPiece(capturedPiece);

        // Calculate and set moveNumber based on the last move of this game
        var moveNumber = moveRepository.findTopByGameOrderByMoveNumberDesc(game)
                .map(Move::getMoveNumber)
                .orElse(0);
        move.setMoveNumber(moveNumber + 1);
        return moveRepository.save(move);
    }

    // Validate move
    public boolean validateMove(String fromPosition, String toPosition, String piece) {
        // Implement chess rules for piece movement
        return true;
    }
}
