import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class SimEnka 
{
	public static Set<Stanje> skupStanja = new TreeSet<Stanje>(new Comp()); 
	public static Set<Stanje> prihvatljivaStanja = new TreeSet<Stanje>(new Comp()); 
	public static Set<Stanje> pocetnaStanja = new TreeSet<Stanje>(new Comp()); 
	public static List<String> abeceda = new ArrayList<String>(); 
	public static List<String> ulazi = new ArrayList<String>();
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
		private Map<String, Set<Stanje>> prijelaziStanja = new HashMap<String, Set<Stanje>>();
		public Stanje (String ime)
		{
			this.ime=ime;
		}
		public void postaviPrijelaz(String znak, Set<Stanje> stanja) 
		{
			prijelaziStanja.put(znak, stanja);
		}
		public String getIme() 
		{
			return ime;
		}
		 public Set<Stanje> getPrijelaz (String znak)
		 {
			 Set<Stanje> prijelaz = new TreeSet<Stanje>(new Comp());		 
			 if (prijelaziStanja.get(znak) != null) prijelaz.addAll(prijelaziStanja.get(znak));
			 return prijelaz;
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
	        postaviEnka(lines);
	        simulacijaEnka();

	}
	private static void simulacijaEnka() 
	{
		
		for (String ulaz : ulazi)
		{
			StringBuilder isb = new StringBuilder();
			String izlaz = "";
			Set<Stanje> trenutnaStanja = new TreeSet<Stanje>(new Comp());
			trenutnaStanja.addAll(e_okr(pocetnaStanja));
			trenutnaStanja.stream().forEachOrdered((x -> isb.append(x.getIme()+",")));
			String iRev = isb.reverse().toString().replaceFirst(",", "");
			isb.delete(0, isb.length());
			izlaz += isb.append(iRev).reverse().toString();
			
			for (String znak : ulaz.split(","))
			{
				StringBuilder sb = new StringBuilder();
				Set<Stanje> eOkr = new TreeSet<Stanje>(new Comp());
				Set<Stanje> prijelaz = new TreeSet<Stanje>(new Comp());
				Set<Stanje> pom =  new TreeSet<Stanje>(new Comp());
				izlaz += '|';
				eOkr.addAll(e_okr(trenutnaStanja));
				for (Stanje stanje : eOkr) prijelaz.addAll(stanje.getPrijelaz(znak));
				pom.addAll(e_okr(prijelaz));
				if (pom.isEmpty()) izlaz += "#";
				else
				{
					pom.stream().forEachOrdered((x -> sb.append(x.getIme()+",")));
					String rev = sb.reverse().toString().replaceFirst(",", "");
					sb.delete(0, sb.length());
					izlaz += sb.append(rev).reverse().toString();
				}
				trenutnaStanja = pom;
			}
			System.out.println(izlaz);
			
			
		}
		
	}
	private static Set<Stanje> e_prijelaz(Set<Stanje> trenutnaStanja)
	{
		Set<Stanje> prijielaz = new TreeSet<Stanje>(new Comp());
		for (Stanje stanje : trenutnaStanja) prijelaz.addAll(stanje.getPrijelaz("$"));
		return prijelaz;
	}
	private static Set<Stanje> e_okr(Set<Stanje> trenutnaStanja) 
	{
		Set <Stanje> pom = new TreeSet<Stanje>(new Comp());

		if (trenutnaStanja.isEmpty()) return trenutnaStanja;
		else if (trenutnaStanja.containsAll(e_prijelaz(trenutnaStanja))) return trenutnaStanja;
		else if (e_prijelaz(trenutnaStanja).isEmpty()) return trenutnaStanja;		
		else
		{
			pom.addAll(e_prijelaz(trenutnaStanja));
			pom.addAll(e_okr(pom));
			pom.addAll(trenutnaStanja);
			return pom;
		}	
	}
	private static void postaviEnka(List<String> config) 
	{
		int i = 0;
		for (String s : config)
		{
			switch (i)
			{
				case 0: //Ulazi
				{
					for (String c : s.split("\\|"))
					{
						ulazi.add(c);
					}
					break;
				}
				case 1: //Stanja
				{
					for (String c : s.split(","))
					{
						skupStanja.add(new Stanje (c));
					}
					break;
				}
				case 2: //Abeceda
				{
					for (String c : s.split(","))
					{
						abeceda.add(c);
					}
					break;
				}
				case 3: // Prihvatljiva stanja
				{
					for (String c : s.split(","))
					{
						prihvatljivaStanja.add(skupStanja.stream().filter(stanje-> stanje.getIme().equals(c)).findFirst().orElse(null));		
					}
					prihvatljivaStanja.removeIf(x -> x == null);
					break;
				}
				case 4: //Pocetno stanje
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
		abeceda.add("$");
		stvoriPrijelaze();
	}
	private static void stvoriPrijelaze() 
	{
		for (String s: prijelazi)
		{
			String arg[] = s.split("->",2);
			String lijevo[] = arg[0].split(","); 
			String desno[] = arg[1].split(",");
			Set <Stanje> pom = new TreeSet<Stanje>(new Comp());
			for (String ime : desno)
			{
				if (skupStanja.stream().filter(x -> x.getIme().equals(ime)).findAny().orElse(null) != null)
				pom.add(skupStanja.stream().filter(x -> x.getIme().equals(ime)).findAny().orElse(null));
			}

			skupStanja.stream().filter(x -> x.getIme().equals(lijevo[0])).forEachOrdered(x -> x.postaviPrijelaz(lijevo[1], pom));
		}
//	System.out.println("ENKA postavljen");
		
	}

}
