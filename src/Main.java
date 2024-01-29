import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

public class Main {
	public static void main(String[] args) {
		Processo processo = new Processo();
		processo.v2();
	}
}

class Processo {
	public static ArrayList<P> lsGeral;
	public static int totalPessoas;
	public static int totalPagantes;
	public static double total;
	public static double vpp;
	public static ArrayList<P> lsNaoIsentos;
	
	public void v1() {
		totalPagantes = 0;
		Tool t = new Tool();
		ArrayList<P> lsGeral = t.getLista();
		ArrayList<P> lsPagantes = new ArrayList();
		ArrayList<P> lsPaga = new ArrayList();
		ArrayList<P> lsRecebe = new ArrayList();
		ArrayList<RP> lsReceberPagantes = new ArrayList();
		total = t.calculaTotal(lsGeral, lsPagantes);
		vpp = t.calculaDividaPorPessoa(total, lsGeral, lsPagantes);
		// lsPaga= (ArrayList<P>) lsGeral.stream().filter(pp -> pp.diff <
		// 1).collect(Collectors.toList());
		// lsRecebe= (ArrayList<P>) lsGeral.stream().filter(pp -> pp.diff >
		// 1).collect(Collectors.toList());
		// t.calcula(total,vpp,lsPaga,lsRecebe,lsReceberPagantes);
		ArrayList<P> lsAux = t.calcularReceberPagantesIgual(lsGeral, lsReceberPagantes);
		lsAux = t.calcularReceberPagantesSomaIgual(lsAux, lsReceberPagantes);
		t.print(total, vpp, lsGeral, lsPagantes.size(), lsReceberPagantes);
	}

	public void v2() {
		Tool tool = new Tool();
		lsGeral = tool.getLista();
		totalPessoas=lsGeral.size();
		lsNaoIsentos = new ArrayList();
		ArrayList<RP> lsReceberPagar = new ArrayList();
		total = tool.calculaTotal(lsGeral, lsNaoIsentos);
		vpp = tool.calculaDividaPorPessoa(total, lsGeral, lsNaoIsentos);
		 //lsReceberPagar.add(new RP(lsGeral.get(1),lsGeral.get(0)));
		 //lsReceberPagar.add(new RP(lsGeral.get(3),null));
		 //lsReceberPagar.add(new RP(null,lsGeral.get(3)));
		ArrayList<P> lsGeralAtual = (ArrayList<P>) lsGeral.stream().filter(t -> t.getSaldoInicial() != 0)
				.collect(Collectors.toList());
		totalPagantes=lsGeralAtual.size();
		int numsize = 7;
		tool.sortGeral(lsGeral);
		tool.sortGeral(lsGeralAtual);
		Tool.print(total, vpp, lsGeralAtual, lsNaoIsentos.size(), lsReceberPagar);
		tool.resolver(lsGeralAtual, lsReceberPagar, new ArrayList<P>(), 1);
		
		Tool.print(total, vpp, lsGeral, lsNaoIsentos.size(), lsReceberPagar);
	}
}

class Tool {
	private int numsize=7;
	public Tool() {
	}

