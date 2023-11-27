import java.util.ArrayList;

public class Main {
	public static void main(String[] args) {
		int totalpagantes = 0;
		T t = new T();
		ArrayList<P> lsGeral = t.getLista();
		ArrayList<P> lsPagantes = new ArrayList();
		ArrayList<RP> lsReceberPagantes = new ArrayList();
		double total = t.calculaTotalMaisListaPagantes(lsGeral, lsPagantes);
		double vpp = t.calculaDividaIndividual(total, lsGeral, lsPagantes);
		ArrayList<P> lsAux =t.calcularReceberPagantesIgual(lsGeral, lsReceberPagantes);
		lsAux = t.calcularReceberPagantesSomaIgual(lsAux, lsReceberPagantes);

		t.print(total, vpp, lsGeral, lsPagantes,lsReceberPagantes);

	}
}

class T {
	public T() {
	}

	public ArrayList<P> calcularReceberPagantesSomaIgual(ArrayList<P> lsGeral, ArrayList<RP> lsReceberPagantes) {
		ArrayList<P> lsAux = (ArrayList<P>) lsGeral.clone();
		for (P r : lsGeral) {
			if(r.diff>0) {
				for (P p : lsGeral) {
					if(lsGeral.indexOf(r)<lsGeral.indexOf(p) &&
							p.diff<0) {
						for (P p0 : lsGeral) {
							if(lsGeral.indexOf(p)<lsGeral.indexOf(p0) &&
									p0.diff<0 && 
									!p.nome.equals(p0.nome)) {
								if(r.diff+(p.diff+p0.diff)==0) {
									boolean exists = false;
									for(RP rp : lsReceberPagantes) {
										if(rp.receber.nome.equals(r.nome)&&
												(rp.pagar.nome.equals(p.nome)||
												rp.pagar.nome.equals(p0.nome))) {
											exists=true;
											break;
										}
									}
									if(!exists) {
										lsReceberPagantes.add(new RP(r.diff+p.diff,r,p));
										lsReceberPagantes.add(new RP(0,r,p0));
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
			if(r.diff>0) {
				for (P p : lsGeral) {
					if(lsGeral.indexOf(r)<lsGeral.indexOf(p) &&
							p.diff<0 && 
							r.diff+p.diff==0) {
						boolean exists = false;
						for(RP rp : lsReceberPagantes) {
							if(rp.pagar.nome.equals(p.nome)) {
								exists=true;
								break;
							}
						}
						if(!exists) {
							lsReceberPagantes.add(new RP(0,r,p));
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

	public void print(double total, double vpp, ArrayList<P> lsGeral, ArrayList<P> lsPagantes,ArrayList<RP> lsReceberPagantes) {
		int numsize = 6;
		int totalpagantes = lsPagantes.size();
		System.out.println("total pag: " + totalpagantes + "  total: " + Utils.leftPad(String.valueOf(total), numsize, " ") + "  pp: " + vpp);
		for (P p : lsGeral) {
			System.out.println(lsGeral.indexOf(p)+" "+p.nome + " " + Utils.leftPad(String.valueOf(p.valor), numsize, " ") + " "
					+ Utils.leftPad(String.valueOf(p.diff), numsize, " "));
		}
		for (RP rp : lsReceberPagantes) {
			System.out.println(lsReceberPagantes.indexOf(rp)+" "
					+rp.receber.nome + " " 
					+ Utils.leftPad(String.valueOf(rp.receber.diff), numsize, " ") + " "
					+ rp.pagar.nome + " " 
					+ Utils.leftPad(String.valueOf(rp.pagar.diff), numsize, " ") + " "
					+ Utils.leftPad(String.valueOf(rp.valor), numsize, " ")
					);
		}
	}

	public double calculaDividaIndividual(double total, ArrayList<P> lsGeral, ArrayList<P> lsPagantes) {
		int totalpagantes = lsPagantes.size();
		double vpp = Utils.round(total / totalpagantes);
		for (P p : lsGeral) {
			if (!p.pagante) {
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
			if (!p.pagante) {
				lsPagentes.add(p);
			}
			total += p.valor;
		}
		return Utils.round(total);
	}

	public ArrayList<P> getLista() {
		ArrayList<P> lista = new ArrayList();
		lista.add(new P("bos0", 500.00, true));
		lista.add(new P("max0", 700.00, false));
		//lista.add(new P("max1",1000.00, false));
		lista.add(new P("med0", 300.00, false));
		//lista.add(new P("med1", 400.00, false));
		lista.add(new P("bai0", 100.00, false));
		//lista.add(new P("bai1", 200.00, false));
		lista.add(new P("nad0", 0.00, false));
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
}

class RP {
	public double valor;
	public P receber;
	public P pagar;

	public RP(double valor, P receber, P pagar) {
		this.valor = Utils.round(valor);
		this.receber = receber;
		this.pagar = pagar;
	}
}

class Utils{
	public static final String leftPad(String valor,int size,String preenchimento) {
		String aux="";
		if(valor.length()>size)return valor;
		for(int i=0;i<size;i++)aux+=preenchimento;
		return aux.substring(valor.length()) + valor;
	}
	public static final double round(double valor) {
		return Math.round(valor);//Math.round(valor * 10) / 10;
	}
}