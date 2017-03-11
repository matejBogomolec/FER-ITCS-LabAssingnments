import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

public class SimPa 
{
	public static Set<Stanje> skupStanja = new TreeSet<Stanje>(new Comp()); 
	public static Set<Stanje> prihvatljivaStanja = new TreeSet<Stanje>(new Comp()); 
	public static Stanje pocetnoStanje;
	public static List<String> abeceda = new ArrayList<String>(); 
	public static List<String> stogZnakovi = new ArrayList<String>(); 
	public static List<String> ulazi = new ArrayList<String>();
	public static List<String> prijelazi = new ArrayList<String>();
	public static Stack<String> stog = new Stack<String>();
	public static String pocetniZnakStog;
	private static class Comp implements Comparator<Stanje>
	{
		@Override
		public int compare(Stanje o1, Stanje o2) 
		{
			return o1.getIme().compareTo(o2.getIme());
		}	
	}
	private static class Value
	{
		private final Stanje stanje;
		private final List<String> znakoviStoga;
		public Value (Stanje stanje, List<String> znakoviStoga)
		{
			this.stanje = stanje;
			this.znakoviStoga = znakoviStoga;
		}
		public Stanje getStanje() 
		{
			return stanje;
		}
		public List<String> getZnakoviStoga() 
		{
			return znakoviStoga;
		}
		
	}
	private static class Key
	{
		private final String key1;
		private final String key2;
		public Key (String key1, String key2)
		{
			this.key1 = key1;
			this.key2 = key2;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key1 == null) ? 0 : key1.hashCode());
			result = prime * result + ((key2 == null) ? 0 : key2.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			Key other = (Key) obj;
			if (key1 == null) 
			{
				if (other.key1 != null) return false;
			} 
			else if (!key1.equals(other.key1)) return false;
			if (key2 == null) 
			{
				if (other.key2 != null) return false;
			} 
			else if (!key2.equals(other.key2)) return false;
			return true;
		}	
	}
	private static class Stanje
	{
		private final String ime;
		private Map<Key,Value> prijelaziStanja = new HashMap<Key, Value>();
		public Stanje (String ime)
		{
			this.ime=ime;
		}
		public void postaviPrijelaz(String znakUlaz, String znakStog, Stanje stanje, List<String> nizStog) 
		{
			prijelaziStanja.put(new Key(znakUlaz, znakStog), new Value(stanje, nizStog));
		}
		public String getIme() 
		{
			return ime;
		}
		public Stanje getPrijelazStanje(String znakUlaz, String znakStog)
		{
			return prijelaziStanja.get(new Key(znakUlaz,znakStog)).getStanje();
		}
		public List<String> getPrijelazNizStog(String znakUlaz, String znakStog)
		{
			return prijelaziStanja.get(new Key(znakUlaz, znakStog)).getZnakoviStoga();
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
     postaviPa(lines);
     simulirajPa();
	}
	private static void simulirajPa() 
	{
		
		for(String ulaz : ulazi)
		{
			Stanje trenutnoStanje = pocetnoStanje;
			boolean fail = false;
			boolean prihvati = false;
			stog.clear();
			stog.push(pocetniZnakStog);
			ispisiConfig(trenutnoStanje);
			for(String znak : ulaz.split(","))
			{
				while(!trenutnoStanje.prijelaziStanja.containsKey(new Key(znak, stog.peek())))
				{
					if(!trenutnoStanje.prijelaziStanja.containsKey(new Key("$", stog.peek())))
					{
						fail = true;
						break;
					}
					else
					{
						Stanje pom  = trenutnoStanje.getPrijelazStanje("$", stog.peek());
						String[] pom1  = trenutnoStanje.getPrijelazNizStog("$", stog.pop()).toArray(new String[0]);
						if(!pom1[0].equals("$"))
							for (int i = pom1.length - 1; i >= 0; i--) stog.push(pom1[i]);
						if(stog.isEmpty() && pom1[0].equals("$")) stog.push(pom1[0]);
						trenutnoStanje = pom;
						ispisiConfig(trenutnoStanje);
					}
				}
				if(fail) break;
				Stanje pom  = trenutnoStanje.getPrijelazStanje(znak, stog.peek());
				String[] pom1  = trenutnoStanje.getPrijelazNizStog(znak, stog.pop()).toArray(new String[0]);
				if(!pom1[0].equals("$"))
					for (int i = pom1.length - 1; i >= 0; i--) stog.push(pom1[i]);
				if(stog.isEmpty() && pom1[0].equals("$")) stog.push(pom1[0]);
				trenutnoStanje = pom;
				ispisiConfig(trenutnoStanje);
			}
			if (prihvatljivaStanja.contains(trenutnoStanje)) prihvati = true;
			else prihvati = false;
			while(trenutnoStanje.prijelaziStanja.containsKey(new Key("$",stog.peek()))
					&& !prihvatljivaStanja.contains(trenutnoStanje))
			{
				Stanje pom = trenutnoStanje.getPrijelazStanje("$", stog.peek());
				String[] pom1  = trenutnoStanje.getPrijelazNizStog("$", stog.pop()).toArray(new String[0]);
				if(!pom1[0].equals("$"))
					for (int i = pom1.length - 1; i >= 0; i--) stog.push(pom1[i]);
				if(stog.isEmpty() && pom1[0].equals("$")) stog.push(pom1[0]);
				trenutnoStanje = pom;
				if(prihvatljivaStanja.contains(trenutnoStanje))
				{
					prihvati = true;
					ispisiConfig(trenutnoStanje);
					break;
				}
				ispisiConfig(trenutnoStanje);
			}
			if (fail || !prihvati)
			{
				if (fail) System.out.print("fail|");
				System.out.println("0");
			}
			else
			{
					System.out.println("1");
			}
		}
	}
	private static void ispisiConfig(Stanje trenutnoStanje) 
	{
		System.out.print(trenutnoStanje.getIme()+"#");
		String[] pom = stog.toArray(new String[0]);
		for(int i = pom.length - 1; i >= 0; i--)
		{
			System.out.print(pom[i]);	
		}
		System.out.print("|");	
	}
	private static void postaviPa(List<String> config) 
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
				case 3: //Znakovi stoga
				{
					for (String c : s.split(","))
					{
						stogZnakovi.add(c);
					}
					break;
				}
				case 4:  //Prihvatljiva stanja
				{
					for (String c : s.split(","))
					{
						
						if(!c.isEmpty()) prihvatljivaStanja.add(skupStanja.stream().filter(stanje-> stanje.getIme().equals(c)).findFirst().orElse(null));		
					}
					break;
				}
				case 5: //Pocetno stanje
				{
					pocetnoStanje = skupStanja.stream().filter(stanje-> stanje.getIme().equals(s)).findFirst().get();
					break;
				}
				case 6: // Pocetni znak stoga
				{
					pocetniZnakStog = stogZnakovi.stream().filter(znak -> znak.equals(s)).findFirst().get();
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
		stogZnakovi.add("$");
		stvoriPrijelaze();	
	}
	private static void stvoriPrijelaze() 
	{
		for (String s : prijelazi)
		{
			String arg[] = s.split("->",2);
			String lijevo[] = arg[0].split(","); 
			String desno[] = arg[1].split(",");
			List<String> pom = new ArrayList<String>();
			for(char znak : desno[1].toCharArray())
			{
				pom.add(String.valueOf(znak));
			}
			skupStanja.stream().filter(x ->x.getIme().equals(lijevo[0])).findFirst().get()
			.postaviPrijelaz(lijevo[1], lijevo[2],
					skupStanja.stream().filter(x ->x.getIme().equals(desno[0])).findFirst().get(), pom);
		}
	}
}