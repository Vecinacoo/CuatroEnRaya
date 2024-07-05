package modelo;

import java.util.Arrays;
import java.util.Scanner;

public class CuatroEnRaya {

	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_RESET = "\u001B[0m";

	private static final int FICHAJUGADOR = 1;
	private static final int FICHAROBOT = 2;
	private static final int VACIO = 0;

	private static final int TURNOJUGADOR = 1;
	private static final int TURNOROBOT = 2;

	public int turno;

	public int[][] tablero;

	public CuatroEnRaya() {

		this.tablero = new int[6][7];
		turno = TURNOJUGADOR;
	}

	public void jugar() {

		Scanner sc = new Scanner(System.in);
		int posicion = -1;

		pintar();

		do {

			if (turno == TURNOJUGADOR) {

				System.out.println("Donde quieres tirar " + ANSI_RED + "O" + ANSI_RESET);
				posicion = sc.nextInt() - 1;

				if (sePuedePonerFicha(posicion)) {
					ponerFicha(posicion, turno, tablero);
				}

			} else {
				
				
				int c = decidirMejorJugada(this.tablero,turno);
				
						ponerFicha(c, FICHAROBOT, this.tablero);
				
				turno = TURNOROBOT;
			}

			pintar();
			turno = (turno == TURNOJUGADOR) ? TURNOROBOT : TURNOJUGADOR;
		} while (!hayEmpate());

	}

	private int decidirMejorJugada(int[][] tab, int turno) {
		
		int mejorPuntuacion = Integer.MIN_VALUE;
		int mejorColumna = -1;
		
		for (int columna = 0; columna < 7; columna++) {
			
			if(sePuedePonerFicha(columna, tab)) {
				
				 int[][] tableroTemporal = Arrays.stream(tablero).map(int[]::clone).toArray(int[][]::new);
		            ponerFicha(columna, turno, tableroTemporal);
		            int nuevoTurno = (turno == TURNOJUGADOR) ? TURNOROBOT : TURNOJUGADOR;
		            int puntuacion = -negamax(tableroTemporal, Integer.MIN_VALUE, Integer.MAX_VALUE, nuevoTurno);

		            if(puntuacion> mejorPuntuacion) {
		            	mejorPuntuacion = puntuacion;
		            	mejorColumna = columna;
		            }
			}
			
		}
		return mejorColumna;
	}

	private int numeroMovimientos(int[][] tab) {

		int nMovimientos = 42;

		for (int f = 0; f < 6; f++) {
			for (int c = 0; c < 7; c++) {
				if (tab[f][c] == VACIO) {
					nMovimientos--;
				}
			}
		}
		return nMovimientos;
	}

	private void pintar() {
		String fila = "";

		System.out.println(" | 1 | 2 | 3 | 4 | 5 | 6 | 7");

		for (int f = 0; f < 6; f++) {
			for (int c = 0; c < 7; c++) {
				fila += " | " + convertirParaVer(tablero[f][c]);
			}
			System.out.println(fila);
			fila = "";
		}

	}

	private String convertirParaVer(int i) {
		String ficha = "Z";

		if (i == FICHAJUGADOR) {
			ficha = ANSI_RED + "O";
		} else if (i == FICHAROBOT) {
			ficha = ANSI_YELLOW + "O";
		}

		return ficha + ANSI_RESET;

	}

	public int negamax(int[][] tab, int alpha, int beta, int nuevoTurno) {

		if (hayEmpate()) {
			return 0;
		}

		for (int columna = 0; columna < 7; columna++) {
			int[][] tableroTemporal = Arrays.stream(tab).map(int[]::clone).toArray(int[][]::new);
			if (sePuedePonerFicha(columna, tableroTemporal)) {

				ponerFicha(columna, nuevoTurno, tableroTemporal);

				if (hayGanador(tableroTemporal)) {
					return (43 - numeroMovimientos(tableroTemporal) / 2);

				}

			}

		}

		int max = (-42-numeroMovimientos(tab))/2;
		
		if(beta > max) {
			beta = max;
			if (alpha>= beta) return beta;
		}

		for (int columna = 0; columna < 7; columna++) {
			int[][] tableroTemporal = Arrays.stream(tab).map(int[]::clone).toArray(int[][]::new);
			if (sePuedePonerFicha(columna, tableroTemporal)) {

				ponerFicha(columna, turno, tableroTemporal);
				turno = (turno == TURNOJUGADOR) ? TURNOROBOT : TURNOJUGADOR;
				
				int puntuacion = -negamax(tableroTemporal,-beta,-alpha,nuevoTurno);
				
				if (puntuacion >= alpha) return puntuacion;
				if(puntuacion > alpha) alpha = puntuacion;
					
			}

		}

		return alpha;

	}

	private boolean hayEmpate() {
		return hayEmpate(this.tablero);
	}

	private boolean hayEmpate(int[][] tab) {

		return hayGanador(tab) && hayMasTiradas(tab);

	}

	public boolean hayMasTiradas(int[][] tablero) {

		for (int i = 0; i < 7; i++) {
			if (tablero[0][i] == VACIO) {
				return true;
			}
		}

		return false;
	}

	public boolean sePuedePonerFicha(int columna, int[][] tab) {

		return tab[0][columna] == VACIO;

	}

	public boolean sePuedePonerFicha(int columna) {

		return sePuedePonerFicha(columna, this.tablero);

	}