	public void resolver(ArrayList<P> p_lsGeralAtual, ArrayList<RP> p_lsReceberPagantes, ArrayList<P> p_lsJaForamDestaVez,
			int qta) {
		if (p_lsGeralAtual.isEmpty())
			return;
		P p = new P("  ", 0, false);
		P parceiro = null;
		sortGeral(p_lsGeralAtual);
		if (hasParSozinho(p_lsReceberPagantes)) {
			RP rp = pegarSozinho(p_lsReceberPagantes);
			p = pegarSozinho(rp);
			if (p != null) {
				for (int i = 0; i < qta; i++) {
					if (i == qta - 1) {
						parceiro = extrairParceiro(p_lsGeralAtual,p_lsJaForamDestaVez, p);
					} else {
						parceiro = extrairPrimeiroParceiro(p_lsGeralAtual,p_lsJaForamDestaVez, p);
					}												
					if (parceiro != null) {
						double valorAux = parceiro.getSaldoAtual();
						if (i > 0) {
							rp = adicionaLsReceberPagantes(p_lsReceberPagantes, p);
						}
						if (p.isReceber()) {
							rp.pagar = parceiro;
						} else {
							rp.receber = parceiro;
							valorAux = valorAux * -1;
						}
						rp.pagar.setSaldoAtual(rp.pagar.getSaldoAtual() - valorAux);
						rp.receber.setSaldoAtual(rp.receber.getSaldoAtual() + valorAux);
						
					} else {
						resetarAndamentoParcial(p_lsGeralAtual, p_lsReceberPagantes, p_lsJaForamDestaVez, rp, p);
					}
				}
			} else {
				System.out.println("erro no tem sozinho");
			}
		}
		if (p_lsGeralAtual.isEmpty()) {
			return;
		}
		if (p_lsGeralAtual.size() < qta + 1) {
			resetGeral(p_lsGeralAtual,p_lsReceberPagantes,p_lsJaForamDestaVez);
			sortGeral(p_lsGeralAtual);
			Tool.print(Processo.total, Processo.vpp, p_lsGeralAtual, Processo.lsNaoIsentos.size(), p_lsReceberPagantes);
			qta++;
			resolver(p_lsGeralAtual, p_lsReceberPagantes, new ArrayList<P>(), qta);
		}
		if (p_lsGeralAtual.isEmpty()) {
			return;
		}
		P pnew = extrairPrimeiro(p_lsGeralAtual,p_lsJaForamDestaVez);
		adicionaLsReceberPagantes(p_lsReceberPagantes, pnew);
		resolver(p_lsGeralAtual, p_lsReceberPagantes, p_lsJaForamDestaVez, qta);
		if (p_lsGeralAtual.isEmpty()) {
			return;
		}
	}

	public void sortGeral(ArrayList<P> p_lsGeralAtual) {
		Collections.sort(p_lsGeralAtual, Comparator.comparingDouble(P::getSaldoAtualPositivo));
		Collections.reverse(p_lsGeralAtual);
	}

	private void resetGeral(ArrayList<P> p_lsGeralAtual, ArrayList<RP> p_lsReceberPagantes,
			ArrayList<P> p_lsJaForamDestaVez) {
		p_lsGeralAtual.addAll(p_lsJaForamDestaVez);
		p_lsGeralAtual.removeAll(getAllRP(p_lsReceberPagantes));
		p_lsGeralAtual.stream().forEach(t->t.resetSaldoAtual());
		p_lsJaForamDestaVez=new ArrayList<P>();
	}

	private ArrayList<P> getAllRP(ArrayList<RP> p_lsReceberPagantes) {
		ArrayList<P> p_resolvidos = new ArrayList<P>();
		for(RP rp : p_lsReceberPagantes) {
			if (!p_resolvidos.contains(rp.pagar)) { 
				p_resolvidos.add(rp.pagar); 
            } 
			if (!p_resolvidos.contains(rp.receber)) { 
				p_resolvidos.add(rp.receber); 
            } 
		}
		return p_resolvidos;
	}

	private static void pnt(String string) {
		System.out.print(string);
	}

	private static void resetarAndamentoParcial(ArrayList<P> p_lsGeralAtual, ArrayList<RP> p_lsReceberPagantes,
			ArrayList<P> p_lsJaForamDestaVez, RP rp, P p) {
		p_lsReceberPagantes.remove(rp);
		
		if (p.isReceber()) {
			ArrayList<RP> lsAux = (ArrayList<RP>) p_lsReceberPagantes.stream().filter(o -> o.receber.equals(p)&&o.pagar!=null)
					.collect(Collectors.toList());
			for (RP rpAux : lsAux) {
				rpAux.pagar.resetSaldoAtual();
				p_lsGeralAtual.add(rpAux.pagar);
			}
			p_lsReceberPagantes.removeAll(lsAux);
		} else {
			ArrayList<RP> lsAux = (ArrayList<RP>) p_lsReceberPagantes.stream().filter(o -> o.pagar.equals(p)&&o.receber!=null)
					.collect(Collectors.toList());
			for (RP rpAux : lsAux) {
				rpAux.receber.resetSaldoAtual();
				p_lsGeralAtual.add(rpAux.receber);
			}
			p_lsReceberPagantes.removeAll(lsAux);
		}
		p.resetSaldoAtual();		
	}

	private RP adicionaLsReceberPagantes(ArrayList<RP> lsReceberPagantes, P pnew) {
		if (pnew.isReceber()) {
			lsReceberPagantes.add(new RP(pnew, null));
		} else {
			lsReceberPagantes.add(new RP(null, pnew));
		}
		return pegarSozinho(lsReceberPagantes);
	}

