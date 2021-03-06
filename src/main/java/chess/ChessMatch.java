package chess;

import static chess.Color.BLACK;
import static chess.Color.WHITE;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {

	private Board board;
	
	private int turn;
	
	private Color currentPlayer;
	
	private List<Piece> piecesOnBoard = new ArrayList<>();
	
	private List<Piece> capturedPieces = new ArrayList<>();
	
	private boolean check;
	
	private boolean checkmate;
	
	private ChessPiece enPassantVulnerable;
	
	private ChessPiece promoted;
	
	public ChessMatch() {
		this.board = new Board(8, 8);
		this.turn = 1;
		this.currentPlayer = WHITE;
		this.initialSetup();
	}
	
	public boolean isCheckmate() {
		return checkmate;
	}
	
	public int getTurn() {
		return turn;
	}

	public Color getCurrentPlayer() {
		return currentPlayer;
	}
	
	public boolean isCheck() {
		return this.check;
	}
	
	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
	}
	
	

	public ChessPiece getPromoted() {
		return promoted;
	}

	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		for (int i=0; i<this.board.getRows(); i++) {
			for (int j=0; j<this.board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) this.board.getPiece(i, j);
			}
		}
		return mat;
	}
	
	private Color getOpponentColor(Color color) {
		return WHITE.equals(color) ? BLACK : WHITE;
	}
	
	private ChessPiece getKing(Color color) {
		List<Piece> list = this.piecesOnBoard.stream().filter(x ->((ChessPiece) x).getColor() == color).collect(Collectors.toList());
		for (Piece p : list) {
			if (p instanceof King) {
				return (ChessPiece) p;
			}
		}
		throw new IllegalStateException("Não existe Rei com a cor " + color + " no tabuleiro");
	}
	
	private boolean testCheck(Color color) {
		Position kingPosition = this.getKing(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = this.piecesOnBoard.stream().filter(x ->((ChessPiece) x).getColor() == getOpponentColor(color)).collect(Collectors.toList());
		for (Piece p : opponentPieces) {
			boolean[][] mat = p.possibleMoves();
			if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false;
	}
	
	private boolean testCheckmate(Color color) {
		if (!this.testCheck(color)) {
			return false;
		}
		List<Piece> pieces = this.piecesOnBoard.stream().filter(x ->((ChessPiece) x).getColor() == color).collect(Collectors.toList());
		for (Piece p : pieces) {
			boolean[][] mat = p.possibleMoves();
			for (int i=0; i<board.getRows(); i++) {
				for (int j=0; j<board.getColumns(); j++) {
					if (mat[i][j]) {
						Position source = ((ChessPiece)p).getChessPosition().toPosition();
						Position target = new Position(i, j);
						Piece capturedPiece = this.makeMove(source, target);
						boolean check = this.testCheck(color);
						undoMove(source, target, capturedPiece);
						if (!check) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		this.piecesOnBoard.add(piece);
	}
	
	private void initialSetup() {
		placeNewPiece('a', 1, new Rook(board, Color.WHITE));
		placeNewPiece('b', 1, new Knight(board, Color.WHITE));
		placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE, this));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));

        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('d', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK, this));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));
	}
	
	public ChessPiece performChessMove(ChessPosition sourcePos, ChessPosition targetPos) {
		Position source = sourcePos.toPosition();
		Position target = targetPos.toPosition();
		validateSourcePosition(source);
		validateTargetPosition(source, target);
		Piece capturedPiece = makeMove(source, target);
		
		if (this.testCheck(currentPlayer)) {
			this.undoMove(source, target, capturedPiece);
			throw new ChessException("Você não pode se colocar em xeque.");
		}
		
		ChessPiece movedPiece = (ChessPiece) this.board.getPiece(target);
		
		// Promoção de Peão
		promoted = null;
		if (movedPiece instanceof Pawn) {
			if ((Color.WHITE.equals(movedPiece.getColor()) && target.getRow() == 0)
				|| (Color.BLACK.equals(movedPiece.getColor()) && target.getRow() == 7)) {
				promoted = (ChessPiece) this.board.getPiece(target);
				promoted = replacePromotedPiece("Q");
			}
		}
		
		this.check = this.testCheck(this.getOpponentColor(currentPlayer));
		
		if (this.testCheckmate(this.getOpponentColor(currentPlayer))) {
			this.checkmate = true;
		} else {
			this.nextTurn();
		}
		
		// Movimento especial - En Passant
		if (movedPiece instanceof Pawn && (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)) {
			enPassantVulnerable = movedPiece;
		} else {
			enPassantVulnerable = null;
		}
		
		return (ChessPiece) capturedPiece;
	}
	
	public ChessPiece replacePromotedPiece(String type) {
		if (promoted == null) {
			throw new IllegalArgumentException("Não há peça a ser promovida.");
		}
		if (!"B".equalsIgnoreCase(type) && !"N".equalsIgnoreCase(type) && !"R".equalsIgnoreCase(type) && !"Q".equalsIgnoreCase(type)) {
			return promoted;
		}
		Position pos = promoted.getChessPosition().toPosition();
		Piece p = this.board.removePiece(pos);
		this.piecesOnBoard.remove(p);
		
		ChessPiece newPiece = this.newPiece(type, promoted.getColor());
		this.board.placePiece(newPiece, pos);
		this.piecesOnBoard.add(newPiece);
		
		return newPiece;
	}
	
	private ChessPiece newPiece(String type, Color color) {
		switch (type.toUpperCase()) {
		case "B":
			return new Bishop(this.board, promoted.getColor());
		
		case "N":
			return new Knight(this.board, promoted.getColor());
		
		case "R":
			return new Rook(this.board, promoted.getColor());
		
		default:
			return new Queen(this.board, promoted.getColor());
		}
	}

	private void validateSourcePosition(Position position) {
		if (!board.thereIsAPiece(position)) {
			throw new ChessException("Não existe peça na posição de origem.");
		}
		if (this.currentPlayer != ((ChessPiece) this.board.getPiece(position)).getColor()) {
			throw new ChessException("A peça escolhida pertence ao adversário.");
		}
		if(!board.getPiece(position).isThereAnyPossibleMove()) {
			throw new ChessException("Não há movimentos possíveis pra peça escolhida.");
		}
	}
	
	private void validateTargetPosition(Position source, Position target) {
		if (!this.board.getPiece(source).possibleMove(target)) {
			throw new ChessException("A peça escolhida não pode se mover para o destino selecionado.");
		}
	}
	
	private Piece makeMove(Position source, Position target) {
		ChessPiece p = (ChessPiece) board.removePiece(source);
		p.increaseMoveCount();
		Piece capturedPiece = board.removePiece(target);
		board.placePiece(p, target);
		if (capturedPiece != null) {
			this.piecesOnBoard.remove(capturedPiece);
			this.capturedPieces.add(capturedPiece);
		}
		
		// Roque pequeno
		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceRook = new Position(source.getRow(), source.getColumn() + 3);
			Position targetRook = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece) this.board.removePiece(sourceRook);
			this.board.placePiece(rook, targetRook);
			rook.increaseMoveCount();
		}
		
		// Roque grande
		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceRook = new Position(source.getRow(), source.getColumn() - 4);
			Position targetRook = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece) this.board.removePiece(sourceRook);
			this.board.placePiece(rook, targetRook);
			rook.increaseMoveCount();
		}
		
		// En passant
		if (p instanceof Pawn) {
			// Se o Peão andou na diagonal e não capturou nenhuma peça, ele realizou um en passant
			if (source.getColumn() != target.getColumn() && capturedPiece == null) {
				Position pawnPosition;
				if (Color.WHITE.equals(p.getColor())) {
					pawnPosition = new Position(target.getRow() + 1, target.getColumn());
				} else {
					pawnPosition = new Position(target.getRow() - 1, target.getColumn());
				}
				capturedPiece = this.board.removePiece(pawnPosition);
				this.capturedPieces.add(capturedPiece);
				this.piecesOnBoard.remove(capturedPiece);
			}
		}
				
		return capturedPiece;
	}
	
	private void undoMove(Position source, Position target, Piece capturedPiece) {
		ChessPiece p = (ChessPiece) board.removePiece(target);
		p.decreaseMoveCount();
		board.placePiece(p, source);
		
		if (capturedPiece != null) {
			this.board.placePiece(capturedPiece, target);
			this.capturedPieces.remove(capturedPiece);
			this.piecesOnBoard.add(capturedPiece);
		}
		
		// Roque pequeno
		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceRook = new Position(source.getRow(), source.getColumn() + 3);
			Position targetRook = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece) this.board.removePiece(targetRook);
			this.board.placePiece(rook, sourceRook);
			rook.decreaseMoveCount();
		}
		
		// Roque grande
		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceRook = new Position(source.getRow(), source.getColumn() - 4);
			Position targetRook = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece) this.board.removePiece(targetRook);
			this.board.placePiece(rook, sourceRook);
			rook.decreaseMoveCount();
		}
		
		// En passant
		if (p instanceof Pawn) {
			// Se o Peão andou na diagonal e não capturou nenhuma peça, ele realizou um en passant
			if (source.getColumn() != target.getColumn() && capturedPiece == this.enPassantVulnerable) {
				ChessPiece pawn = (ChessPiece) this.board.removePiece(target);
				Position pawnPosition;
				if (Color.WHITE.equals(p.getColor())) {
					pawnPosition = new Position(3, target.getColumn());
				} else {
					pawnPosition = new Position(4, target.getColumn());
				}
				this.board.placePiece(pawn, pawnPosition);
				
				capturedPiece = this.board.removePiece(pawnPosition);
			}
		}
	}
	
	public boolean[][] possibleMoves(ChessPosition sourcePosition) {
		Position position = sourcePosition.toPosition();
		this.validateSourcePosition(position);
		return this.board.getPiece(position).possibleMoves();
	}
	
	public void nextTurn() {
		this.turn++;
		this.currentPlayer = (WHITE.equals(this.currentPlayer) ? BLACK : WHITE);
	}
}