	private int ponerFicha(int columna, int ficha, int[][] tab) {

		for (int f = 5; f >= 0; f--) {
			if (tab[f][columna] == VACIO) {
				tab[f][columna] = ficha;
				return f;
			}
		}

		return -1;

	}

	public boolean hayGanador(int[][] tableroTemporal) {

		long start = System.currentTimeMillis();

		boolean hayGanador = false;

		for (int f = 0; f < 6 && !hayGanador; f++) {
			for (int c = 0; c < 7 && !hayGanador; c++) {

				if (comprobarPosicionADerecha(tableroTemporal, f, c)) {
					hayGanador = true;
				} else if (comprobarPosicionAIzquierda(tableroTemporal, f, c)) {
					hayGanador = true;
				} else if (comprobarArribaAAbajo(tableroTemporal, f, c)) {
					hayGanador = true;
				} else if (comprobarHaciaArriba(tableroTemporal, f, c)) {
					hayGanador = true;
				} else if (comprobarDiagonalHaciaEsquinaAbajoDerecha(tableroTemporal, f, c)) {
					hayGanador = true;
				} else if (comprobarDiagonalHaciaEsquinaSuperiorDerecha(tableroTemporal, f, c)) {
					hayGanador = true;
				} else if (comprobarDiagonalHaciaEsquinaAbajoIzquierda(tableroTemporal, f, c)) {
					hayGanador = true;
				} else if (comprobarDiagonalHaciaEsquinaArribaIzquierda(tableroTemporal, f, c)) {
					hayGanador = true;
				}

			}
		}
		// System.out.println((System.currentTimeMillis()-start)/1000.0);
		return hayGanador;
	}

	private boolean comprobarDiagonalHaciaEsquinaAbajoDerecha(int[][] tableroTemporal, int f, int c) {

		try {

			return tableroTemporal[f][c] != VACIO && tableroTemporal[f][c] == tableroTemporal[f + 1][c + 1]
					&& tableroTemporal[f][c] == tableroTemporal[f + 2][c + 2]
					&& tableroTemporal[f][c] == tableroTemporal[f + 3][c + 3];
		} catch (Exception e) {
			return false;
		}
	}

	private boolean comprobarDiagonalHaciaEsquinaArribaIzquierda(int[][] tableroTemporal, int f, int c) {
		try {

			return tableroTemporal[f][c] != VACIO && tableroTemporal[f][c] == tableroTemporal[f - 1][c - 1]
					&& tableroTemporal[f][c] == tableroTemporal[f - 2][c - 2]
					&& tableroTemporal[f][c] == tableroTemporal[f - 3][c - 3];
		} catch (Exception e) {
			return false;
		}
	}

	private boolean comprobarDiagonalHaciaEsquinaSuperiorDerecha(int[][] tableroTemporal, int f, int c) {
		try {

			return tableroTemporal[f][c] != VACIO && tableroTemporal[f][c] == tableroTemporal[f - 1][c + 1]
					&& tableroTemporal[f][c] == tableroTemporal[f - 2][c + 2]
					&& tableroTemporal[f][c] == tableroTemporal[f - 3][c + 3];
		} catch (Exception e) {
			return false;
		}
	}

	private boolean comprobarDiagonalHaciaEsquinaAbajoIzquierda(int[][] tableroTemporal, int f, int c) {
		try {

			return tableroTemporal[f][c] != VACIO && tableroTemporal[f][c] == tableroTemporal[f + 1][c - 1]
					&& tableroTemporal[f][c] == tableroTemporal[f + 2][c - 2]
					&& tableroTemporal[f][c] == tableroTemporal[f + 3][c - 3];

		} catch (Exception e) {
			return false;
		}
	}

	private boolean comprobarArribaAAbajo(int[][] tableroTemporal, int f, int c) {
		try {

			return tableroTemporal[f][c] != VACIO && tableroTemporal[f][c] == tableroTemporal[f + 1][c]
					&& tableroTemporal[f][c] == tableroTemporal[f + 2][c]
					&& tableroTemporal[f][c] == tableroTemporal[f + 3][c];
		} catch (Exception e) {
			return false;
		}
	}

	private boolean comprobarHaciaArriba(int[][] tableroTemporal, int f, int c) {
		try {

			return tableroTemporal[f][c] != VACIO && tableroTemporal[f][c] == tableroTemporal[f - 1][c]
					&& tableroTemporal[f][c] == tableroTemporal[f - 2][c]
					&& tableroTemporal[f][c] == tableroTemporal[f - 3][c];
		} catch (Exception e) {
			return false;
		}
	}

	private boolean comprobarPosicionADerecha(int[][] tableroTemporal, int f, int c) {
		try {
			return tableroTemporal[f][c] != VACIO && tableroTemporal[f][c] == tableroTemporal[f][c + 1]
					&& tableroTemporal[f][c] == tableroTemporal[f][c + 2]
					&& tableroTemporal[f][c] == tableroTemporal[f][c + 3];
		}

		catch (Exception e) {
			return false;
		}

	}

	private boolean comprobarPosicionAIzquierda(int[][] tableroTemporal, int f, int c) {

		try {

			return tableroTemporal[f][c] != VACIO && tableroTemporal[f][c] == tableroTemporal[f][c - 1]
					&& tableroTemporal[f][c] == tableroTemporal[f][c - 2]
					&& tableroTemporal[f][c] == tableroTemporal[f][c - 3];

		} catch (Exception e) {
			return false;
		}
	}
}