	private P extrairParceiro(ArrayList<P> p_lsGeralAtual, ArrayList<P> p_lsJaForamDestaVez, P p) {
		ArrayList<P> pLsPossiveisParceiros = (ArrayList<P>) p_lsGeralAtual.stream()
				.filter(o -> p.getSaldoAtual() + o.getSaldoAtual() == 0).collect(Collectors.toList());
		if (pLsPossiveisParceiros.isEmpty())
			return null;
		P parceiro = pLsPossiveisParceiros.get(0);
		p_lsGeralAtual.remove(parceiro);
		p_lsJaForamDestaVez.add(parceiro);
		return parceiro;
	}

	private P extrairPrimeiroParceiro(ArrayList<P> p_lsGeralAtual, ArrayList<P> p_lsJaForamDestaVez, P p) {
		ArrayList<P> p_lsPossiveisParceiros = (ArrayList<P>) p_lsGeralAtual.stream()
				.filter(o -> (p.isReceber() && !o.isReceber() && p.getSaldoAtual() > o.getSaldoAtual())
						|| (!p.isReceber() && o.isReceber() && p.getSaldoAtual() < o.getSaldoAtual()))
				.collect(Collectors.toList());
		int index = 0;
		if(p_lsPossiveisParceiros.isEmpty())return null;
		P paux = p_lsPossiveisParceiros.get(index);
		p_lsGeralAtual.remove(paux);
		p_lsJaForamDestaVez.add(paux);
		return paux;
	}

	private P pegarSozinho(RP rp) {
		if (rp != null) {
			if (rp.pagar == null) {
				return rp.receber;
			} else {
				return rp.pagar;
			}
		}
		return null;

	}

	private RP pegarSozinho(ArrayList<RP> lsReceberPagantes) {
		if (hasParSozinho(lsReceberPagantes)) {
			return (RP) lsReceberPagantes.stream().filter(rp -> !(rp.receber != null && rp.pagar != null))
					.collect(Collectors.toList()).get(0);
		} else {
			return null;
		}
	}

	private boolean hasParSozinho(ArrayList<RP> lsReceberPagantes) {
		if (lsReceberPagantes.isEmpty())
			return false;
		lsReceberPagantes = (ArrayList<RP>) lsReceberPagantes.stream()
				.filter(rp -> !(rp.receber != null && rp.pagar != null)).collect(Collectors.toList());
		if (lsReceberPagantes.isEmpty())
			return false;
		return true;
	}

	private P extrairPrimeiro(ArrayList<P> p_lsGeralAtual, ArrayList<P> p_lsJaForamDestaVez) {
		int index = 0;
		P p = p_lsGeralAtual.get(index);
		p_lsGeralAtual.remove(p);
		p_lsJaForamDestaVez.add(p);
		return p;
	}
	
	

	public static double valorPendenteReceber(ArrayList<RP> lsReceberPagantes, P r) {
		double total = 0.0;
		for (RP rp : lsReceberPagantes) {
			if (rp.receber.equals(r)) {
				if (rp.receber.getSaldoInicialPositivo() > rp.pagar.getSaldoInicialPositivo()) {
					total += rp.pagar.getSaldoInicialPositivo();
				} else {
					total += rp.receber.getSaldoInicial();
				}
			}
		}
		return r.getSaldoInicial() - total;
	}

	private static double valorPendentePagar(ArrayList<RP> lsReceberPagantes, P p) {
		double total = 0.0;
		for (RP rp : lsReceberPagantes) {
			if (rp.pagar.equals(p)) {
				total += rp.receber.getSaldoInicial();
			}
		}
		return p.getSaldoInicial() + total;
	}

