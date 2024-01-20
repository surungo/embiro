import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
	public static void main(String[] args) {
        double[] nums = {0, 0, 10, -10, -20, -80, 100, 200, 800, -1000, -1300, -1500, -2200, 5000, 6300, 7200, 16500, -30000};
        ArrayList<P> valores = new ArrayList<P>(nums.length);
        for (int i = 0; i < nums.length; i++) {
            P p = new P("pessoa"+i,nums[i],true);
            p.diff=nums[i];
            valores.add(i,p);
        }
        ArrayList<P> valores1 = (ArrayList<P>) valores.stream().filter(t->t.valor!=0).collect(Collectors.toList());
        valores1.sort((o1, o2) -> o1.isMaior(o2)?1:0) ;
        Collections.reverse(valores1);

        Stream.of(valores1).forEach(s -> System.out.println(s.toString()));

        Double sumT = valores.stream().mapToDouble(x -> x.getDiff()).sum();
        System.out.println(sumT);

        ArrayList<RP> lsReceberPagantes = new ArrayList<RP>();
        T.lsResolvidoHardcode(valores1,lsReceberPagantes);

        T.resolve(valores1,lsReceberPagantes);

    }

    public void start(){
        int totalpagantes = 0;
		T t = new T();
		ArrayList<P> lsGeral = t.getLista();
		ArrayList<P> lsPagantes = new ArrayList();
		ArrayList<P> lsPaga = new ArrayList();
		ArrayList<P> lsRecebe = new ArrayList();
		ArrayList<RP> lsReceberPagantes = new ArrayList();
		double total = t.calculaTotalMaisListaPagantes(lsGeral, lsPagantes);
		double vpp = t.calculaDividaIndividual(total, lsGeral, lsPagantes);

		//lsPaga= (ArrayList<P>) lsGeral.stream().filter(pp -> pp.diff < 1).collect(Collectors.toList());
		//lsRecebe= (ArrayList<P>) lsGeral.stream().filter(pp -> pp.diff > 1).collect(Collectors.toList());
		//t.calcula(total,vpp,lsPaga,lsRecebe,lsReceberPagantes);

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

    public static void resolve(ArrayList<P> valores,ArrayList<RP> lsReceberPagantes) {
        double valor_pendente = 0;
        for (P p : valores) {
            valor_pendente += (p.getDiff() > 0) ? valorPendenteReceber(lsReceberPagantes, p)
                    : valorPendentePagar(lsReceberPagantes, p)*-1;
        }
        System.out.println(valor_pendente);
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

	private P getFirst(ArrayList<P> ls) {
		int index = 0;
		P p = ls.get(index);
		return p;
	}*/

	public static double valorPendenteReceber(ArrayList<RP> lsReceberPagantes, P r) {
		double total = 0.0;
		for (RP rp : lsReceberPagantes) {
			if (rp.receber.equals(r)) {
                if(rp.receber.getDiffPlus()>rp.pagar.getDiffPlus()) {
                    total += rp.pagar.getDiffPlus();
                }else{
                    total += rp.receber.getDiff();
                }
			}
		}
		return r.getDiff() - total;
	}

	private static double valorPendentePagar(ArrayList<RP> lsReceberPagantes, P p) {
		double total = 0.0;
		for (RP rp : lsReceberPagantes) {
			if (rp.pagar.equals(p)) {
				total += rp.receber.getDiff();
			}
		}
		return p.diff + total;
	}

    public static void lsResolvidoHardcode(ArrayList<P> valores1, ArrayList<RP> lsReceberPagantes) {
        lsReceberPagantes.add(new RP(0,valores1.get(1),valores1.get(0)));
        lsReceberPagantes.add(new RP(0,valores1.get(2),valores1.get(0)));
        lsReceberPagantes.add(new RP(0,valores1.get(3),valores1.get(0)));
        lsReceberPagantes.add(new RP(0,valores1.get(4),valores1.get(5)));
        lsReceberPagantes.add(new RP(0,valores1.get(4),valores1.get(6)));
        lsReceberPagantes.add(new RP(0,valores1.get(4),valores1.get(7)));
        lsReceberPagantes.add(new RP(0,valores1.get(9),valores1.get(8)));
        lsReceberPagantes.add(new RP(0,valores1.get(10),valores1.get(8)));
        lsReceberPagantes.add(new RP(0,valores1.get(11),valores1.get(12)));
        lsReceberPagantes.add(new RP(0,valores1.get(11),valores1.get(13)));
        lsReceberPagantes.add(new RP(0,valores1.get(15),valores1.get(14)));



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
		System.out.println(" N nome " + Utils.leftPad("Pago", numsize, " ") + " "+Utils.leftPad("Diff", numsize, " ")+ ""+Utils.leftPad("Pend", numsize, " ")+""+Utils.leftPad("Tipo", numsize, " "));
		for (P p : lsGeral) {
			double valor_pendente = (p.diff > 0) ? valorPendenteReceber(lsReceberPagantes, p)
					: valorPendentePagar(lsReceberPagantes, p);

			String tipo = (p.diff > 0) ? " não paga " : " paga ";
			tipo = (p.pagante) ? tipo : " recebe ";
			tipo=Utils.leftPad(tipo, 10, " ");
			String colorBg =(valor_pendente!=0)?COLOR_GREEN_BACKGROUND:COLOR_RED_BACKGROUND;
			String colorFnt = COLOR_BLACK; //(valor_pendente!=0)?COLOR_BLACK:COLOR_BLACK;

			System.out.println(colorFnt+colorBg + Utils.leftPad(String.valueOf(lsGeral.indexOf(p)), 2, " ") + " " + p.nome + " "
					+ Utils.leftPad(String.valueOf(p.valor), numsize, " ") + " "
					+ Utils.leftPad(String.valueOf(p.diff), numsize, " ")
					+ Utils.leftPad(String.valueOf(valor_pendente), numsize, " ") +tipo+ COLOR_BLACK_BACKGROUND
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
    public double getDiffPlus() {
        return Math.sqrt(Math.pow(this.diff, 2));
    }

    public boolean isMaior(P o2) {
        return Math.sqrt(Math.pow(this.diff, 2)) >Math.sqrt(Math.pow(o2.getDiffPlus(), 2));
    }
    @Override
    public String toString() {
        return "P{" +
                "nome='" + nome + '\'' +
                ", valor=" + valor +
                ", diff=" + diff +
                ", pagante='" + pagante + '\'' +
                '}'+"\n";
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