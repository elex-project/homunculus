/*
 * Project Homunculus
 *
 * Copyright (c) 2018-2021. Elex. All Rights Reserved.
 * https://www.elex-project.com/
 */

package com.elex_project.homunculus;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.meta.KeySignature;
import com.leff.midi.event.meta.TimeSignature;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

@Slf4j
public class SongWriterTest {

	//@Test
	public void saveMinuetAll() throws IOException {
		MidiFile midiFile = new MidiFile();
		midiFile.setResolution(960);
		midiFile.setType(1);

		MidiTrack[] tracks = new MidiTrack[3];
		tracks[0] = new MidiTrack();//meta
		tracks[1] = new MidiTrack();//right
		tracks[2] = new MidiTrack();//left

		tracks[0].insertEvent(new KeySignature(0, 0, 2, 0));
		tracks[0].insertEvent(new TimeSignature(0, 0, 3, 4,
				TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION));

		File folder = new File("midi");
		folder.mkdirs();
		for (int i = 1; i <= 176; i++) {
			SongWriter.addTo(tracks, midiFile.getResolution(), i, MidiTable.getMinuet(i));
		}

		for (MidiTrack track : tracks) {
			midiFile.addTrack(track);
		}

		File file = new File(folder, "minuet_all.mid");
		midiFile.writeToFile(file);
	}

	//@Test
	public void saveTrioAll() throws IOException {
		MidiFile midiFile = new MidiFile();
		midiFile.setResolution(960);
		midiFile.setType(1);

		MidiTrack[] tracks = new MidiTrack[3];
		tracks[0] = new MidiTrack();//meta
		tracks[1] = new MidiTrack();//right
		tracks[2] = new MidiTrack();//left

		tracks[0].insertEvent(new KeySignature(0, 0, 2, 0));
		tracks[0].insertEvent(new TimeSignature(0, 0, 3, 4,
				TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION));

		File folder = new File("midi");
		folder.mkdirs();
		for (int i = 1; i <= 96; i++) {
			SongWriter.addTo(tracks, midiFile.getResolution(), i, MidiTable.getTrio(i));
		}

		for (MidiTrack track : tracks) {
			midiFile.addTrack(track);
		}

		File file = new File(folder, "trio_all.mid");
		midiFile.writeToFile(file);
	}

	//@Test
	public void saveMinuets() throws IOException {
		File folder = new File("midi");
		folder.mkdirs();
		for (int i = 1; i <= 176; i++) {
			log.debug("Id= " + i);
			MidiFile midiFile = SongWriter.prepareMinuetFile(i);
			File file = new File(folder, "M_" + i + ".mid");
			midiFile.writeToFile(file);
		}
	}

	//@Test
	public void saveTrios() throws IOException {
		File folder = new File("midi");
		folder.mkdirs();
		for (int i = 1; i <= 96; i++) {
			MidiFile midiFile = SongWriter.prepareTrioFile(i);
			File file = new File(folder, "T_" + i + ".mid");
			midiFile.writeToFile(file);
		}
	}
}
