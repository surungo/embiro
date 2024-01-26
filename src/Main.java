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
	public void v1(){
		int totalpagantes = 0;
		Tool t = new Tool();
		ArrayList<P> lsGeral = t.getLista();
		ArrayList<P> lsPagantes = new ArrayList();
		ArrayList<P> lsPaga = new ArrayList();
		ArrayList<P> lsRecebe = new ArrayList();
		ArrayList<RP> lsReceberPagantes = new ArrayList();
		double total = t.calculaTotal(lsGeral, lsPagantes);
		double vpp = t.calculaDividaPorPessoa(total, lsGeral, lsPagantes);
		//lsPaga= (ArrayList<P>) lsGeral.stream().filter(pp -> pp.diff < 1).collect(Collectors.toList());
		//lsRecebe= (ArrayList<P>) lsGeral.stream().filter(pp -> pp.diff > 1).collect(Collectors.toList());
		//t.calcula(total,vpp,lsPaga,lsRecebe,lsReceberPagantes);
		ArrayList<P> lsAux = t.calcularReceberPagantesIgual(lsGeral, lsReceberPagantes);
		lsAux = t.calcularReceberPagantesSomaIgual(lsAux, lsReceberPagantes);
		t.print(total, vpp, lsGeral, lsPagantes, lsReceberPagantes);
	}
	public void v2(){
		Tool tool = new Tool();
		ArrayList<P> lsGeral = tool.getLista();
		ArrayList<P> lsNaoIsentos = new ArrayList();
		ArrayList<RP> lsReceberPagar = new ArrayList();
		double total = tool.calculaTotal(lsGeral, lsNaoIsentos);
		double vpp = tool.calculaDividaPorPessoa(total, lsGeral, lsNaoIsentos);
		//lsReceberPagar.add(new RP(lsGeral.get(1),lsGeral.get(0)));
		//lsReceberPagar.add(new RP(lsGeral.get(3),null));
		//lsReceberPagar.add(new RP(null,lsGeral.get(3)));
		ArrayList<P> lsGeralAtual = (ArrayList<P>) lsGeral.stream().filter(t->t.getSaldoInicial()!=0).collect(Collectors.toList());
		tool.print( total, vpp, lsGeral, lsNaoIsentos, lsReceberPagar);
		tool.resolver(lsGeralAtual,lsReceberPagar,1);
		tool.print( total, vpp, lsGeralAtual, lsNaoIsentos, lsReceberPagar);
	}
}
class Tool {
	public Tool() {
	}
    public void resolver(ArrayList<P> p_lsGeralAtual, ArrayList<RP> lsReceberPagantes,int qta) {
        if(p_lsGeralAtual.isEmpty())return;
		Collections.sort(p_lsGeralAtual, Comparator.comparingDouble(P::getSaldoAtualPositivo));
		Collections.reverse(p_lsGeralAtual);
		if(hasParSozinho(lsReceberPagantes)){
			RP rp = pegarSozinho(lsReceberPagantes);
			P p = pegarSozinho(rp);
			if(p!=null) {
				for(int i=0;i <qta;i++){
					P parceiro=extrairParceiro(p_lsGeralAtual,p);
					if(parceiro!=null) {
						if (p.isReceber()) {
							rp.pagar = parceiro;
							rp.receber.setSaldoAtual(rp.receber.getSaldoAtual()+rp.pagar.getSaldoAtual());
						} else {
							rp.receber = parceiro;
							rp.pagar.setSaldoAtual(rp.pagar.getSaldoAtual()+rp.receber.getSaldoAtual());
						}

					}else{
						lsReceberPagantes.remove(rp);
						p_lsGeralAtual.add(p);
					}
				}
			}else{
				System.out.println("erro no tem sozinho");
			}
		}else {
			P p = extrairPrimeiro(p_lsGeralAtual);
			if(p.isReceber()){
				lsReceberPagantes.add(new RP(p,null));
			}else{
				lsReceberPagantes.add(new RP(null,p));
			}
			resolver(p_lsGeralAtual,lsReceberPagantes,qta);
		}
        /*for (P p : lsGeralAtual) {
            valor_pendente += (p.getSaldoInicial() > 0) ? valorPendenteReceber(lsReceberPagantes, p)
                    : valorPendentePagar(lsReceberPagantes, p)*-1;
        }
        System.out.println(valor_pendente);*/
    }

	private P extrairParceiro(ArrayList<P> pLsGeralAtual, P p) {
		P parceiro = pLsGeralAtual.stream().filter( o -> p.getSaldoAtual()+o.getSaldoAtual()==0).collect(Collectors.toList()).get(0);
		pLsGeralAtual.remove(parceiro);
		return parceiro;
	}

