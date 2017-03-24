package com.yyy.nlp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;

public class MyParser {

	public static void main(String[] args) throws FileNotFoundException {
		ParserModel model = null;
		InputStream modelIn = new FileInputStream("en-parser-chunking.bin");
		try {
			model = new ParserModel(modelIn);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}

		Parser parser = ParserFactory.create(model);

		String sentence = "The quick brown fox jumps over the lazy dog .";
		Parse topParses[] = ParserTool.parseLine(sentence, parser, 1);
	}

}
