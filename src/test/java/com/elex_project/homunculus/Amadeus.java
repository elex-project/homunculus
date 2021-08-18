/*
 * Project Homunculus
 *
 * Copyright (c) 2018-2021. Elex. All Rights Reserved.
 * https://www.elex-project.com/
 */

package com.elex_project.homunculus;

import java.io.File;
import java.io.IOException;

public final class Amadeus {
	public static void main(String[] args) throws IOException {
		//launch(args);
		//File folder = new File("C:\\Users\\Elex\\workspace\\idea-projects\\SongWriter's\\resources");
		//File file = new File(folder, "test.mid");

		SongWriter songWriter = new SongWriter();
		//songWriter.set();
		//songWriter.setDice(1,2);
		// songWriter.setDice(2,2);
		//songWriter.prepare();
		songWriter.saveTo(new File("test.mid"));

	}


}
