import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Parser 
{
	public static char[] word;
	public static int index = 0;
	public static String izlaz = new String();
	public static void main(String[] args) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		word = reader.readLine().toCharArray();
		reader.close();
		S();
		if (index != word.length) NE();
		else DA();
	}

	private static void S() 
	{
		izlaz+="S";
		if(index == word.length) NE();
		switch (word[index])
		{
			case 'a':
			{
				index ++;
				A();
				B();
				break;
			}
			case 'b':
			{
				index ++;
				B();
				A();
				break;
			}
			default :
			{
				NE();
				break;
			}
		}
	}
	private static void A() 
	{
		izlaz+='A';
		if(index == word.length) NE();
		switch (word[index])
		{
			case 'a':
			{
				index++;
				break;
			}
			case 'b' :
			{
				index++;
				C();
				break;
			}
			default:
			{
				NE();
				break;
			}
		}	
	}
	private static void B() 
	{
		izlaz+='B';
		if(index == word.length) return;
		switch (word[index])
		{
			case 'c':
			{
				index++;
				if(index == word.length) NE();
				else if (word[index] != 'c') NE();
				index++;
				S();
				if(index == word.length) NE();
				else if (word[index] != 'b') NE();
				index++;
				if(index == word.length) NE();
				else if (word[index] != 'c') NE();
				index++;
				break;
			}
			default:
			{
				return;
			}
		}	
	}
	private static void C()
	{
		izlaz+="C";
		A();
		A();
	}

	private static void DA() 
	{
		System.out.println(izlaz);
		System.out.println("DA");
		System.exit(0);
	}

	private static void NE() 
	{
		System.out.println(izlaz);
		System.out.println("NE");
		System.exit(0);
	}
}
