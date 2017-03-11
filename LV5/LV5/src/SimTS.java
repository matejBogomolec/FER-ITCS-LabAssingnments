import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class SimTS 
{
	public static Set<Stanje> skupStanja = new TreeSet<Stanje>(new Comp()); 
	public static Set<Stanje> prihvatljivaStanja = new TreeSet<Stanje>(new Comp()); 
	public static Stanje pocetnoStanje;
	public static List<String> abeceda = new ArrayList<String>(); 
	public static List<String> trakaZnakovi = new ArrayList<String>(); 
	public static List<String> prijelazi = new ArrayList<String>();
	public static char[] traka;
	public static int indeksGlava;
	private static class Stanje
	{
		private final String ime;
		private Map<Character,Value> prijelaziStanja = new HashMap<Character, Value>();
		public Stanje (String ime)
		{
			this.ime=ime;
		}
		public void postaviPrijelaz(char znakUlaz, Stanje stanje, char znakTraka, char desno) 
		{
			prijelaziStanja.put(znakUlaz, new Value(stanje, znakTraka, desno));
		}
		public String getIme() 
		{
			return ime;
		}
		public Stanje getPrijelazStanje(char znakUlaz)
		{
			return prijelaziStanja.get(znakUlaz).getStanje();
		}
		public char getZnakTraka(char znakUlaz)
		{
			return prijelaziStanja.get(znakUlaz).getZnakTraka();
		}
		public int getPomak(char znakUlaz)
		{
			return prijelaziStanja.get(znakUlaz).getPomak() == 'R'?1:-1;
		}
	}
	public static class Value
	{
		private final Stanje stanje;
		private final char znakTraka;
		private final char pomak;
		public Value (Stanje stanje, char znak, char desno)
			{
				this.stanje = stanje;
				this.znakTraka = znak;
				this.pomak = desno;
			}
		public Stanje getStanje() 
			{
				return stanje;
			}
		public char getZnakTraka() 
		{
				return znakTraka;
		}
		public char getPomak() 
			{
				return pomak;
			}
			
		}
	private static class Comp implements Comparator<Stanje>
	{
		@Override
		public int compare(Stanje o1, Stanje o2) 
		{
			return o1.getIme().compareTo(o2.getIme());
		}	
	}

	public static void main(String[] args) throws IOException 
	{
		BufferedReader reader = new BufferedReader(new FileReader("H:/FER/4.semestar/UTR/LV/test_lab5/test10/test.in"));
		List<String> lines = new ArrayList<String>();
		String line;
		while ((line = reader.readLine()) != null)
	    {
			lines.add(line);     	
	    }
	    reader.close();
	    postaviTS(lines);
	}

	private static void postaviTS(List<String> config) 
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
				case 2: //Znakovi trake
				{
					for (String c : s.split(","))
					{
						trakaZnakovi.add(c);
					}
					break;
				}
				case 3: //Znak prazne celije
				{
					trakaZnakovi.add(s);
					break;
				}
				case 4: //Zapis trake
				{
					traka = new String(s).toCharArray();
					break;
				}
				case 5:  //Prihvatljiva stanja
				{
					for (String c : s.split(","))
					{
						
						if(!c.isEmpty()) prihvatljivaStanja.add(skupStanja.stream().filter(stanje-> stanje.getIme().equals(c)).findFirst().orElse(null));		
					}
					break;
				}
				case 6: //Pocetno stanje
				{
					pocetnoStanje = skupStanja.stream().filter(stanje-> stanje.getIme().equals(s)).findFirst().get();
					break;
				}
				case 7: // Pocetni indeks glave
				{
					indeksGlava = new Integer(s);
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
		simulirajTS();
	}

	private static void simulirajTS() 
	{
		
		Stanje trenutnoStanje = pocetnoStanje;
		do
		{
			//ispis(trenutnoStanje);
			if (!trenutnoStanje.prijelaziStanja.containsKey(traka[indeksGlava])) kraj(trenutnoStanje);
			else if(trenutnoStanje.getPomak(traka[indeksGlava]) + indeksGlava > 69) kraj(trenutnoStanje);
			else if(trenutnoStanje.getPomak(traka[indeksGlava]) + indeksGlava < 0) kraj(trenutnoStanje);
			int pomak = trenutnoStanje.getPomak(traka[indeksGlava]);
			Stanje pom = trenutnoStanje.getPrijelazStanje(traka[indeksGlava]);
			traka[indeksGlava] = trenutnoStanje.getZnakTraka(traka[indeksGlava]);
			indeksGlava += pomak;
			trenutnoStanje = pom;
			//ispis(trenutnoStanje);
		} while(true);
	}

	private static void kraj(Stanje trenutnoStanje) 
	{
		String prihvati = prihvatljivaStanja.contains(trenutnoStanje)?"1":"0";
		System.out.println(trenutnoStanje.getIme()+"|"+indeksGlava+"|"+new String(traka)+"|"+prihvati);
		System.exit(0);
	}
	
	private static void stvoriPrijelaze() 
	{
		for(String s : prijelazi)
		{
			String arg[] = s.split("->",2);
			String lijevo[] = arg[0].split(","); 
			String desno[] = arg[1].split(",");
			skupStanja.stream().filter(x ->x.getIme().equals(lijevo[0])).findFirst().get()
			.postaviPrijelaz(lijevo[1].toCharArray()[0],
					skupStanja.stream().filter(x-> x.getIme().equals(desno[0])).findFirst().get(), desno[1].toCharArray()[0], desno[2].toCharArray()[0]);
		}
	}		
}
