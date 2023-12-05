import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Main {
	public static void main(String[] args) {
		int totalpagantes = 0;
		T t = new T();
		ArrayList<P> lsGeral = t.getLista();
		ArrayList<P> lsPagantes = new ArrayList();
		ArrayList<RP> lsReceberPagantes = new ArrayList();
		double total = t.calculaTotalMaisListaPagantes(lsGeral, lsPagantes);
		double vpp = t.calculaDividaIndividual(total, lsGeral, lsPagantes);
		ArrayList<P> lsAux = t.calcularReceberPagantesIgual(lsGeral, lsReceberPagantes);
		lsAux = t.calcularReceberPagantesSomaIgual(lsAux, lsReceberPagantes);

		t.print(total, vpp, lsGeral, lsPagantes, lsReceberPagantes);

	}
}

class T {
	public static final String COLOR_BLACK = "\u001B[30m";
	public static final String COLOR_RED = "\u001B[31m";
	public static final String COLOR_GREEN = "\u001B[32m";
	public static final String COLOR_YELLOW = "\u001B[33m";
	public static final String COLOR_BLUE = "\u001B[34m";
	public static final String COLOR_PURPLE = "\u001B[35m";
	public static final String COLOR_CYAN = "\u001B[36m";
	public static final String COLOR_WHITE = "\u001B[37m";
	public static final String COLOR_BLACK_BACKGROUND = "\u001B[40m";
	public static final String COLOR_RED_BACKGROUND = "\u001B[41m";
	public static final String COLOR_GREEN_BACKGROUND = "\u001B[42m";
	public static final String COLOR_YELLOW_BACKGROUND = "\u001B[43m";
	public static final String COLOR_BLUE_BACKGROUND = "\u001B[44m";
	public static final String COLOR_PURPLE_BACKGROUND = "\u001B[45m";
	public static final String COLOR_CYAN_BACKGROUND = "\u001B[46m";
	public static final String COLOR_WHITE_BACKGROUND = "\u001B[47m";

	public T() {
	}

	private double valorPendenteReceber(ArrayList<RP> lsReceberPagantes, P r) {
		double total = 0.0;
		for (RP rp : lsReceberPagantes) {
			if (rp.receber.equals(r)) {
				total += rp.valorPago;
			}
		}
		return r.diff - total;
	}

	private double valorPendentePagar(ArrayList<RP> lsReceberPagantes, P p) {
		double total = 0.0;
		for (RP rp : lsReceberPagantes) {
			if (rp.pagar.equals(p)) {
				total += rp.valorPago;
			}
		}
		return p.diff + total;
	}

	public ArrayList<P> calcularReceberPagantesSomaIgual(ArrayList<P> lsGeral, ArrayList<RP> lsReceberPagantes) {
		ArrayList<P> lsAux = (ArrayList<P>) lsGeral.clone();
		for (P r : lsGeral) {
			if (r.diff > 0) {
				for (P p : lsGeral) {
					if (lsGeral.indexOf(r) < lsGeral.indexOf(p) && p.diff < 0) {
						for (P p0 : lsGeral) {
							if (valorPendenteReceber(lsReceberPagantes, r) == 0.0) {
								break;
							}
							if (lsGeral.indexOf(p) < lsGeral.indexOf(p0) && p0.diff < 0 && !p.nome.equals(p0.nome)) {
								if (r.diff + (p.diff + p0.diff) == 0) {
									boolean exists = false;
									for (RP rp : lsReceberPagantes) {
										if (rp.receber.nome.equals(r.nome)
												&& (rp.pagar.nome.equals(p.nome) || rp.pagar.nome.equals(p0.nome))) {
											exists = true;
											break;
										}
									}
									if (!exists) {
										lsReceberPagantes.add(new RP(p.diff * -1, r, p));
										lsReceberPagantes.add(new RP(p0.diff * -1, r, p0));
										lsAux.remove(r);
										lsAux.remove(p);
										lsAux.remove(p0);
									}
								}
							}
						}
					}
				}
			}
		}
		return lsAux;
	}

	public ArrayList<P> calcularReceberPagantesIgual(ArrayList<P> lsGeral, ArrayList<RP> lsReceberPagantes) {
		ArrayList<P> lsAux = (ArrayList<P>) lsGeral.clone();
		for (P r : lsGeral) {
			if (r.diff > 0) {
				for (P p : lsGeral) {
					if (lsGeral.indexOf(r) < lsGeral.indexOf(p) && p.diff < 0 && r.diff + p.diff == 0) {
						boolean exists = false;
						for (RP rp : lsReceberPagantes) {
							if (rp.pagar.nome.equals(p.nome)) {
								exists = true;
								break;
							}
						}
						if (!exists) {
							lsReceberPagantes.add(new RP(p.diff * -1, r, p));
							lsAux.remove(r);
							lsAux.remove(p);
							break;
						}
					}
				}
			}
		}
		return lsAux;
	}

