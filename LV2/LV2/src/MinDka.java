import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class MinDka 
{
	public static Set<Stanje> skupStanja = new TreeSet<Stanje>(new Comp()); 
	public static Set<Stanje> prihvatljivaStanja = new TreeSet<Stanje>(new Comp()); 
	public static Set<Stanje> pocetnaStanja = new TreeSet<Stanje>(new Comp()); 
	public static List<String> abeceda = new ArrayList<String>(); 
	public static List<String> prijelazi = new ArrayList<String>();
	private static class Comp implements Comparator<Stanje>
	{
		@Override
		public int compare(Stanje o1, Stanje o2) 
		{
			return o1.getIme().compareTo(o2.getIme());
		}
		
	}
 	private static class Stanje
	{
		private final String ime;
		private int grupa;
		private Map<String, Stanje> prijelaziStanja = new HashMap<String, Stanje>();
		public Stanje (String ime)
		{
			this.ime=ime;
			this.grupa = 0;
		}
		public void postaviPrijelaz(String znak, Stanje stanje) 
		{
			prijelaziStanja.put(znak, stanje);
		}
		public String getIme() 
		{
			return ime;
		}
		 public Stanje getPrijelaz (String znak)
		 {		 
			 if (prijelaziStanja.get(znak) != null) return prijelaziStanja.get(znak);
			 else return null;
		 }
		public int getGrupa() {
			return grupa;
		}
		public void setGrupa(int grupa) {
			this.grupa = grupa;
		}
	}
 	public static void main(String[] args) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
 		
		List<String> lines = new ArrayList<String>();
		String line;
		while ((line = reader.readLine()) != null)
	    {
	    	lines.add(line);     	
	    }
	    reader.close();
	    postaviDka(lines);
	    skupStanja = dohvatljivaStanja(pocetnaStanja);
	    prihvatljivaStanja.retainAll(skupStanja);
	    

		List<Set<Stanje>> podjela = new ArrayList<Set<Stanje>>();
		Set<Stanje> pom = new TreeSet<Stanje>(new Comp());
		pom.addAll(skupStanja);
		pom.removeAll(prihvatljivaStanja);
		podjela.add(pom);
		podjela.add(prihvatljivaStanja);
		podjela = grupirajStanja(podjela);

		minimiziraj(podjela);
		skupStanja = dohvatljivaStanja(pocetnaStanja);
		prihvatljivaStanja.retainAll(skupStanja);
		ispisiDKA();
	}
 	private static void ispisiDKA() 
 	{
 		StringBuilder sb = new StringBuilder();
 		skupStanja.stream().forEachOrdered(s -> sb.append(s.getIme()+","));
 		String rev = sb.reverse().toString().replaceFirst(",", "");
 		sb.delete(0, sb.length());
 		System.out.println(sb.append(rev).reverse().toString());
 		sb.delete(0, sb.length());
 		
 		abeceda.stream().forEachOrdered(s -> sb.append(s+","));
 		rev = sb.reverse().toString().replaceFirst(",", "");
 		sb.delete(0, sb.length());
 		System.out.println(sb.append(rev).reverse().toString());
 		sb.delete(0, sb.length());
 		
 		prihvatljivaStanja.stream().forEachOrdered(s -> sb.append(s.getIme()+","));
 		rev = sb.reverse().toString().replaceFirst(",", "");
 		sb.delete(0, sb.length());
 		System.out.println(sb.append(rev).reverse().toString());
 		sb.delete(0, sb.length());
 		
 		pocetnaStanja.stream().forEachOrdered(s -> sb.append(s.getIme()+","));
 		rev = sb.reverse().toString().replaceFirst(",", "");
 		sb.delete(0, sb.length());
 		System.out.println(sb.append(rev).reverse().toString());
 		sb.delete(0, sb.length());

 		ispisiStanja();
	}
	private static void minimiziraj(List<Set<Stanje>> podjela) 
 	{
		for (Set<Stanje> grupa : podjela)
		{
			if(grupa.size() > 1)
			{
				for (Stanje stanje : skupStanja)
				{
					for (String znak : abeceda)
					{
						if (grupa.contains(stanje.getPrijelaz(znak)))
						{
							stanje.postaviPrijelaz(znak, grupa.stream().findFirst().get());
						}
					}
				}
			}
		}
		for (Set<Stanje> grupa : podjela)
		{
			boolean zastavica = false;
			if(grupa.size() > 1)
			{
				for (Stanje stanje : pocetnaStanja)
				{
					{
						if (grupa.contains(stanje))
						{
							zastavica = true;
							break;
						}
					}
				}
				if(zastavica)
				{
					pocetnaStanja.clear();
					pocetnaStanja.add(grupa.stream().findFirst().get());
					break;
				}
			}
		}
		
	}
	private static void ispisiStanja() 
	{
		for(Stanje stanje : skupStanja)
		{
			{
				for(String znak : abeceda)
				{
					System.out.println(stanje.getIme()+","+znak+"->"+stanje.getPrijelaz(znak).getIme());
				}
			}
		}
	}
	private static List<Set<Stanje>> grupirajStanja(List<Set<Stanje>> podjela)
 	{
 		List<Set<Stanje>> novaPodjela = new ArrayList<Set<Stanje>>();
 		postaviIndekse(podjela);
 		if(podjela.size() <= 1) return podjela;
 		for (Set<Stanje> grupa : podjela)
 		{
 			for(Stanje stanje : grupa)
 			{
 				if (novaPodjela.isEmpty())
 				{
 					Set<Stanje> pom = new TreeSet<Stanje>(new Comp());
 					pom.add(stanje);
 					novaPodjela.add(pom);
 				}
 				else
 				{
 					boolean zastavica = false;
 					for (Iterator<Set<Stanje>> iterator = novaPodjela.iterator(); iterator.hasNext();)
 					{
 						Set<Stanje> novaGrupa = iterator.next();
 						Stanje prvoStanje = novaGrupa.stream().findFirst().get();
 						boolean istaGrupa = true;
 						if (stanje.getGrupa() != prvoStanje.getGrupa()) continue;
 						for (String znak : abeceda)
 						{
 							if (stanje.getPrijelaz(znak).getGrupa() != prvoStanje.getPrijelaz(znak).getGrupa())
 							{
 								istaGrupa = false;
 								break;
 							}
 						}
 						if (istaGrupa)
 						{
 							zastavica = true;
 							novaGrupa.add(stanje);
 						}
 					}
 					if (!zastavica)
 					{
 						Set<Stanje> pom = new TreeSet<Stanje>(new Comp());
 						pom.add(stanje);
 						novaPodjela.add(pom);
 					}
 				}
 			}
 		}
 		postaviIndekse(novaPodjela);
 		if (novaPodjela.size() != podjela.size()) return grupirajStanja(novaPodjela);
 		return novaPodjela;
	}
	private static void postaviIndekse(List<Set<Stanje>> podjela) 
	{
		int indeks = 0;
		for (Set<Stanje> grupa : podjela)
		{
			for(Stanje stanje : grupa)
			{
				stanje.setGrupa(indeks);
			}
			indeks++;
		}
		
	}
	private static Set<Stanje> dohvatljivaStanja(Set<Stanje> trenutnaStanja) 
	{
		Set<Stanje> prijelazi = new TreeSet<Stanje>(new Comp());
		Set<Stanje> pom = new TreeSet<Stanje>(new Comp());
		if (trenutnaStanja.isEmpty()) return trenutnaStanja;
		for(String znak : abeceda)
		{
			for(Stanje stanje : trenutnaStanja)
			{
				prijelazi.add(stanje.getPrijelaz(znak));
			}
		}
		if(prijelazi.isEmpty() || trenutnaStanja.containsAll(prijelazi)) 
			return trenutnaStanja;
		else
		{
			pom.addAll(trenutnaStanja);
			pom.addAll(prijelazi);
			return dohvatljivaStanja(pom);
		}
	}
	private static void postaviDka(List<String> config) 
	{
		int i = 0;
		for (String s : config)
		{
			switch (i)
			{
				case 0: //Stanja
				{
					for (String c : s.split(","))
					{
						skupStanja.add(new Stanje (c));
					}
					break;
				}
				case 1: //Abeceda
				{
					for (String c : s.split(","))
					{
						abeceda.add(c);
					}
					break;
				}
				case 2: // Prihvatljiva stanja
				{
					for (String c : s.split(","))
					{
						if(skupStanja.stream().filter(stanje-> stanje.getIme().equals(c)).findFirst().orElse(null) != null)
						{
						prihvatljivaStanja.add(skupStanja.stream().filter(stanje-> stanje.getIme().equals(c)).findFirst().get());	
						}
					}
					prihvatljivaStanja.removeIf(x -> x == null);
					break;
				}
				case 3: //Pocetno stanje
				{
					pocetnaStanja.add(skupStanja.stream().filter(stanje-> stanje.getIme().equals(s)).findFirst().orElse(null));
					break;
				}
				default: // Prijelazi
				{
					prijelazi.add(s);
					break;
				}
			}
			i++;
		}
		stvoriPrijelaze();
	}
	private static void stvoriPrijelaze() 
	{
		for (String s: prijelazi)
		{
			String arg[] = s.split("->",2);
			String lijevo[] = arg[0].split(","); 
			String desno[] = arg[1].split(",");
			for (String ime : desno)
			{
				if (skupStanja.stream().filter(x -> x.getIme().equals(ime)).findAny().orElse(null) != null)
					skupStanja.stream().filter(x -> x.getIme().equals(lijevo[0])).
					forEachOrdered(x -> x.postaviPrijelaz(lijevo[1],
					skupStanja.stream().filter(y -> y.getIme().equals(ime)).findAny().orElse(null)));
			}

			
		}
		
	}

}
