import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.sound.sampled.Line;

public class StaticCodeAnalysis {

	public static void main(String args[]) throws IOException {
	    System.out.println("Enter the path of the folder where java classes are there");
		Scanner sc = new Scanner(System.in);
		String path = sc.nextLine();
		File folder = new File(path);

		StaticCodeAnalysis analysis = new StaticCodeAnalysis();
		String[] pathOfJavaFiles = analysis.getFileNames(folder);
		analysis.detailsOfJavaClasses(pathOfJavaFiles);

	}

	public String[] getFileNames(File folderPath) throws IOException {

		File[] listOfFiles = folderPath.listFiles();
		String line;
		System.out.println("Total Number of Java Files are:");
		int javaFilesCount = 0;
		String[] pathOfFiles = new String[listOfFiles.length];

		for (File file : listOfFiles) {
			if (file.isFile()) {
				if (file.getName().contains("java")) {
					pathOfFiles[javaFilesCount] = file.getPath();
					javaFilesCount++;

				}
			}
		}
		System.out.println("Total java files are" + " " + javaFilesCount);
		System.out.println();

		return pathOfFiles;
	}

	public void detailsOfJavaClasses(String[] pathofFiles) throws IOException {
		String line;
		int count = -1;
		int totalNumberOfClasses = 0;
		// int functionCount=0;
		while (count < pathofFiles.length - 1) {
			count++;
			if (pathofFiles[count] != null) {
				String[] splitPath = pathofFiles[count].split("\\W");
				String className = splitPath[splitPath.length - 2];
				// String[] name=pathofFiles[count].split(".");
				// System.out.println("Class:" + pathofFiles[count]);
				FileReader fileReader = new FileReader(pathofFiles[count]);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				int numberofbrackets = 0, classCount = 0, packageCount = 0, innerClassCount = 0, outerClassCount = 0,
						interfaceCount = 0, functionCount = 0, lineCount = 0;
				Map<Character, Integer> characterMap = new HashMap<Character, Integer>();
				characterMap.put('{', 0);
				Map<String, Integer> variableMap = new HashMap<String, Integer>();

				while ((line = bufferedReader.readLine()) != null) {
					lineCount++;
					String[] splitIntoWords = line.split("\\W");
					char[] splitIntoCharacters = line.toCharArray();
					List<String> wordList = new ArrayList<String>();
					List<Character> characterList = new ArrayList<Character>();

					for (Character character : splitIntoCharacters) {
						if (character.equals(' ')) {
						} else
							characterList.add(character);
					}
					for (String words : splitIntoWords) {
						if (variableMap.containsKey(words)) {
							variableMap.put(words, variableMap.get(words) + 1);
						}
						wordList.add(words);

					}

					for (Character character : characterList) {
						if (character == '{') {
							characterMap.put('{', characterMap.get(character) + 1);
						}

						if (character == '}') {
							characterMap.put('{', characterMap.get('{') - 1);
						}

					}
					if (wordList.size() > 0) {
						if (wordList.contains("import") && characterList.contains(';'))
							packageCount++;

						if (wordList.contains("class"))
							classCount++;

						if (wordList.contains("class") && characterMap.get('{') == 0)
							outerClassCount++;

						if (wordList.contains("class") && characterMap.get('{') != 0)
							innerClassCount++;

						if (wordList.contains("interface"))
							interfaceCount++;

					}

					for (String word : wordList) {
						if (word.equals("long") || word.equals("short") || word.equals("char") || word.equals("boolean")
								|| word.equals("String") || word.equals("int") || word.equals("double")
								|| word.equals("float") && characterList.equals(';')) {
							String name = wordList.get(wordList.indexOf(word) + 1);
							if (variableMap.containsKey(name)) {
								variableMap.put(name, variableMap.get(name) + 1);
							}
							variableMap.put(name, 1);

						}

					}

					if (characterList.size() > 1)
						if (((characterList.get(characterList.size() - 1) == '{')
								&& (characterList.get(characterList.size() - 2) == ')')
								&& (!characterList.contains(';')) && (!wordList.contains("catch")))
								|| ((characterList.get(characterList.size() - 1) == ')')
										&& (!characterList.contains(';')) && (!wordList.contains("catch")))
								|| ((characterList.contains(')')) && (characterList.contains('('))
										&& (!wordList.contains("catch")) && (wordList.contains("throws"))
										&& (!characterList.contains(';')))) {
							if (!wordList.contains("if") && !wordList.contains("while") && !wordList.contains("for")) {
								functionCount++;
							}

						}

				}

				if (interfaceCount > 0) {
					System.out.println("Class Name: " + className);
					System.out.println("Number of interface are=" + interfaceCount);
					System.out.println("Number of lines of code= " + lineCount);
					System.out.println("Number of imports " + packageCount);

				}

				else if (classCount > 0) {

					System.out.println();
					System.out.println("Class Name: " + className);
					System.out.println("Number of imports " + packageCount);
					System.out.println("Functions are:" + functionCount);
					System.out.println("Number of classes are=" + classCount);
					System.out.println("Number of Inner Classes=" + innerClassCount);
					System.out.println("Number of Outer Classes are=" + outerClassCount);
					System.out.println("Number of lines of code= " + lineCount);
					// System.out.println("Number of functions are: "+
					//functionCount );
					System.out.println("Unused Variables:");
					for (String variable : variableMap.keySet()) {
						if (variableMap.get(variable) < 2) {
							System.out.println(variable);
						}
					}

					System.out.println();

				}

				else {
					System.out.println("not a valid class");
					continue;
				}

			}

		}

	}

}