	private P pegarSozinho(RP rp) {
		if(rp!=null) {
			if (rp.pagar == null) {
				return rp.receber;
			} else {
				return rp.pagar;
			}
		}
		return null;

	}

	private RP pegarSozinho(ArrayList<RP> lsReceberPagantes) {
		if(hasParSozinho(lsReceberPagantes)) {
			return (RP) lsReceberPagantes.stream().filter(rp -> !(rp.receber != null && rp.pagar != null)).collect(Collectors.toList()).get(0);
		}else{
			return null;
		}
	}

	private boolean hasParSozinho(ArrayList<RP> lsReceberPagantes) {
		if(lsReceberPagantes.isEmpty())return false;
		lsReceberPagantes = (ArrayList<RP>) lsReceberPagantes.stream().filter(rp -> !(rp.receber != null && rp.pagar !=null) ).collect(Collectors.toList());
		if(lsReceberPagantes.isEmpty())return false;
		return true;
	}
	/*public void calcula(double total, double vpp, ArrayList<P> lsPaga, ArrayList<P> lsRecebe, ArrayList<RP> lsReceberPagantes) {
		Double sumPaga   = lsPaga.stream().mapToDouble(x -> x.getDiff()).sum();
		Double sumRecebe = lsRecebe.stream().mapToDouble(x -> x.getDiff()).sum();
		if(sumRecebe != 0 || sumPaga != 0) {
			Collections.sort(lsPaga, Comparator.comparingDouble(P::getDiff));
			Collections.sort(lsRecebe, Comparator.comparingDouble(P::getDiff));
			Collections.reverse(lsRecebe);
			P recebeP = getFirst(lsRecebe);
			P pagoP = getFirst(lsPaga);
			if(recebeP.getDiff()+pagoP.getDiff()>0){
				RP rp = new RP(pagoP.getDiff()*-1,recebeP,pagoP);
			}
		}

		System.out.println("n");
	}
*/
	private P extrairPrimeiro(ArrayList<P> ls) {
		int index = 0;
		P p = ls.get(index);
		ls.remove(p);
		return p;
	}
	public static double valorPendenteReceber(ArrayList<RP> lsReceberPagantes, P r) {
		double total = 0.0;
		for (RP rp : lsReceberPagantes) {
			if (rp.receber.equals(r)) {
                if(rp.receber.getSaldoInicialPositivo()>rp.pagar.getSaldoInicialPositivo()) {
                    total += rp.pagar.getSaldoInicialPositivo();
                }else{
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
    public static void lsResolvidoHardcode(ArrayList<P> valores1, ArrayList<RP> lsReceberPagantes) {
        lsReceberPagantes.add(new RP(valores1.get(1),valores1.get(0)));
        lsReceberPagantes.add(new RP(valores1.get(2),valores1.get(0)));
        lsReceberPagantes.add(new RP(valores1.get(3),valores1.get(0)));
        lsReceberPagantes.add(new RP(valores1.get(4),valores1.get(5)));
        lsReceberPagantes.add(new RP(valores1.get(4),valores1.get(6)));
        lsReceberPagantes.add(new RP(valores1.get(4),valores1.get(7)));
        lsReceberPagantes.add(new RP(valores1.get(9),valores1.get(8)));
        lsReceberPagantes.add(new RP(valores1.get(10),valores1.get(8)));
        lsReceberPagantes.add(new RP(valores1.get(11),valores1.get(12)));
        lsReceberPagantes.add(new RP(valores1.get(11),valores1.get(13)));
        lsReceberPagantes.add(new RP(valores1.get(15),valores1.get(14)));
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
							if (lsGeral.indexOf(p) < lsGeral.indexOf(p0) && p0.getSaldoInicial() < 0 && !p.getNome().equals(p0.getNome())) {
								if (r.getSaldoInicial() + (p.getSaldoInicial() + p0.getSaldoInicial()) == 0) {
									boolean exists = false;
									for (RP rp : lsReceberPagantes) {
										if (rp.receber.getNome().equals(r.getNome())
												&& (rp.pagar.getNome().equals(p.getNome()) || rp.pagar.getNome().equals(p0.getNome()))) {
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
					if (lsGeral.indexOf(r) < lsGeral.indexOf(p) && p.getSaldoInicial() < 0 && r.getSaldoInicial() + p.getSaldoInicial() == 0) {
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
	public void print(double total, double vpp, ArrayList<P> p_lsGeral, ArrayList<P> lsPagantes,
                             ArrayList<RP> lsReceberPagantes) {

		ArrayList<P> lsGeral = (ArrayList<P>) p_lsGeral.clone();

		int numsize = 7;
		int totalpagantes = lsPagantes.size();
        String colorBg  = COLOR.BG_BLACK;
        String colorFnt = COLOR.FG_GREEN;

        System.out.println(colorFnt+colorBg+"total pag: " + totalpagantes + "  total: "
				+ Utils.leftPad(String.valueOf(total), numsize, " ") + "  pp: " + vpp);
		colorBg  = COLOR.BG_WHITE;
		colorFnt = COLOR.FG_BLACK;
		System.out.println(colorFnt+colorBg+
                " N|nome|" +
                        Utils.leftPad("ValPago", numsize, " ") + "|"+
                        Utils.leftPad("SalInit", numsize, " ") + "|"+
                        Utils.leftPad("SalAtua", numsize, " ") +"|"+
                        Utils.leftPad("Tipo", numsize, " ")+"|"
		);
        colorBg  = COLOR.BG_BLACK;
        colorFnt = COLOR.FG_WHITE;

        for (P p : lsGeral) {
			String tipo = (p.getSaldoInicial() > 0) ? " recebe" : " paga";
			tipo = (p.isNaoIsento()) ? tipo : " isento";

			System.out.println(colorFnt+colorBg +
					Utils.leftPad(String.valueOf(lsGeral.indexOf(p)), 2, " ") + "|" +
					p.getNome() + "|" +
					Utils.leftPad(String.valueOf(p.getValorPago()), numsize, " ") + "|" +
					Utils.leftPad(String.valueOf(p.getSaldoInicial()), numsize, " ") + "|" +
					Utils.leftPad(String.valueOf(p.getSaldoAtual()), numsize, " ") + "|" +
					Utils.leftPad(tipo, numsize, " ") + "|");
		}
		colorBg  = COLOR.BG_WHITE;
		colorFnt = COLOR.FG_BLACK;

		System.out.println("");
        System.out.println(colorFnt+colorBg +" N|"+
				"NmRc|" +
				Utils.leftPad("vlARc", numsize, " ") +"|"+
				Utils.leftPad("vlIRc", numsize, " ") +"|"+
				"NmPg|" +
				Utils.leftPad("vlAPg", numsize, " ") +"|"+
				Utils.leftPad("vlIPg", numsize, " ") +"|"
		) ;
		colorBg  = COLOR.BG_BLACK;
		colorFnt = COLOR.FG_WHITE;
        for (RP rp : lsReceberPagantes) {
			P receber = ((rp.receber!=null)?rp.receber:new P("    ",0,false));
			P pagar =   ((rp.pagar!=null)?rp.pagar:new P("    ",0,false));
			System.out.println(colorFnt+colorBg +
					Utils.leftPad(String.valueOf(lsReceberPagantes.indexOf(rp)), 2, " ") + "|" +
					receber.getNome() + "|" +
					Utils.leftPad(String.valueOf(receber.getSaldoInicial()), numsize, " ") + "|" +
					Utils.leftPad(String.valueOf(receber.getSaldoAtual()), numsize, " ") + "|" +
					pagar.getNome() + "|" +
					Utils.leftPad(String.valueOf(pagar.getSaldoAtual()), numsize, " ") + "|" +
					Utils.leftPad(String.valueOf(pagar.getSaldoAtual()), numsize, " ") + "|"
			);
		}
	}

	public static double calculaDividaPorPessoa(double valorTotal, ArrayList<P> lsGeral, ArrayList<P> lsNaoIsentos) {
		int totalNaoIsentos = lsNaoIsentos.size();
		double valorPorPessoa = Utils.round(valorTotal / totalNaoIsentos);
		for (P p : lsGeral) {
			if (p.isNaoIsento()) {
				p.setSaldoInicial( Utils.round(p.getValorPago() - valorPorPessoa));
			} else {
				p.setSaldoInicial( Utils.round(p.getValorPago()));
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
		//lista.addAll(getLista1());
		//lista.addAll(getLista2());
		//lista.addAll(getLista3());

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
        return getSaldoInicial() >o2.getSaldoInicial();
    }
    public boolean isSaldoInicialPositvoMaior(P o2) {
        return getSaldoInicialPositivo() >o2.getSaldoInicialPositivo();
    }
    public boolean isSaldoAtualMaior(P o2) {
        return getSaldoAtual() >o2.getSaldoAtual();
    }
    public boolean isSaldoAtualPositvoMaior(P o2) {
        return getSaldoAtualPositivo() >o2.getSaldoAtualPositivo();
    }
    public boolean isReceber(){
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
        return "P{" +
                "nome='" + getNome() + '\'' +
                ", naoIsento='" + isNaoIsento() + '\'' +
                ", valor=" + getValorPago() +
                ", saldoInicial=" + getSaldoInicial() +
                ", saldoAtual=" + getSaldoAtual() +
                '}'+"\n";
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