	public ArrayList<P> calcularReceberPagantesSomaIgual(ArrayList<P> lsGeral, ArrayList<RP> lsReceberPagantes) {
		ArrayList<P> lsAux = (ArrayList<P>) lsGeral.clone();
		for (P r : lsGeral) {
			if (r.getSaldoInicial() > 0) {
				for (P p : lsGeral) {
					if (lsGeral.indexOf(r) < lsGeral.indexOf(p) && p.getSaldoInicial() < 0) {
						for (P p0 : lsGeral) {
							if (valorPendenteReceber(lsReceberPagantes, r) == 0.0) {
								break;
							}
							if (lsGeral.indexOf(p) < lsGeral.indexOf(p0) && p0.getSaldoInicial() < 0
									&& !p.getNome().equals(p0.getNome())) {
								if (r.getSaldoInicial() + (p.getSaldoInicial() + p0.getSaldoInicial()) == 0) {
									boolean exists = false;
									for (RP rp : lsReceberPagantes) {
										if (rp.receber.getNome().equals(r.getNome())
												&& (rp.pagar.getNome().equals(p.getNome())
														|| rp.pagar.getNome().equals(p0.getNome()))) {
											exists = true;
											break;
										}
									}
									if (!exists) {
										lsReceberPagantes.add(new RP(r, p));
										lsReceberPagantes.add(new RP(r, p0));
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
			if (r.getSaldoInicial() > 0) {
				for (P p : lsGeral) {
					if (lsGeral.indexOf(r) < lsGeral.indexOf(p) && p.getSaldoInicial() < 0
							&& r.getSaldoInicial() + p.getSaldoInicial() == 0) {
						boolean exists = false;
						for (RP rp : lsReceberPagantes) {
							if (rp.pagar.getNome().equals(p.getNome())) {
								exists = true;
								break;
							}
						}
						if (!exists) {
							lsReceberPagantes.add(new RP(r, p));
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

	public static void printTest(double total, double vpp, ArrayList<P> p_lsGeral,ArrayList<RP> lsReceberPagantes) {
		int numsize = 7;
		int totalGeral=p_lsGeral.size();
		Tool.printTotal(total, vpp, totalGeral, numsize);
		Tool.printLsPessoas(p_lsGeral, numsize);
		Tool.printLsRP(lsReceberPagantes, numsize);
		//tool.clearScreen();
	}

	public static void print(double total, double vpp, ArrayList<P> p_lsGeral, int totalPagantes,
			ArrayList<RP> lsReceberPagantes) {

		int numsize = 7;
		printTotal(total, vpp, totalPagantes, numsize);
		printLsPessoas(p_lsGeral, numsize);
		printLsRP(lsReceberPagantes, numsize);
	}

	public static void printLsRP(ArrayList<RP> lsReceberPagantes, int numsize) {
		printRPHeader(numsize);
		for (RP rp : lsReceberPagantes) {
			printRP(lsReceberPagantes.indexOf(rp), numsize, rp);
		}
	}
	public static void printRPHeader(int numsize) {
		String colorBg = COLOR.BG_WHITE;
		String colorFnt = COLOR.FG_BLACK;
		printRPHeader(numsize, colorBg, colorFnt);
	}
	public static void printRPHeader(int numsize, String colorBg, String colorFnt) {
		System.out.println(colorFnt + colorBg + " N|" + "NmRc|" + Utils.leftPad("vlARc", numsize, " ") + "|"
				+ Utils.leftPad("vlIRc", numsize, " ") + "|" + "NmPg|" + Utils.leftPad("vlAPg", numsize, " ") + "|"
				+ Utils.leftPad("vlIPg", numsize, " ") + "|");
	}
	public static void printRP(int index, int numsize, RP rp) {
		String colorBg = COLOR.BG_BLACK;
		String colorFnt = COLOR.FG_WHITE;
		printRP(index, numsize, colorBg, colorFnt,  rp);
	}
	public static void printRP(int index, int numsize, String colorBg, String colorFnt, RP rp) {
		P receber = ((rp.receber != null) ? rp.receber : new P("    ", 0, false));
		P pagar = ((rp.pagar != null) ? rp.pagar : new P("    ", 0, false));
		System.out.println(colorFnt + colorBg + Utils.leftPad(String.valueOf(index), 2, " ")
				+ "|" + receber.getNome() + "|"
				+ Utils.leftPad(String.valueOf(receber.getSaldoAtual()), numsize, " ") + "|"
				+ Utils.leftPad(String.valueOf(receber.getSaldoInicial()), numsize, " ") + "|" + pagar.getNome()
				+ "|" + Utils.leftPad(String.valueOf(pagar.getSaldoAtual()), numsize, " ") + "|"
				+ Utils.leftPad(String.valueOf(pagar.getSaldoInicial()), numsize, " ") + "|");
	}

	public static void printLsPessoas(ArrayList<P> p_lsGeral, int numsize) {
		printPessoaHeader(numsize);
		for (P p : p_lsGeral) {
			int index = p_lsGeral.indexOf(p);
			printPessoa(numsize, p, index);
		}
	}
	public static void printPessoaHeader(int numsize) {
		String colorBg = COLOR.BG_WHITE;
		String colorFnt = COLOR.FG_BLACK;
		printPessoaHeader(numsize, colorBg, colorFnt);
	}
	public static void printPessoaHeader(int numsize, String colorBg, String colorFnt) {
		System.out.println(colorFnt + colorBg + " N|nome|" + Utils.leftPad("ValPago", numsize, " ") + "|"
				+ Utils.leftPad("SalInit", numsize, " ") + "|" + Utils.leftPad("SalAtua", numsize, " ") + "|"
				+ Utils.leftPad("Tipo", numsize, " ") + "|");
	}
	public static void printPessoa(int numsize, P p, int index) {
		String colorBg = COLOR.BG_BLACK;
		String colorFnt = COLOR.FG_WHITE;
		printPessoa( numsize,  colorBg,  colorFnt,  p, index);
	}
	public static void printPessoa(int numsize, String colorBg, String colorFnt, P p, int index) {
		String tipo = (p.getSaldoInicial() > 0) ? " recebe" : " paga";
		tipo = (p.isNaoIsento()) ? tipo : " isento";
		System.out.println(colorFnt + colorBg + Utils.leftPad(String.valueOf(index), 2, " ") + "|"
				+ p.getNome() + "|" + Utils.leftPad(String.valueOf(p.getValorPago()), numsize, " ") + "|"
				+ Utils.leftPad(String.valueOf(p.getSaldoInicial()), numsize, " ") + "|"
				+ Utils.leftPad(String.valueOf(p.getSaldoAtual()), numsize, " ") + "|"
				+ Utils.leftPad(tipo, numsize, " ") + "|");
	}

	public static void printTotal(double total, double vpp, int totalpagantes, int numsize) {
		String colorBg = COLOR.BG_BLACK;
		String colorFnt = COLOR.FG_GREEN;
		System.out.println(colorFnt + colorBg + "total pag: " + totalpagantes + "  total: "
				+ Utils.leftPad(String.valueOf(total), numsize, " ") + "  pp: " + vpp);
	}

	public void clearScreen() {
		System.out.print("\033[H\033[2J");
		System.out.flush();
		//final String vESC = "\033[";
		//System.out.print(vESC + "2J"); 
	}
	
	public static double calculaDividaPorPessoa(double valorTotal, ArrayList<P> lsGeral, ArrayList<P> lsNaoIsentos) {
		int totalNaoIsentos = lsNaoIsentos.size();
		double valorPorPessoa = Utils.round(valorTotal / totalNaoIsentos);
		for (P p : lsGeral) {
			if (p.isNaoIsento()) {
				p.setSaldoInicial(Utils.round(p.getValorPago() - valorPorPessoa));
			} else {
				p.setSaldoInicial(Utils.round(p.getValorPago()));
			}
			p.setSaldoAtual(p.getSaldoInicial());
		}
		return valorPorPessoa;
	}

	public static double calculaTotal(ArrayList<P> lsGeral, ArrayList<P> lsNaoIsento) {
		double valorTotal = 0;
		for (P p : lsGeral) {
			if (p.isNaoIsento()) {
				lsNaoIsento.add(p);
			}
			valorTotal += p.getValorPago();
		}
		return Utils.round(valorTotal);
	}

	public static ArrayList<P> getLista() {
		ArrayList<P> lista = new ArrayList();
		lista.addAll(getLista0());
		lista.addAll(getLista1());
		//lista.addAll(getLista2());
		 lista.addAll(getLista3());

		return lista;
	}

	public static ArrayList<P> getLista0() {
		ArrayList<P> lista = new ArrayList();
		lista.add(new P("bos0", 500.00, false));
		lista.add(new P("max0", 700.00, true));
		lista.add(new P("for0", 400.00, true));
		lista.add(new P("med0", 300.00, true));
		lista.add(new P("bai0", 100.00, true));
		lista.add(new P("nad0", 0.00, true));
		return lista;
	}

	public static ArrayList<P> getLista1() {
		ArrayList<P> lista = new ArrayList();
		lista.add(new P("max1", 1000.00, true));
		lista.add(new P("med1", 400.00, true));
		lista.add(new P("bai1", 200.00, true));
		return lista;
	}

	public static ArrayList<P> getLista2() {
		ArrayList<P> lista = new ArrayList();
		lista.add(new P("max2", 300.00, true));
		lista.add(new P("med2", 200.00, true));
		lista.add(new P("bai2", 100.00, true));
		return lista;
	}

	public static ArrayList<P> getLista3() {
		ArrayList<P> lista = new ArrayList();
		lista.add(new P("max3", 500.00, true));
		lista.add(new P("med3", 400.00, true));
		lista.add(new P("bai3", 200.00, true));
		lista.add(new P("nad3", 0.00, true));
		return lista;
	}
}

class P {
	private String nome;
	private double valorPago;
	private boolean naoIsento;
	private double saldoInicial;
	private double saldoAtual;

	public P(String nome, double valorPago, boolean naoIsento) {
		this.nome = nome;
		this.valorPago = Utils.round(valorPago);
		this.naoIsento = naoIsento;
	}

	public void resetSaldoAtual() {
		this.saldoAtual = this.saldoInicial;
	}

	public String getNome() {
		return nome;
	}

	public double getValorPago() {
		return valorPago;
	}

	public boolean isNaoIsento() {
		return naoIsento;
	}

	public double getSaldoInicial() {
		return this.saldoInicial;
	}

	public double getSaldoAtual() {
		return this.saldoAtual;
	}

	public double getSaldoInicialPositivo() {
		return Math.sqrt(Math.pow(this.saldoInicial, 2));
	}

	public double getSaldoAtualPositivo() {
		return Math.sqrt(Math.pow(this.saldoAtual, 2));
	}

	public boolean isSaldoInicialMaior(P o2) {
		return getSaldoInicial() > o2.getSaldoInicial();
	}

	public boolean isSaldoInicialPositvoMaior(P o2) {
		return getSaldoInicialPositivo() > o2.getSaldoInicialPositivo();
	}

	public boolean isSaldoAtualMaior(P o2) {
		return getSaldoAtual() > o2.getSaldoAtual();
	}

	public boolean isSaldoAtualPositvoMaior(P o2) {
		return getSaldoAtualPositivo() > o2.getSaldoAtualPositivo();
	}

	public boolean isReceber() {
		return getSaldoAtual() > 0;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setValorPago(double valorPago) {
		this.valorPago = valorPago;
	}

	public void setNaoIsento(boolean naoIsento) {
		this.naoIsento = naoIsento;
	}

	public void setSaldoInicial(double saldoInicial) {
		this.saldoInicial = saldoInicial;
	}

	public void setSaldoAtual(double saldoAtual) {
		this.saldoAtual = saldoAtual;
	}

	@Override
	public String toString() {
		return "P{" + "nome='" + getNome() + '\'' + ", saldoAtual=" + getSaldoAtual() + ", saldoInicial="
				+ getSaldoInicial() + ", valor=" + getValorPago() + ", naoIsento='" + isNaoIsento() + '\'' + '}' + "\n";
	}
}

class RP {
	public P receber;
	public P pagar;

	public RP(P receber, P pagar) {
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

class COLOR {
	public static final String FG_BLACK = "\u001B[30m";
	public static final String FG_RED = "\u001B[31m";
	public static final String FG_GREEN = "\u001B[32m";
	public static final String FG_YELLOW = "\u001B[33m";
	public static final String FG_BLUE = "\u001B[34m";
	public static final String FG_PURPLE = "\u001B[35m";
	public static final String FG_CYAN = "\u001B[36m";
	public static final String FG_WHITE = "\u001B[37m";
	public static final String BG_BLACK = "\u001B[40m";
	public static final String BG_RED = "\u001B[41m";
	public static final String BG_GREEN = "\u001B[42m";
	public static final String BG_YELLOW = "\u001B[43m";
	public static final String BG_BLUE = "\u001B[44m";
	public static final String BG_PURPLE = "\u001B[45m";
	public static final String BG_CYAN = "\u001B[46m";
	public static final String BG_WHITE = "\u001B[47m";
}