	public void print(double total, double vpp, ArrayList<P> p_lsGeral, ArrayList<P> lsPagantes,
			ArrayList<RP> lsReceberPagantes) {

		ArrayList<P> lsGeral = (ArrayList<P>) p_lsGeral.clone();
		Collections.sort(lsGeral, Comparator.comparingDouble(P::getDiff));
		Collections.reverse(lsGeral);

		int numsize = 7;
		int totalpagantes = lsPagantes.size();
		System.out.println("total pag: " + totalpagantes + "  total: "
				+ Utils.leftPad(String.valueOf(total), numsize, " ") + "  pp: " + vpp);
		System.out.println(" N nome " + Utils.leftPad("Pago", numsize, " ") + " "+Utils.leftPad("Diff", numsize, " ")+ ""+Utils.leftPad("Pend", numsize, " "));
		for (P p : lsGeral) {
			double valor_pendente = (p.diff > 0) ? valorPendenteReceber(lsReceberPagantes, p)
					: valorPendentePagar(lsReceberPagantes, p);
			String colorBg = (p.diff > 0) ? COLOR_GREEN_BACKGROUND : COLOR_RED_BACKGROUND;
			colorBg = (p.pagante) ? colorBg : COLOR_BLUE_BACKGROUND;
			String colorFnt = (valor_pendente!=0)?COLOR_BLACK:COLOR_WHITE;
					
			System.out.println(colorFnt+colorBg + Utils.leftPad(String.valueOf(lsGeral.indexOf(p)), 2, " ") + " " + p.nome + " "
					+ Utils.leftPad(String.valueOf(p.valor), numsize, " ") + " "
					+ Utils.leftPad(String.valueOf(p.diff), numsize, " ")
					+ Utils.leftPad(String.valueOf(valor_pendente), numsize, " ") + COLOR_BLACK_BACKGROUND
					+ COLOR_WHITE);
		}
		System.out.println(" N rcbd " + Utils.leftPad("valR", numsize, " ") +" "+"pgnt "+ Utils.leftPad("dvda", numsize, " ") + "   valorPago") ;
		for (RP rp : lsReceberPagantes) {
			System.out.println( Utils.leftPad(String.valueOf(lsReceberPagantes.indexOf(rp)), 2, " ") + " " + rp.receber.nome + " "
					+ Utils.leftPad(String.valueOf(rp.receber.diff), numsize, " ") + " " + rp.pagar.nome + " "
					+ Utils.leftPad(String.valueOf(rp.pagar.diff), numsize, " ") + " "
					+ Utils.leftPad(String.valueOf(rp.valorPago), numsize, " "));
		}
	}

	public double calculaDividaIndividual(double total, ArrayList<P> lsGeral, ArrayList<P> lsPagantes) {
		int totalpagantes = lsPagantes.size();
		double vpp = Utils.round(total / totalpagantes);
		for (P p : lsGeral) {
			if (p.pagante) {
				p.diff = Utils.round(p.valor - vpp);
			} else {
				p.diff = Utils.round(p.valor);
			}
		}
		return vpp;
	}

	public double calculaTotalMaisListaPagantes(ArrayList<P> lsGeral, ArrayList<P> lsPagentes) {
		double total = 0;
		for (P p : lsGeral) {
			if (p.pagante) {
				lsPagentes.add(p);
			}
			total += p.valor;
		}
		return Utils.round(total);
	}

	public ArrayList<P> getLista() {
		ArrayList<P> lista = new ArrayList();
		lista.addAll(getLista0());
		lista.addAll(getLista1());
		lista.addAll(getLista2());
		lista.addAll(getLista3());

		return lista;
	}

	public ArrayList<P> getLista0() {
		ArrayList<P> lista = new ArrayList();
		lista.add(new P("bos0", 500.00, false));
		lista.add(new P("max0", 700.00, true));
		lista.add(new P("med0", 300.00, true));
		lista.add(new P("bai0", 100.00, true));
		lista.add(new P("nad0", 0.00, true));
		return lista;
	}

	public ArrayList<P> getLista1() {
		ArrayList<P> lista = new ArrayList();
		lista.add(new P("max1", 1000.00, true));
		lista.add(new P("med1", 400.00, true));
		lista.add(new P("bai1", 200.00, true));
		return lista;
	}

	public ArrayList<P> getLista2() {
		ArrayList<P> lista = new ArrayList();
		lista.add(new P("max2", 300.00, true));
		lista.add(new P("med2", 200.00, true));
		lista.add(new P("bai2", 100.00, true));
		return lista;
	}

	public ArrayList<P> getLista3() {
		ArrayList<P> lista = new ArrayList();
		lista.add(new P("max3", 500.00, true));
		lista.add(new P("med3", 400.00, true));
		lista.add(new P("bai3", 200.00, true));
		lista.add(new P("nad3", 0.00, true));
		return lista;
	}
}

class P {
	public String nome;
	public double valor;
	public boolean pagante;
	public double diff;

	public P(String nome, double valor, boolean pagante) {
		this.nome = nome;
		this.valor = Utils.round(valor);
		this.pagante = pagante;
	}

	public double getDiff() {
		return this.diff;
	}
}

class RP {
	public double valorPago;
	public P receber;
	public P pagar;

	public RP(double valor, P receber, P pagar) {
		this.valorPago = Utils.round(valor);
		this.receber = receber;
		this.pagar = pagar;
	}
}

class Utils {
	public static final String leftPad(String valor, int size, String preenchimento) {
		String aux = "";
		if (valor.length() > size)
			return valor;
		for (int i = 0; i < size; i++)
			aux += preenchimento;
		return aux.substring(valor.length()) + valor;
	}

	public static final double round(double valor) {
		return Math.round(valor);// Math.round(valor * 10) / 10;
	}
}